CREATE DATABASE IF NOT EXISTS blinkit_replica
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blinkit_replica;

CREATE TABLE IF NOT EXISTS categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  category_id INT NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  image_url VARCHAR(500),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_name VARCHAR(120) NOT NULL,
  delivery_address VARCHAR(500) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id INT NOT NULL,
  product_name VARCHAR(120) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT IGNORE INTO categories (id, name) VALUES
(1, 'Dairy'), (2, 'Fruits'), (3, 'Vegetables'),
(4, 'Snacks'), (5, 'Beverages'), (6, 'Bakery');

INSERT INTO products (category_id, name, description, price, stock, image_url)
SELECT c.id, v.name, v.description, v.price, v.stock, v.image_url
FROM (
  SELECT 'Dairy' category, 'Fresh Milk 500 ml' name, 'Pasteurised toned milk' description, 32.00 price, 50 stock, 'https://placehold.co/400x300/f4f8e9/245b35?text=Fresh+Milk' image_url
  UNION ALL SELECT 'Dairy', 'Farm Eggs (6 pcs)', 'Pack of six eggs', 48.00, 35, 'https://placehold.co/400x300/f4f8e9/245b35?text=Eggs'
  UNION ALL SELECT 'Fruits', 'Banana (6 pcs)', 'Fresh bananas', 42.00, 40, 'https://placehold.co/400x300/f4f8e9/245b35?text=Bananas'
  UNION ALL SELECT 'Fruits', 'Apples (4 pcs)', 'Crisp red apples', 120.00, 25, 'https://placehold.co/400x300/f4f8e9/245b35?text=Apples'
  UNION ALL SELECT 'Vegetables', 'Tomato 500 g', 'Fresh red tomatoes', 28.00, 45, 'https://placehold.co/400x300/f4f8e9/245b35?text=Tomatoes'
  UNION ALL SELECT 'Vegetables', 'Potato 1 kg', 'Everyday potatoes', 36.00, 60, 'https://placehold.co/400x300/f4f8e9/245b35?text=Potatoes'
  UNION ALL SELECT 'Snacks', 'Potato Chips', 'Classic salted chips', 20.00, 70, 'https://placehold.co/400x300/f4f8e9/245b35?text=Chips'
  UNION ALL SELECT 'Snacks', 'Chocolate Bar', 'Milk chocolate bar', 35.00, 40, 'https://placehold.co/400x300/f4f8e9/245b35?text=Chocolate'
  UNION ALL SELECT 'Beverages', 'Orange Juice 1 L', 'Orange fruit drink', 95.00, 20, 'https://placehold.co/400x300/f4f8e9/245b35?text=Juice'
  UNION ALL SELECT 'Beverages', 'Bottled Water 1 L', 'Drinking water', 20.00, 100, 'https://placehold.co/400x300/f4f8e9/245b35?text=Water'
  UNION ALL SELECT 'Bakery', 'Bread Loaf', 'Soft sandwich bread', 40.00, 30, 'https://placehold.co/400x300/f4f8e9/245b35?text=Bread'
  UNION ALL SELECT 'Dairy', 'Butter 100 g', 'Creamy salted butter', 58.00, 25, 'https://placehold.co/400x300/f4f8e9/245b35?text=Butter'
) v
JOIN categories c ON c.name = v.category
WHERE NOT EXISTS (SELECT 1 FROM products p WHERE p.name = v.name);
