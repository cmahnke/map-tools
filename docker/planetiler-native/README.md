# Notes

# Notes 2026

This seems to work, at least on Docker (Linux on ARM)

## References

* https://github.com/onthegomap/planetiler/discussions/773
* https://github.com/apache/lucene/pull/13196

## TODO

* Consider fixing org.geotools.util.logging.Logging$LogLevel
  * See https://github.com/onthegomap/planetiler/pull/776
* Remove reachability metadata, it could be regenerated if actually needed

# Notes 2024

https://simply-how.com/fix-graalvm-native-image-compilation-issues


Replace log4j with logback: https://github.com/micronaut-projects/micronaut-core/issues/6041

https://issues.apache.org/jira/browse/LOG4J2-2604

https://graalvm.github.io/native-build-tools/latest/maven-plugin.html


# Maven profiles

## `shade` in `planetiler-dist`

```
<profile>
  <id>shade</id>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.5.1</version>
        <dependencies>
          <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-transform-maven-shade-plugin-extensions</artifactId>
            <version>0.1.0</version>
          </dependency>
        </dependencies>
        <configuration>
          <transformers>
            <transformer implementation="org.apache.logging.log4j.maven.plugins.shade.transformer.Log4j2PluginCacheFileTransformer"/>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
              <manifestEntries>
                <Multi-Release>true</Multi-Release>
              </manifestEntries>
            </transformer>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
              <mainClass>${mainClass}</mainClass>
            </transformer>
          </transformers>
          <shadedArtifactAttached>true</shadedArtifactAttached>
          <filters>
            <filter>
              <artifact>*:*</artifact>
              <excludes>
                <exclude>META-INF/*.SF</exclude>
                <exclude>META-INF/*.DSA</exclude>
                <exclude>META-INF/*.RSA</exclude>
              </excludes>
            </filter>
          </filters>
        </configuration>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</profile>
```

# GraalVM native config
## `proxy-config-json`

```
[
  {
    "interfaces":["com.onthegomap.planetiler.util.Madvise$NativeC","jnr.ffi.provider.LoadedLibrary"]
  }
]
```


# Example dev workflow

## Install GraalVM

```
sdk install java 24.0.1-graal
```

```
git clone --recurse-submodules https://github.com/onthegomap/planetiler.git --single-branch
cd planetiler
mvn -DskipTests=true clean install
mvn -DskipTests=true clean install package
mkdir -p ./planetiler-native-config-help ./planetiler-native-config-generate ./planetiler-native-config
export DIST_DIR=planetiler-dist/target
export PLANETILER_JAR=`ls $DIST_DIR/planetiler-*with-deps.jar`
$JAVA_HOME/bin/java -agentlib:native-image-agent=config-output-dir=./planetiler-native-config-help/ -cp $PLANETILER_JAR com.onthegomap.planetiler.Main --help
native-image-configure generate --input-dir=./planetiler-native-config-help/ --input-dir=./planetiler-native-config-generate/ --output-dir=./planetiler-native-config/
native-image -H:+UnlockExperimentalVMOptions -H:ConfigurationFileDirectories=./planetiler-native-config/ -H:EnableURLProtocols=http,https --no-fallback -march=native -cp $PLANETILER_JAR $PLANETILER_BIN
```
