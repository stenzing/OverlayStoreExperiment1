CREATE SCHEMA sample_schema AUTHORIZATION sa;
CREATE TABLE entries (path VARCHAR(255) NOT NULL, content BLOB, is_deleted BOOLEAN DEFAULT FALSE, version INT DEFAULT 0, volume_id VARCHAR(50) NOT NULL,  PRIMARY KEY(path, volume_id));
