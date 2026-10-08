-- Demo-data til Wishlist (H2 in-memory, kører hvert boot)
-- Hibernate laver tabeller (ddl-auto=create). Vi seeder blot indhold.

INSERT INTO users (id, username, email, password) VALUES
    (1, 'Demo',  'demo', 'demo'),
    (2, 'Sofie', 'sofie@wishlist.dk', 'demo1234');

INSERT INTO wishlists (id, name, share_id, user_id) VALUES
    (1, 'Nyfødt baby',    '11111111-1111-1111-1111-111111111111', 1),
    (2, 'Fødselsdag',     '22222222-2222-2222-2222-222222222222', 1),
    (3, 'Bryllup',        '33333333-3333-3333-3333-333333333333', 2);

INSERT INTO wishes (id, description, link, price, reserved, reserved_by, wishlist_id) VALUES
    (1, 'BabyBjörn bæresele One',        'https://www.babybjorn.dk',       999.00, FALSE, NULL,       1),
    (2, 'Mushie sutter 2-pak',           'https://mushie.com',             249.00, TRUE,  'Mormor',   1),
    (3, 'Liewood pusle-sæt',             'https://liewood.com',            349.00, FALSE, NULL,       1),
    (4, 'Nike Air Force 1',              'https://www.nike.com/dk',       1099.00, FALSE, NULL,       2),
    (5, 'Fjällräven Kånken rygsæk',      'https://www.fjallraven.com',     699.00, FALSE, NULL,       2),
    (6, 'LEGO Technic Lamborghini',      'https://www.lego.com/da-dk',     899.00, FALSE, NULL,       2),
    (7, 'Royal Copenhagen krus-sæt',     'https://www.royalcopenhagen.com',599.00, FALSE, NULL,       3),
    (8, 'Zwilling knivsæt',              'https://www.zwilling.com',      2499.00, TRUE,  'Søster',   3),
    (9, 'Smeg brødrister',               'https://www.smeg.dk',           1799.00, FALSE, NULL,       3);

-- H2 identity-sekvenser skal følge med
ALTER TABLE users     ALTER COLUMN id RESTART WITH 10;
ALTER TABLE wishlists ALTER COLUMN id RESTART WITH 10;
ALTER TABLE wishes    ALTER COLUMN id RESTART WITH 10;
