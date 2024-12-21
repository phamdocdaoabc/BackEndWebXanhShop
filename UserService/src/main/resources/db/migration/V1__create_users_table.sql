CREATE TABLE users (
   user_id INT AUTO_INCREMENT PRIMARY KEY,
   full_name VARCHAR(255),
   sex VARCHAR(50),
   image_url VARCHAR(255) DEFAULT 'https://bootdey.com/img/Content/avatar/avatar7.png',
   email VARCHAR(255) DEFAULT 'springxyzabcboot@gmail.com',
   phone VARCHAR(50) DEFAULT '+21622125144',
   adress VARCHAR(255),
   birthday DATE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

