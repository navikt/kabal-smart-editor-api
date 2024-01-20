ALTER TABLE klage.document RENAME TO document_version;

ALTER TABLE klage.document_version
    ADD COLUMN version INT DEFAULT 1 NOT NULL,
    ADD COLUMN author_nav_ident TEXT
;

ALTER TABLE klage.document_version
    RENAME COLUMN id TO document_id;

ALTER TABLE klage.comment DROP CONSTRAINT comment_document_id_fkey;

ALTER TABLE klage.document_version DROP CONSTRAINT document_pkey;
ALTER TABLE klage.document_version ADD PRIMARY KEY (document_id, version);

CREATE INDEX document_id_ix ON klage.document_version (document_id);
CREATE INDEX document_version_ix ON klage.document_version (version);

CREATE TABLE klage.document
(
    id       UUID PRIMARY KEY,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

INSERT INTO klage.document(id, created, modified)
SELECT document_id, created, modified
FROM klage.document_version;

ALTER TABLE klage.document_version ADD CONSTRAINT document_version_document_id_fkey FOREIGN KEY (document_id) REFERENCES klage.document (id);

ALTER TABLE klage.comment ADD CONSTRAINT comment_document_id_fkey FOREIGN KEY (document_id) REFERENCES klage.document (id);

--was forgotten
CREATE INDEX document_id_comment_ix ON klage.comment (document_id);
