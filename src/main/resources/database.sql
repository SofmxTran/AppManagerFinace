-- Tạo database
CREATE DATABASE IF NOT EXISTS finance_manager;
USE finance_manager;

-- Tạo bảng categories
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng transactions
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Thêm một số danh mục mẫu
INSERT INTO categories (name, description) VALUES
('Lương', 'Thu nhập từ lương'),
('Thưởng', 'Thu nhập từ thưởng'),
('Ăn uống', 'Chi phí ăn uống hàng ngày'),
('Di chuyển', 'Chi phí xăng xe, taxi, grab'),
('Mua sắm', 'Chi phí mua sắm cá nhân'),
('Giải trí', 'Chi phí giải trí, xem phim, cafe'),
('Hóa đơn', 'Chi phí điện, nước, internet'),
('Khác', 'Các khoản chi phí khác');

-- Thêm một số giao dịch mẫu
INSERT INTO transactions (category_id, amount, description, type, date) VALUES
(1, 15000000, 'Lương tháng 3', 'INCOME', NOW()),
(3, 200000, 'Ăn trưa', 'EXPENSE', NOW()),
(4, 50000, 'Xăng xe', 'EXPENSE', NOW()),
(2, 1000000, 'Thưởng dự án', 'INCOME', NOW()); 