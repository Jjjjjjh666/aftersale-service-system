# 数据库配置说明

## 远程MySQL服务器配置

本项目已配置连接到远程MySQL服务器：

```yaml
数据库地址: 124.70.89.47:3306
数据库名: aftersales_db
用户名: root  
密码: 123245
```

## 数据库表说明

### 1. aftersales 表（售后单表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| shop_id | BIGINT | 店铺ID |
| order_id | BIGINT | 订单ID |
| type | TINYINT | **售后类型: 0-换货, 1-退货, 2-维修** |
| status | TINYINT | **状态: 0-待审核, 1-已通过, 2-已拒绝** |
| reason | VARCHAR(500) | 售后原因 |
| conclusion | VARCHAR(500) | 审核结论 |
| gmt_create | DATETIME | 创建时间 |
| gmt_modified | DATETIME | 修改时间 |

### 2. service_order 表（服务单表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| shop_id | BIGINT | 店铺ID |
| aftersales_id | BIGINT | 售后单ID |
| type | TINYINT | **服务类型: 0-上门, 1-寄修, 2-其他** |
| status | TINYINT | **状态: 0-待接受, 1-已接受, 2-已取消** |
| consignee | VARCHAR(100) | 收件人 |
| mobile | VARCHAR(20) | 手机号 |
| address | VARCHAR(500) | 服务地址 |
| gmt_create | DATETIME | 创建时间 |
| gmt_modified | DATETIME | 修改时间 |

## 环境变量配置

可以通过环境变量覆盖默认配置：

```bash
export MYSQL_HOST=124.70.89.47
export MYSQL_DATABASE=aftersales_db
export MYSQL_USER=root
export MYSQL_PASSWORD=123245
```

或在IDEA中配置运行参数：

```
-DMYSQL_HOST=124.70.89.47
-DMYSQL_DATABASE=aftersales_db
-DMYSQL_USER=root
-DMYSQL_PASSWORD=123245
```

## 测试数据

数据库中已有测试数据：

### aftersales 表
- ID=1: type=2 (维修), status=0 (待审核)
- ID=2: type=1 (退货), status=0 (待审核)
- ID=3: type=0 (换货), status=0 (待审核)
- ID=4: type=2 (维修), status=0 (待审核)

## 连接测试

启动项目后，访问Druid监控页面：
- http://localhost:8081/druid/
- 用户名: admin
- 密码: 123456

可以查看数据库连接状态和SQL执行情况。

