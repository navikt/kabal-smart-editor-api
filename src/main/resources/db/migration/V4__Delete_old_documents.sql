DELETE
FROM klage.document_version
WHERE created < '2023-06-06'
  AND document_id NOT IN (
                          '9f64b3a9-b484-48fa-8937-43e0dba24c43',
                          'f6e37bd8-c277-4556-8b40-7ff4ee559561',
                          '45038c1f-4f20-4c09-893b-c3fab817bb0a'
    );

DELETE
FROM klage.document
WHERE created < '2023-06-06'
  AND id NOT IN (
                          '9f64b3a9-b484-48fa-8937-43e0dba24c43',
                          'f6e37bd8-c277-4556-8b40-7ff4ee559561',
                          '45038c1f-4f20-4c09-893b-c3fab817bb0a'
    );
