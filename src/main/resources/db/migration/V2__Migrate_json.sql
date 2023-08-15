UPDATE klage.document
SET json = REPLACE(json, 'heading-one', 'h1')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'heading-two', 'h2')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'heading-three', 'h3')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'heading-four', 'h4')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'paragraph', 'p')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'bullet-list', 'ul')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'numbered-list', 'ol')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'list-item-container', 'lic')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'list-item', 'li')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'textAlign', 'align')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'text-align-left', 'left')
WHERE created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'text-align-right', 'right')
WHERE created > '2023-03-24';
