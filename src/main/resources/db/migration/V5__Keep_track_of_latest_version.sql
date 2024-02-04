CREATE TABLE klage.latest_document_version
(
    document_id     UUID PRIMARY KEY,
    current_version INT NOT NULL DEFAULT 1,
    CONSTRAINT latest_document_version_document_id_fkey
        FOREIGN KEY (document_id) REFERENCES klage.document (id)
);

INSERT INTO klage.latest_document_version(document_id, current_version)
SELECT document_id, MAX(version)
FROM klage.document_version
GROUP BY document_id;

CREATE FUNCTION update_current_version() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$f$
BEGIN
    INSERT INTO klage.latest_document_version AS lnv
    VALUES (NEW.document_id)
    ON CONFLICT(document_id) DO UPDATE SET current_version=NEW.version;
    RETURN NEW;
END;
$f$;

CREATE TRIGGER tg_document_version
    BEFORE INSERT
    ON klage.document_version
    FOR EACH ROW
EXECUTE FUNCTION update_current_version();
