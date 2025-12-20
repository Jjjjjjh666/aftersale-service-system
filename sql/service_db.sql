-- 服务数据库
CREATE DATABASE IF NOT EXISTS service_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE service_db;

-- 服务商变更草稿表
CREATE TABLE IF NOT EXISTS service_provider_draft (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '草稿ID',
    service_provider_id BIGINT NOT NULL COMMENT '服务商ID',
    provider_name VARCHAR(100) NOT NULL COMMENT '服务商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态 PENDING-待审核 APPROVED-审核通过 REJECTED-审核拒绝',
    opinion VARCHAR(500) COMMENT '审核意见',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_service_provider_id (service_provider_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商变更草稿表';

-- 服务单表
CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务单ID',
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    aftersales_id BIGINT NOT NULL COMMENT '售后单ID',
    service_provider_id BIGINT COMMENT '服务商ID',
    type INT NOT NULL DEFAULT 0 COMMENT '服务类型 0-上门 1-寄修 2-其他',
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED' COMMENT '状态 CREATED-已创建 ACCEPTED-已接受 CANCELLED-已取消',
    consignee VARCHAR(200) COMMENT '收件人信息',
    address VARCHAR(500) COMMENT '服务地址',
    tracking_number VARCHAR(100) COMMENT '物流单号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_shop_id (shop_id),
    INDEX idx_aftersales_id (aftersales_id),
    INDEX idx_service_provider_id (service_provider_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务单表';

-- 插入测试数据
INSERT INTO service_provider_draft (id, service_provider_id, provider_name, contact_person, contact_phone, address, status, created_at, updated_at) VALUES
(1, 1, '张三维修服务', '张三', '13800138000', '北京市朝阳区XX街道', 'PENDING', NOW(), NOW()),
(2, 2, '李四售后服务', '李四', '13900139000', '上海市浦东新区YY路', 'PENDING', NOW(), NOW()),
(3, 3, '王五技术服务', '王五', '13700137000', '深圳市南山区ZZ大道', 'APPROVED', NOW(), NOW());

-- 插入服务单测试数据
INSERT INTO service_order (id, shop_id, aftersales_id, service_provider_id, type, status, consignee, created_at, updated_at) VALUES
(1, 1, 3, NULL, 0, 'CREATED', '张三', NOW(), NOW()),  -- 待接受-上门维修
(2, 1, 6, NULL, 1, 'CREATED', '李四', NOW(), NOW()),  -- 待接受-寄修
(3, 1, 102, 1, 0, 'ACCEPTED', '王五', NOW(), NOW()),  -- 已接受-上门维修
(4, 1, 103, 2, 1, 'ACCEPTED', '赵六', NOW(), NOW());  -- 已接受-寄修

