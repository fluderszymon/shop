SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE carts;
TRUNCATE TABLE products;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (username, email, password, role, address, balance) VALUES
('admin', 'admin@shop.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/8Kz8Kz2', 'ADMIN', '123 Admin Street, Admin City', 10000.00),
('john_doe', 'john@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/8Kz8Kz2', 'USER', '456 Main Street, New York', 500.00),
('jane_smith', 'jane@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/8Kz8Kz2', 'USER', '789 Oak Avenue, Los Angeles', 750.00),
('bob_wilson', 'bob@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/8Kz8Kz2', 'USER', '321 Pine Road, Chicago', 300.00),
('alice_brown', 'alice@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/8Kz8Kz2', 'USER', '654 Elm Street, Houston', 1200.00);

INSERT INTO products (name, description, price, stock) VALUES
('Laptop Pro 15"', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 25),
('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 29.99, 150),
('Mechanical Keyboard', 'RGB backlit mechanical keyboard with blue switches', 89.99, 75),
('Monitor 27" 4K', 'Ultra HD 4K monitor with HDR support', 399.99, 40),
('Webcam HD', '1080p HD webcam with auto-focus and noise reduction', 79.99, 60),
('Gaming Headset', '7.1 surround sound gaming headset with microphone', 149.99, 30),
('USB-C Hub', 'Multi-port USB-C hub with HDMI, USB 3.0, and SD card reader', 49.99, 100),
('External SSD 1TB', 'Portable SSD with USB 3.2 Gen 2 interface', 129.99, 50),
('Desk Lamp LED', 'Adjustable LED desk lamp with touch control', 39.99, 80),
('Laptop Stand', 'Aluminum laptop stand with adjustable height', 24.99, 120);

INSERT INTO carts (user_id) VALUES
(2),
(3),
(4),
(5);

INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 1),
(2, 4, 1),
(2, 5, 1),
(3, 6, 1),
(3, 7, 1),
(4, 8, 1),
(4, 9, 1),
(4, 10, 1);

INSERT INTO orders (user_id, total_price, order_date) VALUES
(2, 1459.97, '2024-01-15'),
(3, 479.98, '2024-01-16'),
(4, 199.98, '2024-01-17'),
(5, 194.97, '2024-01-18');

INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES
(1, 1, 1, 1299.99),
(1, 2, 2, 29.99),
(1, 3, 1, 89.99),
(2, 4, 1, 399.99),
(2, 5, 1, 79.99),
(3, 6, 1, 149.99),
(3, 7, 1, 49.99),
(4, 8, 1, 129.99),
(4, 9, 1, 39.99),
(4, 10, 1, 24.99);

SELECT 'Database populated successfully!' as Status;