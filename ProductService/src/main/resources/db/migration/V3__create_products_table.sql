CREATE TABLE products (
                          product_id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
                          category_id INT(11),
                          product_name VARCHAR(50),
                          sku VARCHAR(255),
                          product_price DOUBLE,
                          quantity INT(11),
                          description VARCHAR,
                          discount FLOAT,
                          product_type VARCHAR
);
