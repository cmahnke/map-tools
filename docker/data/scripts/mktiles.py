#!/usr/bin/env python

import json, argparse, logging, sys, shlex, subprocess, re, zipfile, os, shutil, math
from pathlib import Path
from urllib.parse import urlparse
from io import StringIO
import requests
from dotmap import DotMap

default_log_level = logging.WARN

def load_config(cfg):
    with open(cfg, 'r') as file:
        if cfg.lower().endswith('.toml'):
            import toml
            return DotMap(toml.safe_load(file))
        elif cfg.lower().endswith('.yml') or cfg.lower().endswith('.yaml'):
            import yaml
            return DotMap(yaml.safe_load(file))
        else:
            raise NotImplementedError()

def load_urls(filename, ignore_config=True):
    global logger, config
    with open(filename) as file:
        try:
            data = json.load(file)
        except json.decoder.JSONDecodeError:
            logger.error(f"Faild to load {filename}, skipping")
            return []
        except RecursionError:
            logger.error(f"Faild to load {filename}, it seems to include itself, skipping")
            return []
    if 'urls' in data:
        urls = data['urls']
    else:
        urls = []
    if 'files' in data:
        path = Path(filename).parent
        if isinstance(data['files'], str):
            urls.extend(load_urls(path.joinpath(data['files'])))
        elif isinstance(data['files'], list):
            for f in data['files']:
                urls.extend(load_urls(path.joinpath(f)))
        else:
            logger.warn(f"Can't handle type {str(type(f))}")
    if not ignore_config and "config" in data:
        for key, value in data["config"].items():
            logger.info(f"Patching config at {key} with {value}")
            setattr(config, key, value)
    return urls

def download(url, dest):
    global logger
    if dest.is_file():
        size = int(requests.head(url).headers['Content-length'])
        if size == dest.stat().st_size:
            return
    logger.debug(f"Downloading {url} to {dest}")
    response = requests.get(url, stream=True)
    size = int(response.headers['Content-length'])
    byte_per_ten_percent = math.floor(size / 10)
    chunk_size = 10 * 1024
    bytes_written = 0
    with open(dest, mode="wb") as file:
        for chunk in response.iter_content(chunk_size=chunk_size):
            file.write(chunk)
            bytes_written += chunk_size
            if bytes_written >= byte_per_ten_percent:
                logger.debug(f"Downloaded {math.floor(os.path.getsize(dest) / byte_per_ten_percent)}0% of {url}")
                bytes_written = 0

def get_datasets():
    global logger, config
    for dataset in config.datasets.values():
        target = Path(dataset.path)
        target.mkdir(parents=True, exist_ok=True)
        if "postprocess" in dataset and dataset.postprocess != "":
            target = Path(str(target) + '.tmp')
            target.mkdir(parents=True, exist_ok=True)

        if not list(target.iterdir()):
            filename = Path(urlparse(dataset.url).path).name
            dest = Path(target).joinpath(filename)
            download(dataset.url, dest)
            with zipfile.ZipFile(dest, 'r') as zip:
                for zip_info in zip.infolist():
                    if zip_info.is_dir():
                        continue
                    zip_info.filename = Path(zip_info.filename).name
                    zip.extract(zip_info, target)
            if "postprocess" in dataset and dataset.postprocess != "":
                if isinstance(dataset.postprocess, list):
                    for pp in dataset.postprocess:
                        run_external_with_logger(pp, logger, target)
                else:
                    run_external_with_logger(dataset.postprocess, logger, target)
                if not config.keep:
                    shutil.rmtree(target)
            if not config.keep:
                dest.unlink(missing_ok=True)
        else:
            logger.info(f"Directory {target} already populated")

def run_external_with_logger(cmd, log, input = ''):
    if cmd.find('{') < cmd.find('}'):
        cmd = eval('f"' + cmd + '"', {}, {"pwd": Path.cwd(), "pwd.parent": Path.cwd().parent, "input_dir": input})
        log.debug(f"Rewritten command is {cmd}")
    args = shlex.split(cmd)
    logger.debug(f"Running command is {args}")
    process = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
    for stdout_line in iter(process.stdout.readline, ""):
        logger.debug(stdout_line.strip())
    process.stdout.close()
    ret = process.wait()
    if ret:
        raise subprocess.CalledProcessError(ret, cmd)

def merge(urls):
    #TODO: Implement merge as a single command call
    global logger, config
    previous = ''
    out_file = Path(config.directories.work).joinpath('output.osm.pbf')
    in_file = Path(config.directories.work).joinpath('input.osm.pbf')
    dest = [];
    for i in range(len(urls)):
        url = urls[i]
        filename = Path(urlparse(url).path).name
        prefix = config.directories.download
        dest.append(Path(prefix).joinpath(filename))
        dest[i].parent.mkdir(parents=True, exist_ok=True)
        download(url, dest[i])
        if previous != '':
            if urls[i] == urls[-1]:
                out_file = Path(config.commands.osmium.merge.path)
                logger.debug(f"Writing last file to {out_file}, {config.commands.osmium.merge.path}")
            cmd = ' '.join([config.commands.osmium.merge.cmd, config.commands.osmium.merge.opts, f"{previous} {dest[i]} -o {out_file}"])
            try:
                run_external_with_logger(cmd, logger)
                if not config.keep:
                    in_file.unlink(missing_ok=True)
                else :
                    in_file.rename(str(infile) + f".{i}")
                logger.debug(f"Size of result file {out_file} is {os.path.getsize(out_file)}")
                if urls[i] != urls[-1]:
                    out_file.rename(in_file)
                    previous = in_file
            except subprocess.CalledProcessError as cpe:
                logger.error(f"Failed to run {cpe.cmd}")
                raise cpe
            except FileNotFoundError as fnfe:
                logger.error(f"Can't find result file {out_file}")
                raise fnfe
            if not config.keep:
                dest[i-1].unlink()
                if urls[i] == urls[-1]:
                    dest[i].unlink()
        else:
            previous = dest[i]

        if not config.keep:
            if len(dest) > 2:
                dest[i-2].unlink(missing_ok=True)
    if len(urls) < 2:
        target = Path(config.commands.osmium.merge.path)
        logger.info(f"Just one file downloaded, renaming {dest[0]} to {target}")
        dest[0].rename(target)

def process(urls):
    global logger, config
    for url in urls:
        filename = Path(urlparse(url).path).name
        prefix = config.directories.download
        dest = Path(prefix).joinpath(filename)
        dest.parent.mkdir(parents=True, exist_ok=True)
        download(url, dest)
        # Split layers
        for layer, filter in config.tags.items():
            if filter == '':
                logger.info(f"Filter for {layer} is empty, ignoring")
                continue
            name = re.search(r'(?P<stem>.*?)(?P<suffix>\..*)', dest.name)
            layerfile = dest.parent.joinpath(f"{name.group('stem')}-{layer}{name.group('suffix')}")
            logger.debug(f"Splitting out {layer} from {dest} into {layerfile}")
            cmd = ' '.join([config.commands.osmium.filter.cmd, config.commands.osmium.filter.opts, f"-o {layerfile} {dest} {filter}"])
            try:
                run_external_with_logger(cmd, logger)
                logger.debug(f"Size of result file is {os.path.getsize(layerfile)}")
            except subprocess.CalledProcessError as cpe:
                logger.error(f"Failed to run {cpe.cmd}")
                raise cpe
            except FileNotFoundError as fnfe:
                logger.error(f"Can't find result file {layerfile}")
                raise fnfe
            # Tilemaker
            cmd = ' '.join([config.commands.tilemaker.cmd, config.commands.tilemaker.opts, f"--input {layerfile} --output {config.directories.tiles} --merge"])
            try:
                pass
                run_external_with_logger(cmd, logger)
                if "postprocess" in config.commands.tilemaker and config.commands.tilemaker.postprocess != "":
                    if isinstance(config.commands.tilemaker.postprocess, list):
                        for pp in dataset.postprocess:
                            run_external_with_logger(pp, logger, target)
                    else:
                        run_external_with_logger(config.commands.tilemaker.post, logger)
            except subprocess.CalledProcessError as cpe:
                logger.error(f"Failed to run {cpe.cmd}")
                raise cpe
            if not config.keep:
                Path(layerfile).unlink
        if not config.keep:
            dest.unlink()

def main():
    global logger, config
    logger = logging.getLogger()
    log_stream = StringIO()
    logging.basicConfig(stream=log_stream, level=logging.DEBUG)
    columns = shutil.get_terminal_size().columns
    parser = argparse.ArgumentParser(prog='mktiles.py', description='Create vector tiles')
    parser.add_argument('-v', '--verbose', action='store_true', default=False, help='Enable logging')
    parser.add_argument('-i', '--init', action='store_true', default=False, help='Download required datasets')
    parser.add_argument('-k', '--keep', action='store_true', default=False, help='Keep downloaded files')
    parser.add_argument('-u', '--urls', type=argparse.FileType('r'), required=True, help="URL file to be used")
    parser.add_argument('-c', '--config', type=argparse.FileType('r'), action='append', required=True, help="Configuration file to be used")
    parser.add_argument('--print-cmd', action='store_true', default=False, help='Print command for Planetiler')
    parser.add_argument('-m', '--merge', action='store_true', default=False, help='Merge only')
    parser.add_argument('-d', '--dump', action='store_true', default=False, help='List included regions')

    args = parser.parse_args()
    if args.verbose:
        print(log_stream.getvalue(), end='')
    else:
        logger.setLevel(default_log_level)
    logging.basicConfig(stream=sys.stdout, force=True)
    config = DotMap({"keep": False})
    config.columns = columns
    config.keep = args.keep
    if args.config:
        for cfg in args.config:
            config = config | load_config(cfg.name)
    if args.init:
        get_datasets()
    if args.urls:
        urls = load_urls(args.urls.name, False)
    if logging.DEBUG >= logger.level:
        logger.debug(f"Config is {config}")

    if args.dump:
        for url in urls:
            name = re.search(r'.*?geofabrik.de/(?P<region>.*)/(?P<subregion>.*?)-latest.osm.pbf', url)
            logger.info(f"Included {name.group('region')} -> {name.group('subregion')}")

    if args.print_cmd and config.commands['planetiler']:
        print(f"java -Xmx{config.commands.planetiler.memory} -jar {config.commands.planetiler.jar} {config.commands.planetiler.opts} --osm-path={config.datasets.merge.path}")

    if args.merge:
        merge(urls)
    else:
        process(urls)

if __name__ == "__main__":
    main()
