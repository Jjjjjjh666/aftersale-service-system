-- 售后数据库
CREATE DATABASE IF NOT EXISTS aftersale_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE aftersale_db;

-- 售后单表
CREATE TABLE IF NOT EXISTS aftersale_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '售后单ID',
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    type INT NOT NULL COMMENT '售后类型 0-退货 1-换货 2-维修',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态 PENDING-待审核 APPROVED-已审核 CANCELLED-已取消',
    reason VARCHAR(500) COMMENT '售后原因',
    conclusion VARCHAR(500) COMMENT '审核结论',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_shop_id (shop_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后单表';

-- 插入测试数据
INSERT INTO aftersale_order (id, shop_id, order_id, type, status, reason, created_at, updated_at) VALUES
(1, 1, 100, 0, 'PENDING', '商品质量问题', NOW(), NOW()),
(2, 1, 101, 1, 'PENDING', '商品不符合描述', NOW(), NOW()),
(3, 1, 102, 2, 'PENDING', '商品需要维修', NOW(), NOW()),
(4, 1, 103, 0, 'APPROVED', '退货申请', NOW(), NOW()),
(5, 1, 104, 1, 'APPROVED', '换货申请', NOW(), NOW()),
(6, 1, 105, 2, 'APPROVED', '维修申请', NOW(), NOW());

