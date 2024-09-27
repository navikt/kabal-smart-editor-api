ALTER TABLE klage.document_version
    DROP COLUMN data
;

ALTER TABLE klage.document
    ADD COLUMN data TEXT
;