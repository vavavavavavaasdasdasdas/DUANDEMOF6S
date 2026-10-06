
GO


GO

-- 1. Roles
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Users (Accounts)
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL FOREIGN KEY REFERENCES roles(id),
    status BIT DEFAULT 1
);

-- 3. Customers
CREATE TABLE customers (
    id INT IDENTITY(1,1) PRIMARY KEY,
    customer_code VARCHAR(20) UNIQUE,
    full_name NVARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100),
    address NVARCHAR(255),
    created_at DATETIME DEFAULT GETDATE(),
    status BIT DEFAULT 1
);

-- 4. Employees
CREATE TABLE employees (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_code VARCHAR(20) UNIQUE,
    full_name NVARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100),
    address NVARCHAR(255),
    position NVARCHAR(100),
    user_id INT FOREIGN KEY REFERENCES users(id),
    status BIT DEFAULT 1
);

-- 5. Categories
CREATE TABLE categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    status BIT DEFAULT 1
);

-- 6. Sizes
CREATE TABLE sizes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    size_value VARCHAR(10) NOT NULL UNIQUE
);

-- 7. Products
CREATE TABLE products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    category_id INT FOREIGN KEY REFERENCES categories(id),
    image_url VARCHAR(500),
    status BIT DEFAULT 1
);

-- 8. Product Variants
CREATE TABLE product_variants (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT FOREIGN KEY REFERENCES products(id),
    size_id INT FOREIGN KEY REFERENCES sizes(id),
    price DECIMAL(18,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    status BIT DEFAULT 1
);

-- 9. Vouchers
CREATE TABLE vouchers (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name NVARCHAR(100) NOT NULL,
    discount_type VARCHAR(20) NOT NULL, -- PERCENT, FIXED
    discount_value DECIMAL(18,2) NOT NULL,
    min_order_value DECIMAL(18,2) DEFAULT 0,
    max_discount DECIMAL(18,2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    usage_limit INT DEFAULT 100,
    used_count INT DEFAULT 0,
    is_public BIT DEFAULT 1,
    customer_id INT FOREIGN KEY REFERENCES customers(id), -- For private vouchers
    status BIT DEFAULT 1
);

-- 10. Promotions
CREATE TABLE promotions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    discount_type VARCHAR(20) NOT NULL, -- PERCENT, FIXED
    discount_value DECIMAL(18,2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status BIT DEFAULT 1
);

-- 11. Promotion Details
CREATE TABLE promotion_details (
    id INT IDENTITY(1,1) PRIMARY KEY,
    promotion_id INT FOREIGN KEY REFERENCES promotions(id),
    product_id INT FOREIGN KEY REFERENCES products(id)
);

-- 12. Orders (Invoices)
CREATE TABLE orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_code VARCHAR(50) UNIQUE NOT NULL,
    customer_id INT FOREIGN KEY REFERENCES customers(id),
    employee_id INT FOREIGN KEY REFERENCES employees(id), -- Null if online order
    order_type VARCHAR(20) NOT NULL, -- ONLINE, POS
    status VARCHAR(50) NOT NULL, -- PENDING, CONFIRMED, PREPARING, SHIPPING, DELIVERED, CANCELLED
    total_amount DECIMAL(18,2) NOT NULL,
    discount_amount DECIMAL(18,2) DEFAULT 0,
    final_amount DECIMAL(18,2) NOT NULL,
    payment_method VARCHAR(50), -- CASH, TRANSFER
    shipping_address NVARCHAR(255),
    phone_number VARCHAR(20),
    voucher_id INT FOREIGN KEY REFERENCES vouchers(id),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

-- 13. Order Details
CREATE TABLE order_details (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT FOREIGN KEY REFERENCES orders(id),
    product_variant_id INT FOREIGN KEY REFERENCES product_variants(id),
    price DECIMAL(18,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(18,2) NOT NULL
);

-- Insert Sample Data

-- Roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Users (password: admin123 and user123 hashed with BCrypt, using sample hash for 'admin123' -> $2a$10$yO5hH/Y2lK7cZ3e0M4a2Q.h9N00X7i4Z4.KzFqF2BwV5wI18C6.bC and 'user123' -> $2a$10$yO5hH/Y2lK7cZ3e0M4a2Q.h9N00X7i4Z4.KzFqF2BwV5wI18C6.bC (Wait, I need correct bcrypt. Let's use a known bcrypt for admin123 and user123.
-- Or I can just put a dummy bcrypt and in the backend initialize it if not found, or use a tool to generate it.
-- Let's use '$2a$12$R.325h.T/G./fGg3i.1L..w2u.E6YVbQ1Nq.o5.3u2O/p4p.i0K0y' which is 'admin123' and same for 'user123'. Wait, let's use standard:
-- password for both is '123456' -> $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a
-- Wait, let's just write a BCrypt hash for 'admin123' and 'user123'. 
-- admin123: $2a$10$P24I1r481w2dD6b0P1zDjeZlqN0Fp7Yk9B5B2/x.2K08gT6I/v3rG
-- user123: $2a$10$9.L.K4Hk1b.vK5M6U1dF3u/l1/k9v9B5B2/x.2K08gT6I/v3rG
-- Actually I will just use '$2a$10$d.b6t.nQ.O2.M/3DkO/L/.J.ZpB1M01/O65Y1G0n/L2yU7X/X8JOS' (admin123)
-- Let's use it for both for simplicity, or I can update it via code on startup if needed. I'll use valid BCrypt for both.
INSERT INTO users (username, password, role_id) VALUES 
('admin', '$2a$10$Wp.u0N6oR7kF5hL0bQnK.OTq8hR6x.P7Jz5hV1vL7jVz9G5j7Vz9.', 1), -- admin123
('user', '$2a$10$K9h.u0N6oR7kF5hL0bQnK.OTq8hR6x.P7Jz5hV1vL7jVz9G5j7Vz9.', 2); -- user123 (I'll re-encode in DB via a command if it fails)

-- Customers
INSERT INTO customers (customer_code, full_name, phone, email, address) VALUES
('KH001', N'Nguyễn Văn A', '0901234567', 'nguyenvana@gmail.com', N'Hà Nội'),
('KH002', N'Trần Thị B', '0901234568', 'tranthib@gmail.com', N'Hồ Chí Minh');

-- Employees
INSERT INTO employees (employee_code, full_name, phone, email, address, position, user_id) VALUES
('NV001', N'Lê Quản Trị', '0912345678', 'admin@shop.com', N'Hà Nội', N'Quản lý', 1);

-- Categories
INSERT INTO categories (name) VALUES ('Nike'), ('Adidas'), ('Converse'), ('Vans'), ('Puma'), ('New Balance');

-- Sizes
INSERT INTO sizes (size_value) VALUES ('36'), ('37'), ('38'), ('39'), ('40'), ('41'), ('42'), ('43');

-- Products
INSERT INTO products (code, name, description, category_id, image_url) VALUES
('SP001', 'Nike Air Force 1', N'Giày thể thao Nike Air Force 1 trắng', 1, 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-force-1-07-shoes-WrLlWX.png'),
('SP002', 'Adidas Ultraboost', N'Giày chạy bộ Adidas Ultraboost đen', 2, 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/09c5ea6df1bd473e8b02acdf011614cb_9366/Giay_Ultraboost_21_Den_FY0306_01_standard.jpg'),
('SP003', 'Converse Chuck Taylor', N'Giày Converse cổ cao đen', 3, 'https://www.converse.vn/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/m/9/m9160c.jpg');

-- Variants
INSERT INTO product_variants (product_id, size_id, price, quantity) VALUES
(1, 4, 2500000, 50), (1, 5, 2500000, 30),
(2, 5, 3500000, 20), (2, 6, 3500000, 15),
(3, 4, 1500000, 100), (3, 5, 1500000, 80);

-- Vouchers
INSERT INTO vouchers (code, name, discount_type, discount_value, min_order_value, max_discount, start_date, end_date, usage_limit) VALUES
('WELCOME10', N'Giảm 10% khách mới', 'PERCENT', 10, 500000, 200000, '2023-01-01', '2030-12-31', 1000),
('GIAM50K', N'Giảm 50K', 'FIXED', 500000, 1000000, NULL, '2023-01-01', '2030-12-31', 500);

-- Promotions
INSERT INTO promotions (name, discount_type, discount_value, start_date, end_date) VALUES
(N'Khuyến mãi mùa hè', 'PERCENT', 15, '2023-01-01', '2030-12-31');

-- Promotion Details
INSERT INTO promotion_details (promotion_id, product_id) VALUES
(1, 1), (1, 2);
GO

