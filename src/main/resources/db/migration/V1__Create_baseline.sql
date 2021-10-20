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

CREATE TABLE klage.comment
(
    id                UUID PRIMARY KEY,
    parent_comment_id UUID REFERENCES klage.comment (id),
    document_id       UUID REFERENCES klage.document (id),
    text              TEXT      NOT NULL,
    author_name       TEXT      NOT NULL,
    author_ident      TEXT      NOT NULL,
    created           TIMESTAMP NOT NULL,
    modified          TIMESTAMP NOT NULL
);

CREATE INDEX document_comment_ix ON klage.comment (document_id);
CREATE INDEX comment_parent_comment_ix ON klage.comment (parent_comment_id);