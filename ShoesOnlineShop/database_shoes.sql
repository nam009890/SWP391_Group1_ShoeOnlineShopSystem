-- Tạo database (chạy riêng nếu cần)

CREATE DATABASE ShoesOnlineShop
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_name NVARCHAR(50) UNIQUE NOT NULL,
    user_email NVARCHAR(100) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(MAX),
    user_role NVARCHAR(20) NOT NULL 
        CHECK (user_role IN ('ADMIN', 'SALE_STAFF', 'CUSTOMER')),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng Products
CREATE TABLE products (
    product_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_name NVARCHAR(200) NOT NULL,
    product_description NVARCHAR(MAX),
    product_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url NVARCHAR(500),
    category_name NVARCHAR(100),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- Bảng Coupons
CREATE TABLE coupons (
    coupon_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    coupon_name NVARCHAR(200) NOT NULL,
    coupon_code NVARCHAR(50) UNIQUE NOT NULL,
    discount_percent INT NOT NULL 
        CHECK (discount_percent >= 0 AND discount_percent <= 100),
    create_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BIT DEFAULT 1,
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
    link_url NVARCHAR(500),
    position INT DEFAULT 0,
    is_active BIT DEFAULT 1,
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

INSERT INTO products (product_name, product_description, product_price, stock_quantity, category_name)
VALUES
('Nike Air Force 1','Classic white sneaker',2500000,100,'Sneaker'),
('Adidas Ultraboost','Running shoes',3200000,80,'Running'),
('Puma RS-X','Sport sneaker',2100000,60,'Sneaker'),
('Converse Chuck 70','Canvas high-top',1800000,120,'Sneaker'),
('Vans Old Skool','Skate shoe',1700000,90,'Sneaker'),
('Nike Air Jordan 1','Basketball shoes',4500000,50,'Basketball'),
('Adidas Superstar','Classic shell toe',2200000,70,'Sneaker'),
('MLB Chunky Liner','Korean style sneaker',2400000,85,'Sneaker'),
('Bitis Hunter X','Vietnam sneaker',1200000,150,'Sneaker'),
('New Balance 550','Retro sneaker',3000000,65,'Sneaker');

INSERT INTO coupons (coupon_name, coupon_code, discount_percent, create_date, end_date)
VALUES
('New Year Sale','NY2026',10,'2026-01-01','2026-12-31'),
('Summer Sale','SUMMER10',10,'2026-03-01','2026-09-01'),
('VIP Customer','VIP20',20,'2026-01-01','2026-12-31'),
('Flash Sale','FLASH15',15,'2026-02-01','2026-06-01'),
('Student Discount','STUDENT5',5,'2026-01-01','2026-12-31'),
('Black Friday','BF30',30,'2026-11-01','2026-11-30'),
('Shopee Style','SHOES10',10,'2026-01-01','2026-12-31'),
('Holiday Sale','HOLIDAY15',15,'2026-04-01','2026-12-31'),
('Member Discount','MEMBER10',10,'2026-01-01','2026-12-31'),
('Mega Sale','MEGA25',25,'2026-05-01','2026-05-31');

INSERT INTO orders (user_id, total_amount, order_status, shipping_address, phone,payment_status, coupon_id)
VALUES
(4,2500000,'DELIVERED','Hanoi', '0123456789','PAID',1),
(5,3200000,'DELIVERED','Hanoi','0123456789','PAID',2),
(6,1800000,'SHIPPED','Hanoi','0123456789','PAID',NULL),
(7,4500000,'CONFIRMED','Hanoi','0123456789','PENDING',3),
(8,1700000,'DELIVERED','Hanoi','0123456789','PAID',NULL),
(9,2200000,'DELIVERED','Hanoi','0123456789','PAID',1),
(10,2400000,'SHIPPED','Hanoi','0123456789','PAID',NULL),
(4,1200000,'DELIVERED','Hanoi','0123456789','PAID',NULL),
(5,3000000,'PENDING','Hanoi','0123456789','PENDING',2),
(6,2100000,'CONFIRMED','Hanoi','0123456789','PENDING',NULL);

INSERT INTO order_details (order_id, product_id, quantity, unit_price, subtotal)
VALUES
(1,1,1,2500000,2500000),
(2,2,1,3200000,3200000),
(3,4,1,1800000,1800000),
(4,6,1,4500000,4500000),
(5,5,1,1700000,1700000),
(6,7,1,2200000,2200000),
(7,8,1,2400000,2400000),
(8,9,1,1200000,1200000),
(9,10,1,3000000,3000000),
(10,3,1,2100000,2100000);

INSERT INTO payments (order_id, payment_amount, payment_method, payment_status, transaction_id)
VALUES
(1,2500000,'VNPAY','SUCCESS','TXN001'),
(2,3200000,'VNPAY','SUCCESS','TXN002'),
(3,1800000,'COD','SUCCESS','TXN003'),
(4,4500000,'VNPAY','PENDING','TXN004'),
(5,1700000,'COD','SUCCESS','TXN005'),
(6,2200000,'VNPAY','SUCCESS','TXN006'),
(7,2400000,'VNPAY','SUCCESS','TXN007'),
(8,1200000,'COD','SUCCESS','TXN008'),
(9,3000000,'VNPAY','PENDING','TXN009'),
(10,2100000,'COD','PENDING','TXN010');

INSERT INTO invoices (order_id, invoice_number, total_amount, tax_amount, discount_amount)
VALUES
(1,'INV001',2500000,250000,0),
(2,'INV002',3200000,320000,0),
(3,'INV003',1800000,180000,0),
(4,'INV004',4500000,450000,0),
(5,'INV005',1700000,170000,0),
(6,'INV006',2200000,220000,0),
(7,'INV007',2400000,240000,0),
(8,'INV008',1200000,120000,0),
(9,'INV009',3000000,300000,0),
(10,'INV010',2100000,210000,0);

INSERT INTO feedbacks (user_id, product_id, rating, comment, is_approved)
VALUES
(4,1,5,N'Giày rất đẹp và êm chân',1),
(5,2,4,N'Chạy rất thích',1),
(6,4,5,N'Form chuẩn',1),
(7,6,5,N'Jordan quá đỉnh',1),
(8,5,4,N'Giá hợp lý',1),
(9,7,5,N'Classic style',1),
(10,8,4,N'Phù hợp đi chơi',1),
(4,9,5,N'Ủng hộ hàng Việt',1),
(5,10,5,N'Rất chất lượng',1),
(6,3,4,N'Laptop? À nhầm, giày đẹp',1);

INSERT INTO sliders (slider_title, image_url, link_url, position)
VALUES
(N'New Year Sale 2026','/images/slider1.jpg','/shop',1),
(N'Summer Collection','/images/slider2.jpg','/category/sneaker',2),
(N'Jordan Special Offer','/images/slider3.jpg','/product/6',3),
(N'Running Shoes Discount','/images/slider4.jpg','/category/running',4),
(N'Vietnam Brand Bitis','/images/slider5.jpg','/product/9',5);

INSERT INTO contents (content_title, content_text, image_url, content_type)
VALUES
(N'Big Sneaker Sale',
 N'Up to 30% off for selected sneaker models in 2026 season.',
 '/images/banner1.jpg',
 'BANNER'),

(N'Summer Promo',
 N'Buy 2 get extra 10% discount for all running shoes.',
 '/images/promo1.jpg',
 'PROMO'),

(N'About Our Shop',
 N'Shoe Online Shop was established in 2025 with mission to bring quality shoes to everyone.',
 '/images/about.jpg',
 'ABOUT'),

(N'Holiday Discount',
 N'Special holiday deals for loyal customers.',
 '/images/promo2.jpg',
 'PROMO'),

(N'New Arrival',
 N'Latest sneaker models have arrived. Check them out now!',
 '/images/banner2.jpg',
 'BANNER');

-- Table Deliveries
CREATE TABLE deliveries (
    delivery_id BIGINT IDENTITY(1,1) PRIMARY KEY,

    order_id BIGINT NOT NULL,
    user_id BIGINT NULL, -- shipper (user role = SHIPPER)

    tracking_number NVARCHAR(100) UNIQUE,

    delivery_status NVARCHAR(30) DEFAULT 'PENDING'
        CHECK (delivery_status IN (
            'PENDING',
            'ASSIGNED',
            'PICKED_UP',
            'DELIVERING',
            'DELIVERED',
            'FAILED'
        )),

    shipping_fee DECIMAL(10,2) DEFAULT 0,
    proof_image_url NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    assigned_at DATETIME2 NULL,
    shipped_date DATETIME2 NULL,
    delivered_date DATETIME2 NULL,

    note NVARCHAR(MAX),

    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
GO

ALTER TABLE deliveries
ADD is_deleted BIT DEFAULT 0;
GO
