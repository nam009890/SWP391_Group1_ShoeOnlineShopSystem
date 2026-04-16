--If you want drop database , try this
USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'ShoesOnlineShop')
BEGIN
    ALTER DATABASE ShoesOnlineShop SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ShoesOnlineShop;
END
GO

CREATE DATABASE ShoesOnlineShop
GO
USE ShoesOnlineShop
GO

CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_name NVARCHAR(50) UNIQUE NOT NULL,
    user_email NVARCHAR(100) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NULL,
    full_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(MAX),
    user_role NVARCHAR(50) NOT NULL 
        CHECK (user_role IN ('ADMIN', 'SALE_STAFF', 'CUSTOMER', 'MARKETING_STAFF', 'SHOP_MANAGER')),
    auth_provider NVARCHAR(20),
    provider_id NVARCHAR(50),
    reset_token varchar (100),
    reset_token_expiry DATETIME2 (7),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng Categories
CREATE TABLE categories (
    category_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_name NVARCHAR(100) UNIQUE NOT NULL,
    is_active BIT DEFAULT 1,
    display_order INT DEFAULT 0,
    parent_id BIGINT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (parent_id) REFERENCES categories(category_id)
);
GO

-- Bảng Products
CREATE TABLE products (
   product_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_name NVARCHAR(200) NOT NULL,
    product_description NVARCHAR(MAX),
    product_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    size NVARCHAR(50),
    color NVARCHAR(100),
    image_url NVARCHAR(500),
    category_id BIGINT,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
GO

-- Bảng Coupons
CREATE TABLE coupons (
    coupon_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    coupon_name NVARCHAR(200) NOT NULL,
    coupon_code NVARCHAR(50) UNIQUE NOT NULL,
    discount_value INT NOT NULL,
    discount_type NVARCHAR(20) DEFAULT 'PERCENTAGE' CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    min_order_value INT DEFAULT 0,
    max_discount_amount INT,
    scope NVARCHAR(30) DEFAULT 'ALL' CHECK (scope IN ('ALL', 'SPECIFIC_PRODUCTS')),
    create_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BIT DEFAULT 1,
    approval_status NVARCHAR(20) DEFAULT 'PENDING' CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED', 'REMAKE')),
    remake_note NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng Orders
CREATE TABLE orders (
    order_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date DATETIME2 DEFAULT GETDATE(),
    total_amount DECIMAL(10, 2) NOT NULL,
    order_status NVARCHAR(20) DEFAULT 'PENDING'
        CHECK (order_status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    shipping_address NVARCHAR(MAX),
    phone NVARCHAR(20),
    payment_status NVARCHAR(20) DEFAULT 'PENDING'
        CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED')),
    coupon_id BIGINT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    is_active BIT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id)
);
GO

-- Bảng Order Details
CREATE TABLE order_details (
    order_detail_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
GO

-- Bảng Carts
CREATE TABLE carts (
    cart_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    session_id NVARCHAR(255),
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    size NVARCHAR(50),
    color NVARCHAR(100),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
GO

-- Bảng Invoices
CREATE TABLE invoices (
    invoice_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    invoice_number NVARCHAR(100) UNIQUE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    generated_date DATETIME2 DEFAULT GETDATE(),
    pdf_url NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    is_active BIT DEFAULT 1,
    [status] VARCHAR(10) DEFAULT 'Active',
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
GO

-- Bảng Payments
CREATE TABLE payments (
    payment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL,
    payment_method NVARCHAR(50),
    payment_status NVARCHAR(20) DEFAULT 'PENDING'
        CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED')),
    gateway_response NVARCHAR(MAX),
    transaction_id NVARCHAR(100),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
GO

CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);



-- =========================================
-- 4. TẠO BẢNG TRUNG GIAN USERS_ROLES (Quan hệ Nhiều - Nhiều)
-- =========================================
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Bảng Feedbacks
CREATE TABLE feedbacks (
    feedback_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    session_id NVARCHAR(255),
    order_id BIGINT,
    product_id BIGINT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    is_approved BIT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
GO

-- Bảng Sliders
CREATE TABLE sliders (
    slider_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    slider_title NVARCHAR(200),
    image_url NVARCHAR(500) NOT NULL,
    position INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    approval_status NVARCHAR(20) DEFAULT 'PENDING' CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED', 'REMAKE')),
    remake_note NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng Contents
CREATE TABLE contents (
    content_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    content_title NVARCHAR(200) NOT NULL,
    content_text NVARCHAR(MAX),
    image_url NVARCHAR(500),
    content_type NVARCHAR(20) NOT NULL
        CHECK (content_type IN ('BANNER', 'PROMO', 'ABOUT')),
    is_active BIT DEFAULT 1,
    approval_status NVARCHAR(20) DEFAULT 'PENDING' CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED', 'REMAKE')),
    remake_note NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng trung gian nối Slider và Coupon
CREATE TABLE slider_coupons (
    slider_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    PRIMARY KEY (slider_id, coupon_id),
    FOREIGN KEY (slider_id) REFERENCES sliders(slider_id) ON DELETE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON DELETE CASCADE
);
GO

-- Bảng trung gian nối Slider và Product (ĐÃ THÊM CỘT DISCOUNT ĐỂ PATCH LỖI)
CREATE TABLE slider_products (
    slider_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    discount INT NOT NULL DEFAULT 0, -- NEW FIELD
    PRIMARY KEY (slider_id, product_id),
    FOREIGN KEY (slider_id) REFERENCES sliders(slider_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
CREATE TABLE user_coupons (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    is_used BIT DEFAULT 0,
    CONSTRAINT UQ_user_coupon UNIQUE (user_id, coupon_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON DELETE CASCADE
);
CREATE TABLE order_history (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status NVARCHAR(50),
    note NVARCHAR(MAX),
    timestamp DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);
GO

INSERT INTO users (user_name, user_email, password_hash, full_name, phone, address, user_role)
VALUES
('admin1','admin@shoeshop.com','hash','Admin Shop','0900000001','Hanoi','ADMIN'),
('sale1','sale1@shoeshop.com','hash','Sale Staff 1','0900000002','Hanoi','SALE_STAFF'),
('sale2','sale2@shoeshop.com','hash','Sale Staff 2','0900000003','Hanoi','SALE_STAFF'),
('customer1','cus1@mail.com','hash','Nguyen Van A','0900000004','Hanoi','CUSTOMER'),
('customer2','cus2@mail.com','hash','Tran Van B','0900000005','Hanoi','CUSTOMER'),
('customer3','cus3@mail.com','hash','Le Van C','0900000006','Hanoi','CUSTOMER'),
('customer4','cus4@mail.com','hash','Pham Van D','0900000007','Hanoi','CUSTOMER'),
('customer5','cus5@mail.com','hash','Hoang Van E','0900000008','Hanoi','CUSTOMER'),
('customer6','cus6@mail.com','hash','Do Van F','0900000009','Hanoi','CUSTOMER'),
('customer7','cus7@mail.com','hash','Bui Van G','0900000010','Hanoi','CUSTOMER');
GO

INSERT INTO categories (category_name, is_active, display_order, parent_id)
VALUES 
-- Parent Categories (IDs: 1 to 5)
('Men', 1, 1, NULL),
('Women', 1, 2, NULL),
('Kids', 1, 3, NULL),
('Sport', 1, 4, NULL),
('Brand', 1, 5, NULL),

-- Subcategories for Men (Parent 1) (IDs: 6 to 10)
('Men Sneakers', 1, 1, 1),
('Men Boots', 1, 2, 1),
('Men Oxfords', 1, 3, 1),
('Men Loafers', 1, 4, 1),
('Men Sandals', 1, 5, 1),

-- Subcategories for Women (Parent 2) (IDs: 11 to 15)
('Women Heels', 1, 1, 2),
('Women Flats', 1, 2, 2),
('Women Sneakers', 1, 3, 2),
('Women Boots', 1, 4, 2),
('Women Sandals', 1, 5, 2),

-- Subcategories for Kids (Parent 3) (IDs: 16 to 20)
('Boys Sneakers', 1, 1, 3),
('Girls Flats', 1, 2, 3),
('School Shoes', 1, 3, 3),
('Kids Boots', 1, 4, 3),
('Toddler Shoes', 1, 5, 3),

-- Subcategories for Sport (Parent 4) (IDs: 21 to 25)
('Running', 1, 1, 4),
('Basketball', 1, 2, 4),
('Football', 1, 3, 4),
('Tennis', 1, 4, 4),
('Training', 1, 5, 4),

-- Subcategories for Brand (Parent 5) (IDs: 26 to 30)
('Nike', 1, 1, 5),
('Adidas', 1, 2, 5),
('Puma', 1, 3, 5),
('Vans', 1, 4, 5),
('Converse', 1, 5, 5);
GO

INSERT INTO products (product_name, product_description, product_price, stock_quantity, size, color, image_url, category_id)
VALUES
-- 20 Products distributed among child categories
('Classic Men Sneaker', 'A versatile white sneaker for everyday wear.', 1500000, 50, '42', 'White', '/uploads/men-sneaker.png', 6),
('Elegant Men Oxford', 'Premium leather oxford shoes for formal events.', 3200000, 30, '41', 'Black', '/uploads/products/1774488495474_images (2).jpg', 8),
('Casual Men Loafer', 'Comfortable slip-on loafers.', 1800000, 40, '43', 'Brown', '/uploads/products/1774488453294_images (1).jpg', 9),
('Sturdy Men Boot', 'Durable combat boots for tough terrains.', 2500000, 25, '44', 'Brown', '/uploads/products/1774488665012_images (3).jpg', 7),
('Women Stiletto Heel', 'Elegant high heels for parties.', 1900000, 45, '38', 'Red', '/uploads/products/1774488688634_images (4).jpg', 11),
('Women Ballet Flat', 'Comfortable everyday flats.', 1200000, 60, '37', 'Beige', '/uploads/products/1774488723037_images (5).jpg', 12),
('Women Chunky Sneaker', 'Trendy chunky sneakers.', 2100000, 50, '39', 'White', '/uploads/products/1774488745150_images (6).jpg', 13),
('Women Ankle Boot', 'Stylish leather ankle boots.', 2800000, 35, '38', 'Black', '/uploads/products/1774488769838_images (7).jpg', 14),
('Kids Running Shoe', 'Lightweight running shoes for active boys.', 950000, 80, '32', 'Blue', '/uploads/products/1774488789569_images (8).jpg', 16),
('Girls Sparkle Flat', 'Cute sparkly flats for girls.', 850000, 70, '30', 'Pink', '/uploads/products/1774488821338_images (9).jpg', 17),
('Classic School Shoe', 'Durable black school shoes.', 1100000, 100, '34', 'Black', '/uploads/products/1774488858303_images (10).jpg', 18),
('Pro Running Shoe X', 'High-performance marathon running shoes.', 3500000, 40, '42', 'Neon Green', '/uploads/products/1774488891180_images (11).jpg', 21),
('Court Master Basketball', 'Excellent grip for basketball courts.', 3800000, 30, '45', 'Red/Black', '/uploads/products/1774488954369_images (12).jpg', 22),
('Turf Football Cleat', 'Agile cleats for artificial turf.', 2200000, 50, '43', 'Blue/White', '/uploads/products/1774488986671_images (13).jpg', 23),
('Grand Slam Tennis Shoe', 'Durable hard-court tennis shoes.', 2600000, 40, '41', 'White', '/uploads/products/1774489020144_images (14).jpg', 24),
('Nike Air Max Vision', 'Classic Nike Air Max design.', 4500000, 20, '42', 'Grey', '/uploads/products/1774489055056_images (15).jpg', 26),
('Adidas Ultraboost Pro', 'Ultimate comfort with boost technology.', 4200000, 25, '43', 'Black', '/uploads/products/1774489083237_images (16).jpg', 27),
('Puma RS-X Core', 'Chunky retro style from Puma.', 2900000, 35, '41', 'White/Blue', '/uploads/products/1774489121719_images (17).jpg', 28),
('Vans Old Skool Classic', 'The timeless skate shoe.', 1700000, 60, '41', 'Black/White', '/uploads/products/1774489169746_vans-classic-old-skool-13.jpg', 29),
('Converse Chuck 70 High', 'Premium canvas high-tops.', 1900000, 55, '42', 'Parchment', '/uploads/products/1774489200058_images (18).jpg', 30);
GO

INSERT INTO coupons (coupon_name, coupon_code, discount_value, create_date, end_date, approval_status)
VALUES
('New Year Sale','NY2026',10,'2026-01-01','2026-02-28','APPROVED'),
('Summer Sale','SUMMER15',15,'2026-05-01','2026-08-31','APPROVED'),
('VIP Customer','VIP20',20,'2026-01-01','2026-12-31','APPROVED'),
('Flash Sale','FLASH25',25,'2026-02-01','2026-02-15','APPROVED'),
('Student Discount','STUDENT5',5,'2026-01-01','2026-12-31','APPROVED'),
('Black Friday','BF30',30,'2026-11-20','2026-11-30','APPROVED'),
('Shopee Style','SHOES10',10,'2026-01-01','2026-12-31','APPROVED'),
('Holiday Sale','HOLIDAY15',15,'2026-12-01','2026-12-31','APPROVED'),
('Member Discount','MEMBER10',10,'2026-01-01','2026-12-31','APPROVED'),
('Mega Sale','MEGA40',40,'2026-09-01','2026-09-09','APPROVED'),
('Spring Collection','SPRING12',12,'2026-03-01','2026-04-30','APPROVED'),
('Autumn Vibes','AUTUMN15',15,'2026-09-01','2026-10-31','APPROVED'),
('First Purchase','FIRST10',10,'2026-01-01','2026-12-31','PENDING'),
('Weekend Flash','WEEKEND20',20,'2026-03-14','2026-03-15','PENDING'),
('Clearance Sale','CLEAR50',50,'2026-12-25','2026-12-31','PENDING');
GO

INSERT INTO orders (user_id, total_amount, order_status, shipping_address, phone, payment_status, coupon_id)
VALUES
(4, 1500000, 'DELIVERED', '123 Main St, Hanoi', '0900000004', 'PAID', 1),
(5, 3200000, 'DELIVERED', '456 Le Loi, Hanoi', '0900000005', 'PAID', 2),
(6, 1800000, 'SHIPPED', '789 Tran Hung Dao, HCM', '0900000006', 'PAID', NULL),
(7, 2500000, 'CONFIRMED', '101 Nguyen Hue, HCM', '0900000007', 'PENDING', 3),
(8, 1900000, 'DELIVERED', '202 Pham Van Dong, Hanoi', '0900000008', 'PAID', NULL);
GO

INSERT INTO order_details (order_id, product_id, quantity, unit_price, subtotal)
VALUES
(1, 1, 1, 1500000, 1500000),
(2, 2, 1, 3200000, 3200000),
(3, 3, 1, 1800000, 1800000),
(4, 4, 1, 2500000, 2500000),
(5, 5, 1, 1900000, 1900000);
GO

INSERT INTO payments (order_id, payment_amount, payment_method, payment_status, transaction_id)
VALUES
(1, 1500000, 'VNPAY', 'SUCCESS', 'TXN001'),
(2, 3200000, 'VNPAY', 'SUCCESS', 'TXN002'),
(3, 1800000, 'COD', 'SUCCESS', 'TXN003'),
(4, 2500000, 'VNPAY', 'PENDING', 'TXN004'),
(5, 1900000, 'COD', 'SUCCESS', 'TXN005');
GO

INSERT INTO invoices (order_id, invoice_number, total_amount, tax_amount, discount_amount)
VALUES
(1, 'INV001', 1500000, 150000, 150000),
(2, 'INV002', 3200000, 320000, 480000),
(3, 'INV003', 1800000, 180000, 0),
(4, 'INV004', 2500000, 250000, 500000),
(5, 'INV005', 1900000, 190000, 0);
GO

INSERT INTO feedbacks (user_id, product_id, rating, comment, is_approved)
VALUES
(4, 1, 5, N'Giày rất êm và nhẹ!', 1),
(5, 2, 4, N'Oxfords nhìn rất sang.', 1),
(6, 3, 5, N'Loafer mang đi làm rất tuyệt.', 1),
(7, 4, 5, N'Boot cực kỳ chắc chắn.', 1),
(8, 5, 4, N'Gót cao tôn dáng, rất thích.', 1);
GO

INSERT INTO sliders (slider_title, image_url, position, is_active)
VALUES
(N'Spring New Arrivals', '/uploads/1774479419973_41d6c247abc0bb90ed1b2bdb27b4fb72.jpg', 1, 1),
(N'Massive Summer Sale', '/uploads/1774489320710_sport-shoes-sale-banner-1-5fe0c471dbecb.png', 2, 1),
(N'Adidas Ultraboost Release', '/uploads/1774489348781_shoe-sale-banner-vector.jpg', 3, 1),
(N'Back To School Offers', '/uploads/1774489495053_41d6c247abc0bb90ed1b2bdb27b4fb72.jpg', 4, 1),
(N'Nike Special Edition', '/uploads/1774489370557_shoes-sale-bannuer-design-template-38d8c87b5b44afb4906d2d55743a98ae_screen.jpg', 5, 1);
GO

INSERT INTO contents (content_title, content_text, image_url, content_type)
VALUES
(N'10 Tips To Maintain Your Sneakers',
 N'Keep your sneakers clean and fresh with these 10 easy tips...',
 '/uploads/contents/1774489396142_creative-promotional-banner-sneaker-brand-260nw-2607988933.webp',
 'BANNER'),

(N'Upcoming Summer Promo Event',
 N'Get ready for the biggest sneaker sale of the year. Save up to 50%!',
 '/uploads/contents/1774489403342_sport-shoes-sale-banner-1-5fe0c471dbecb.png',
 'PROMO'),

(N'About Our Journey',
 N'Founded in 2025 by sneaker enthusiasts, we aim to provide the best quality footwear experience.',
 '/uploads/contents/1774489409447_shoes-sale-bannuer-design-template-38d8c87b5b44afb4906d2d55743a98ae_screen.jpg',
 'ABOUT'),

(N'Nike vs Adidas: The 2026 Showdown',
 N'Comparing the latest running technologies from the top two giants.',
 '/uploads/contents/1774489415675_41d6c247abc0bb90ed1b2bdb27b4fb72.jpg',
 'BANNER');
 INSERT INTO slider_products (slider_id,product_id,discount) 
 VALUES
 (1, 2, 0),
 (1, 3, 30),
 (1, 4, 30),
 (2, 2, 20),
 (2, 3, 40),
 (2, 4, 10),
 (3, 2, 10),
 (3, 3, 20),
 (3, 4, 30),
 (3, 5, 50),
 (4, 2, 10),
 (4, 3, 20),
 (4, 4, 50),
 (4, 5, 10),
 (5, 3, 20),
 (5, 4, 10),
 (5, 5, 40);
 INSERT INTO slider_coupons (slider_id,coupon_id)
 VALUES
 (1,3),
 (1,4),
 (2,9),
 (2,10),
 (2,11),
 (2,12),
 (3,4),
 (3,6),
 (4,4),
 (4,6),
 (5,6),
 (5,7);
-- Cho phép m?t kh?u ???c ?? tr?ng (vì ??ng nh?p Google không dùng m?t kh?u h? th?ng)
ALTER TABLE users ALTER COLUMN password_hash NVARCHAR(255) NULL;

-- Thêm c?t l?u nhà cung c?p (GOOGLE) và ID c?a Google
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'auth_provider')
BEGIN
    ALTER TABLE users ADD auth_provider NVARCHAR(20);
END

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'provider_id')
BEGIN
    ALTER TABLE users ADD provider_id NVARCHAR(50);
END
GO

-- Bảng Marketing Plans
CREATE TABLE marketing_plans (
    plan_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    assigned_role NVARCHAR(50),
    start_date DATE,
    end_date DATE,
    status NVARCHAR(20) DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng trung gian Coupon - Product (khi scope = SPECIFIC_PRODUCTS)
CREATE TABLE coupon_products (
    coupon_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (coupon_id, product_id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
GO
