TRUNCATE TABLE order_items, items, orders RESTART IDENTITY CASCADE;


INSERT INTO items (name, deleted, price, created_at, updated_at)
VALUES
    ('Widget A', false, 9.99, NOW(), NOW()),
    ('Gadget B', true, 29.99, NOW(), NOW()),
    ('Tool C', false, 19.99, NOW(), NOW()),
    ('Device D', false, 49.99, NOW(), NOW()),
    ('Component E', false, 14.99, NOW(), NOW()),
    ('Module F', false, 39.99, NOW(), NOW()),
    ('Accessory G', false, 4.99, NOW(), NOW()),
    ('Kit H', false, 59.99, NOW(), NOW()),
    ('Package I', false, 99.99, NOW(), NOW()),
    ('Upgrade J', false, 79.99, NOW(), NOW());

INSERT INTO orders (user_id, total_price, status, created_at, updated_at, deleted)
VALUES
    (1,39.97, 'CREATED', NOW(), NOW(), false),
    (2, 54.95, 'APPROVED', NOW(), NOW(), true),
    (3, 209.97, 'CREATED', NOW(), NOW(), false);

INSERT INTO order_items (order_id, item_id, quantity, created_at, updated_at)
VALUES
    (1, 1, 2, NOW(), NOW()),
    (1, 3, 1, NOW(), NOW()),
    (2, 5, 3, NOW(), NOW()),
    (2, 7, 2, NOW(), NOW()),
    (3, 9, 1, NOW(), NOW()),
    (3, 10, 1, NOW(), NOW()),
    (3, 2, 1, NOW(), NOW());