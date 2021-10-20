DO
$$
    BEGIN
        IF EXISTS
            (SELECT 1 from pg_roles where rolname = 'cloudsqliamuser')
        THEN
            GRANT USAGE ON SCHEMA public TO cloudsqliamuser;
            GRANT USAGE ON SCHEMA klage TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA public TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA klage TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA klage GRANT SELECT ON TABLES TO cloudsqliamuser;
        END IF;
    END
$$;

CREATE TABLE klage.document
(
    id       UUID PRIMARY KEY,
    json     TEXT      NOT NULL,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

CREATE TABLE klage.document_comment
(
    id          UUID PRIMARY KEY,
    document_id UUID REFERENCES klage.document (id),
    text        TEXT      NOT NULL,
    created     TIMESTAMP NOT NULL,
    modified    TIMESTAMP NOT NULL
);

CREATE INDEX document_comment_ix ON klage.document_comment (document_id);