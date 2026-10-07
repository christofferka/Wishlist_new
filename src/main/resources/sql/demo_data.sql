-- Demo-data til Wishlist (H2 in-memory, kører hvert boot)
-- Hibernate laver tabeller (ddl-auto=create). Vi seeder blot indhold.

INSERT INTO users (id, username, email, password) VALUES
    (1, 'Christoffer', 'demo@wishlist.dk', 'demo1234'),
    (2, 'Sofie',       'sofie@wishlist.dk', 'demo1234');

INSERT INTO wishlists (id, name, share_id, user_id) VALUES
    (1, 'Jul 2025',       '11111111-1111-1111-1111-111111111111', 1),
    (2, 'Fødselsdag',     '22222222-2222-2222-2222-222222222222', 1),
    (3, 'Bryllup',        '33333333-3333-3333-3333-333333333333', 2);

INSERT INTO wishes (id, description, link, price, reserved, reserved_by, wishlist_id) VALUES
    (1, 'Sony WH-1000XM5 høretelefoner', 'https://www.sony.dk',           2999.00, FALSE, NULL,       1),
    (2, 'Le Creuset gryde 24 cm',        'https://www.lecreuset.dk',      1899.00, TRUE,  'Mor',      1),
    (3, 'Kindle Paperwhite',             'https://www.amazon.de',         1499.00, FALSE, NULL,       1),
    (4, 'Rejsegavekort til Berlin',      'https://www.momondo.dk',        3000.00, FALSE, NULL,       2),
    (5, 'Fjällräven Kånken rygsæk',      'https://www.fjallraven.com',     699.00, FALSE, NULL,       2),
    (6, 'Vinsmagning for to',            'https://www.wineandbarrels.dk', 1200.00, FALSE, NULL,       3),
    (7, 'KitchenAid røremaskine',        'https://www.kitchenaid.dk',     4499.00, TRUE,  'Søster',   3);

-- H2 identity-sekvenser skal følge med
ALTER TABLE users     ALTER COLUMN id RESTART WITH 10;
ALTER TABLE wishlists ALTER COLUMN id RESTART WITH 10;
ALTER TABLE wishes    ALTER COLUMN id RESTART WITH 10;
