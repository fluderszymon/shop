CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL,
    address VARCHAR(255),
    balance DOUBLE DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    stock INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS carts (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DOUBLE NOT NULL,
    order_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_purchase DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

INSERT INTO users (username, email, password, role, address, balance) VALUES
('admin', 'admin@shop.com', '$2a$12$k42PXoiZ.XKnEpXyzeY9cOPwNnVyjb16xQwRZOAbCEoXQlK05Jiom', 'ADMIN', '123 Admin Street, Admin City', 10000.00),
('john_doe', 'john@example.com', '$2a$12$WCUskLcjWsUX4fMDz74zuuLWYRa29/IqGRpB/7Ft4B.Jv8krPmIai', 'USER', '456 Main Street, New York', 500.00),
('jane_smith', 'jane@example.com', '$2a$12$7iB5A3lvJ3rhnCh.t7tu/uAGk7vdGWO6Y4ciUdx8bltbF9lUb0.wy', 'USER', '789 Oak Avenue, Los Angeles', 750.00),
('bob_wilson', 'bob@example.com', '$2a$12$QGki6m2npLQ.0vyO17gObefRA70LsO8Ce/myNrDkzdumg0AGpXYvm', 'USER', '321 Pine Road, Chicago', 300.00),
('alice_brown', 'alice@example.com', '$$2a$12$Y9LjbDhaDFsF2/WemP1Bnu8ix5WCXg7INo09u5CpfqLA.hkp/pQC2', 'USER', '654 Elm Street, Houston', 1200.00);

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
(1),
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
(4, 10, 1),
(5, 1, 1),
(5, 4, 1);

INSERT INTO orders (user_id, total_price, order_date) VALUES
(1, 1459.97, '2024-01-15'),
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