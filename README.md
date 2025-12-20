# 售后服务系统 - API测试指南

## 系统概述

本系统包含两个模块：
1. **aftersale模块**（端口8081）：售后管理模块
2. **service模块**（端口8082）：服务商管理模块

## 核心设计

### 1. 售后模块多态设计

使用策略模式处理三种售后类型：

- **退货（type=0）**：`ReturnConfirmStrategy` / `ReturnCancelStrategy`
- **换货（type=1）**：`ExchangeConfirmStrategy` / `ExchangeCancelStrategy`
- **维修（type=2）**：`RepairConfirmStrategy` / `RepairCancelStrategy`

维修类型的特殊之处：
- 审核通过时自动调用service模块创建服务单
- 取消时自动调用service模块取消服务单

## 数据库初始化

```bash
# 1. 初始化售后数据库
mysql -uroot -p < sql/aftersale_db.sql

# 2. 初始化服务数据库
mysql -uroot -p < sql/service_db.sql
```

## 启动服务

```bash
# 1. 启动service模块（必须先启动，aftersale模块会调用它）
cd service
mvn spring-boot:run

# 2. 启动aftersale模块
cd aftersale
mvn spring-boot:run
```

## API测试

### 售后模块API

#### 1. 商户审核售后 - PUT /shops/{shopid}/aftersaleorders/{id}/confirm

**测试用例**：

```bash
# 审核通过 - 退货（ID=1）
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/1/confirm \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "conclusion": "同意退货"}'

# 审核通过 - 维修（ID=3，会自动创建服务单）
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/3/confirm \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "conclusion": "同意维修"}'

# 审核拒绝
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/1/confirm \
  -H "Content-Type: application/json" \
  -d '{"confirm": false, "conclusion": "拒绝退货"}'
```

**预期响应**：
```json
{
  "errno": 0,
  "errmsg": "成功",
  "status": "APPROVED"
}
```

#### 2. 商户取消售后 - DELETE /shops/{shopid}/aftersaleorders/{id}/cancel

**测试用例**：

```bash
# 取消退货（ID=4，状态为APPROVED）
curl -X DELETE http://localhost:8081/shops/1/aftersaleorders/4/cancel \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "reason": "客户要求取消"}'

# 取消维修（ID=6，会同时取消服务单）
curl -X DELETE http://localhost:8081/shops/1/aftersaleorders/6/cancel \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "reason": "维修服务商无法提供服务"}'
```

**预期响应**：
```json
{
  "errno": 0,
  "errmsg": "成功",
  "status": "CANCELLED"
}
```

### 服务模块API

#### 3. 平台管理员审核服务商变更 - PUT /draft/{draftid}/review

**测试用例**：

```bash
# 审核通过（ID=1）
curl -X PUT http://localhost:8082/draft/1/review \
  -H "Content-Type: application/json" \
  -d '{"conclusion": 1, "opinion": "审核通过"}'

# 审核拒绝（ID=2）
curl -X PUT http://localhost:8082/draft/2/review \
  -H "Content-Type: application/json" \
  -d '{"conclusion": 0, "opinion": "资质不符合要求"}'
```

**预期响应**：
```json
{
  "errno": 0,
  "errmsg": "成功"
}
```

#### 4. 服务商接受服务单 - PUT /serviceprovider/{did}/serviceorders/{id}/accept

**测试用例**：

```bash
# 接受上门维修服务单（ID=1，type=0）
curl -X PUT http://localhost:8082/serviceprovider/1/serviceorders/1/accept

# 接受寄修服务单（ID=2，type=1）
curl -X PUT http://localhost:8082/serviceprovider/2/serviceorders/2/accept
```

**预期响应**：
```json
{
  "errno": 0,
  "errmsg": "成功"
}
```

**多态行为**：
- **上门维修**：日志显示"执行上门维修接受策略" + 安排技师和预约时间
- **寄修**：日志显示"执行寄修接受策略" + 生成寄修地址

#### 5. 服务商取消服务单 - DELETE /serviceprovider/{did}/serviceorders/{id}/cancel

**测试用例**：

```bash
# 取消上门维修服务单（ID=3，type=0）
curl -X DELETE http://localhost:8082/serviceprovider/1/serviceorders/3/cancel

# 取消寄修服务单（ID=4，type=1）
curl -X DELETE http://localhost:8082/serviceprovider/2/serviceorders/4/cancel
```

**预期响应**：
```json
{
  "errno": 0,
  "errmsg": "成功"
}
```

**多态行为**：
- **上门维修**：日志显示"执行上门维修取消策略" + 通知客户和技师
- **寄修**：日志显示"执行寄修取消策略" + 处理物流拦截

### service_order表

| ID | aftersales_id | service_provider_id | type | status | 说明 |
|----|---------------|---------------------|------|--------|------|
| 1 | 3 | NULL | 0 | CREATED | 待接受-上门维修 |
| 2 | 6 | NULL | 1 | CREATED | 待接受-寄修 |
| 3 | 102 | 1 | 0 | ACCEPTED | 已接受-上门维修 |
| 4 | 103 | 2 | 1 | ACCEPTED | 已接受-寄修 |

| ID | shop_id | type | status | 说明 |
|----|---------|------|--------|------|
| 1 | 1 | 0 | PENDING | 待审核-退货 |
| 2 | 1 | 1 | PENDING | 待审核-换货 |
| 3 | 1 | 2 | PENDING | 待审核-维修 |
| 4 | 1 | 0 | APPROVED | 已审核-退货 |
| 5 | 1 | 1 | APPROVED | 已审核-换货 |
| 6 | 1 | 2 | APPROVED | 已审核-维修 |

### service_provider_draft表

| ID | provider_name | status | 说明 |
|----|--------------|--------|------|
| 1 | 张三维修服务 | PENDING | 待审核 |
| 2 | 李四售后服务 | PENDING | 待审核 |
| 3 | 王五技术服务 | APPROVED | 已审核 |

## 多态行为验证

### 1. 验证售后审核策略多态

```bash
# 退货策略
PUT /shops/1/aftersaleorders/1/confirm (type=0)
# 观察日志: "执行退货审核策略"

# 换货策略
PUT /shops/1/aftersaleorders/2/confirm (type=1)
# 观察日志: "执行换货审核策略"

# 维修策略（会调用service模块）
PUT /shops/1/aftersaleorders/3/confirm (type=2)
# 观察日志: "执行维修审核策略" + "创建服务单"
```

### 2. 验证售后取消策略多态

```bash
# 退货取消
DELETE /shops/1/aftersaleorders/4/cancel (type=0)
# 观察日志: "执行退货取消策略"

# 维修取消（会调用service模块）
DELETE /shops/1/aftersaleorders/6/cancel (type=2)
# 观察日志: "执行维修取消策略" + "取消服务单"
```

### 3. 验证服务单接受策略多态

```bash
# 上门维修接受
PUT /serviceprovider/1/serviceorders/1/accept (type=0)
# 观察日志: "执行上门维修接受策略" + "分配技师"

# 寄修接受
PUT /serviceprovider/2/serviceorders/2/accept (type=1)
# 观察日志: "执行寄修接受策略" + "生成寄修地址"
```

### 4. 验证服务单取消策略多态

```bash
# 上门维修取消
DELETE /serviceprovider/1/serviceorders/3/cancel (type=0)
# 观察日志: "执行上门维修取消策略" + "通知客户和技师"

# 寄修取消
DELETE /serviceprovider/2/serviceorders/4/cancel (type=1)
# 观察日志: "执行寄修取消策略" + "处理物流拦截"
```

## 异常场景测试

### 1. 状态校验

```bash
# 已审核状态无法再次审核
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/4/confirm \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "conclusion": "测试"}'
# 预期: errno=602, errmsg="只有待审核状态的售后单才能进行审核"

# 待审核状态无法取消
curl -X DELETE http://localhost:8081/shops/1/aftersaleorders/1/cancel \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "reason": "测试"}'
# 预期: errno=602, errmsg="只有已审核状态的售后单才能取消"
```

### 2. 参数校验

```bash
# confirm参数缺失
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/1/confirm \
  -H "Content-Type: application/json" \
  -d '{"conclusion": "测试"}'
# 预期: errno=400, errmsg="请求参数错误"
```

## 项目结构

```
aftersale-service-system/
├── common/                      # 公共模块
│   └── src/main/java/
│       └── cn/edu/xmu/common/
│           ├── model/          # 通用模型
│           ├── exception/      # 异常处理
│           └── util/           # 工具类
├── aftersale/                  # 售后模块
│   └── src/main/java/
│       └── cn/edu/xmu/aftersale/
│           ├── model/          # 领域模型
│           │   └── strategy/  # 策略模式实现
│           ├── dao/            # 数据访问层
│           ├── service/        # 业务服务层
│           ├── controller/     # 控制器层
│           └── client/         # OpenFeign客户端
├── service/                    # 服务模块
│   └── src/main/java/
│       └── cn/edu/xmu/service/
│           ├── model/          # 领域模型
│           ├── dao/            # 数据访问层
│           ├── service/        # 业务服务层
│           └── controller/     # 控制器层
└── sql/                        # SQL脚本
    ├── aftersale_db.sql
    └── service_db.sql
```

## 关键设计要点

1. **策略模式**：
   - 售后模块：不同类型的售后单有不同的审核和取消策略
   - 服务模块：不同类型的服务单有不同的接受和取消策略
2. **模块间通信**：使用OpenFeign实现跨模块调用
3. **DDD设计**：清晰的分层架构（model-dao-service-controller）
4. **状态机**：售后单、服务单和草稿的状态流转控制
5. **面向对象**：充分利用多态实现业务逻辑的扩展性

## 多态策略总览

### 售后模块策略
- **审核策略**：`ReturnConfirmStrategy`、`ExchangeConfirmStrategy`、`RepairConfirmStrategy`
- **取消策略**：`ReturnCancelStrategy`、`ExchangeCancelStrategy`、`RepairCancelStrategy`

### 服务模块策略
- **接受策略**：`OnsiteAcceptStrategy`（上门维修）、`MailInAcceptStrategy`（寄修）
- **取消策略**：`OnsiteCancelStrategy`（上门维修）、`MailInCancelStrategy`（寄修）

