-- See https://dba.stackexchange.com/a/103153
-- See https://postgresqlco.nf/doc/en/param/
ALTER SYSTEM SET archive_mode = off;
ALTER SYSTEM SET archive_command = '/bin/true';
ALTER SYSTEM SET archive_cleanup_command = 'pg_archivecleanup /var/lib/postgresql/data/pg_wal %r';
ALTER SYSTEM SET min_wal_size = '32MB';
ALTER SYSTEM SET max_wal_size = '512MB';
ALTER SYSTEM SET max_wal_senders = 0;
ALTER SYSTEM SET wal_level = minimal;
ALTER SYSTEM SET wal_buffers = '4MB';
ALTER SYSTEM SET wal_keep_size = 0;
ALTER SYSTEM SET wal_compression = pglz;
ALTER SYSTEM SET wal_recycle = off;
-- ALTER SYSTEM SET default_toast_compression = pglz;
ALTER SYSTEM RESET shared_buffers;
