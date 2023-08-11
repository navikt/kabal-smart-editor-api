UPDATE klage.document
SET json = REPLACE(json, 'heading-one', 'h1')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'heading-two', 'h2')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'heading-three', 'h3')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'heading-four', 'h4')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'paragraph', 'p')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'bullet-list', 'ul')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'ordered-list', 'ol')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'numbered-list', 'ol')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'list-item-container', 'lic')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'list-item', 'li')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'textAlign', 'align')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'text-align-left', 'left')
where created > '2023-03-24';

UPDATE klage.document
SET json = REPLACE(json, 'text-align-right', 'right')
where created > '2023-03-24';