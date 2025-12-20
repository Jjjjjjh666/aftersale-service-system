# 售后服务系统 - 项目总览

## 🎯 项目完成状态

✅ **所有任务已完成！**

- [x] 创建Maven多模块项目结构
- [x] 设计售后单多态架构（退货/换货/维修）
- [x] 实现商户审核售后API（使用策略模式）
- [x] 实现商户取消售后API（使用策略模式）
- [x] 实现平台管理员审核服务商变更API
- [x] 创建数据库表设计和SQL脚本
- [x] 编写API测试文件和使用文档
- [x] 完善配置文件和启动类

## 📁 项目结构

```
aftersale-service-system/
│
├── README.md                    # 快速开始指南
├── ARCHITECTURE.md              # 详细架构设计文档
├── start.sh                     # 快速启动脚本
├── pom.xml                      # 父项目POM
│
├── common/                      # 公共模块
│   ├── pom.xml
│   └── src/main/java/cn/edu/xmu/common/
│       ├── model/              # ReturnNo, ReturnObject
│       ├── exception/          # BusinessException, GlobalExceptionHandler
│       └── util/               # JacksonUtil
│
├── aftersale/                  # 售后模块（端口8081）
│   ├── pom.xml
│   └── src/main/java/cn/edu/xmu/aftersale/
│       ├── model/              
│       │   ├── AftersaleOrder.java          # 领域对象
│       │   ├── AftersaleType.java           # 类型枚举
│       │   ├── AftersaleStatus.java         # 状态枚举
│       │   └── strategy/                    # 策略模式核心
│       │       ├── AftersaleConfirmStrategy.java    # 审核策略接口
│       │       ├── AftersaleCancelStrategy.java     # 取消策略接口
│       │       └── impl/
│       │           ├── ReturnConfirmStrategy.java   # 退货审核
│       │           ├── ExchangeConfirmStrategy.java # 换货审核
│       │           ├── RepairConfirmStrategy.java   # 维修审核（调用service）
│       │           ├── ReturnCancelStrategy.java    # 退货取消
│       │           ├── ExchangeCancelStrategy.java  # 换货取消
│       │           └── RepairCancelStrategy.java    # 维修取消（调用service）
│       ├── dao/                
│       │   ├── po/AftersaleOrderPo.java
│       │   ├── AftersaleOrderMapper.java
│       │   └── AftersaleOrderRepository.java
│       ├── service/
│       │   └── AftersaleService.java        # 服务层（策略选择）
│       ├── controller/
│       │   ├── AftersaleController.java     # API控制器
│       │   └── dto/                         # 请求DTO
│       ├── client/
│       │   ├── ServiceClient.java           # OpenFeign客户端
│       │   └── dto/
│       └── AftersaleApplication.java
│
├── service/                    # 服务模块（端口8082）
│   ├── pom.xml
│   └── src/main/java/cn/edu/xmu/service/
│       ├── model/
│       │   ├── ServiceProviderDraft.java    # 服务商草稿领域对象
│       │   └── DraftStatus.java             # 草稿状态枚举
│       ├── dao/
│       │   ├── po/
│       │   │   ├── ServiceProviderDraftPo.java
│       │   │   └── ServiceOrderPo.java
│       │   ├── ServiceProviderDraftMapper.java
│       │   ├── ServiceOrderMapper.java
│       │   └── ServiceProviderDraftRepository.java
│       ├── service/
│       │   ├── ServiceProviderService.java  # 服务商业务
│       │   └── ServiceOrderService.java     # 服务单业务
│       ├── controller/
│       │   ├── ServiceProviderController.java       # 服务商API
│       │   ├── ServiceOrderInternalController.java  # 内部API
│       │   └── dto/
│       └── ServiceApplication.java
│
├── sql/                        # 数据库脚本
│   ├── aftersale_db.sql       # 售后数据库（含测试数据）
│   └── service_db.sql         # 服务数据库（含测试数据）
│
└── test/                       # API测试文件
    ├── aftersale-api.http     # 售后模块API测试
    └── service-api.http       # 服务模块API测试
```

## 🚀 快速开始

### 方法1：使用启动脚本（推荐）

```bash
cd /Users/jhlee/Desktop/ooad/aftersale-service-system
./start.sh
```

### 方法2：手动启动

```bash
# 1. 初始化数据库
mysql -uroot -p < sql/aftersale_db.sql
mysql -uroot -p < sql/service_db.sql

# 2. 启动service模块
cd service
mvn spring-boot:run &

# 3. 启动aftersale模块
cd ../aftersale
mvn spring-boot:run &
```

## 📝 API接口概览

### 售后模块（http://localhost:8081）

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 商户审核售后 | PUT | `/shops/{shopid}/aftersaleorders/{id}/confirm` | 多态处理不同类型售后 |
| 商户取消售后 | DELETE | `/shops/{shopid}/aftersaleorders/{id}/cancel` | 多态处理不同类型取消 |

### 服务模块（http://localhost:8082）

| API | 方法 | 路径 | 功能 |
|-----|------|------|------|
| 审核服务商变更 | PUT | `/draft/{draftid}/review` | 平台管理员审核 |
| 创建服务单 | POST | `/internal/shops/{shopId}/aftersales/{id}/serviceorders` | 内部接口 |

## 🎨 核心设计亮点

### 1. 策略模式实现多态

```java
// 售后审核 - 根据类型自动选择策略
AftersaleConfirmStrategy strategy = confirmStrategies.stream()
    .filter(s -> s.support(order.getType()))
    .findFirst()
    .orElseThrow();

strategy.confirm(order, confirm, conclusion);  // 多态调用
```

**三种策略的差异**：
- **退货/换货**：仅更新售后单状态
- **维修**：额外调用service模块创建服务单（跨模块通信）

### 2. 模块间通信

```java
// 维修策略中调用service模块
@Component
@RequiredArgsConstructor
public class RepairConfirmStrategy implements AftersaleConfirmStrategy {
    private final ServiceClient serviceClient;  // OpenFeign客户端
    
    @Override
    public void confirm(AftersaleOrder order, Boolean confirm, String conclusion) {
        if (confirm) {
            // 跨模块调用
            serviceClient.createServiceOrder(shopId, aftersalesId, request);
        }
    }
}
```

### 3. 状态机控制

```
PENDING (待审核) → APPROVED (已审核) → CANCELLED (已取消)
     ↑                                      ↑
     只能审核                               只能取消
```

## 🧪 测试示例

### 测试1：维修审核（验证跨模块调用）

```bash
# 1. 审核维修售后单（ID=3）
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/3/confirm \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "conclusion": "同意维修"}'

# 观察日志输出：
# [aftersale] 执行维修审核策略
# [aftersale] 准备创建服务单
# [service]  收到创建服务单请求
# [service]  服务单创建成功: id=1
```

### 测试2：状态机验证

```bash
# 尝试审核已经审核过的售后单（应该失败）
curl -X PUT http://localhost:8081/shops/1/aftersaleorders/4/confirm \
  -H "Content-Type: application/json" \
  -d '{"confirm": true, "conclusion": "测试"}'

# 预期响应：
# {"errno": 602, "errmsg": "只有待审核状态的售后单才能进行审核"}
```

## 📊 测试数据

### aftersale_order表

| ID | type | status | 说明 | 用途 |
|----|------|--------|------|------|
| 1 | 0 | PENDING | 待审核-退货 | 测试退货审核 |
| 2 | 1 | PENDING | 待审核-换货 | 测试换货审核 |
| 3 | 2 | PENDING | 待审核-维修 | **测试维修审核（会创建服务单）** |
| 4 | 0 | APPROVED | 已审核-退货 | 测试退货取消 |
| 5 | 1 | APPROVED | 已审核-换货 | 测试换货取消 |
| 6 | 2 | APPROVED | 已审核-维修 | **测试维修取消（会取消服务单）** |

### service_provider_draft表

| ID | provider_name | status | 用途 |
|----|--------------|--------|------|
| 1 | 张三维修服务 | PENDING | 测试审核通过 |
| 2 | 李四售后服务 | PENDING | 测试审核拒绝 |
| 3 | 王五技术服务 | APPROVED | 测试状态校验 |

## 📚 文档索引

- **README.md** - 快速开始和API测试
- **ARCHITECTURE.md** - 详细架构设计文档
- **test/aftersale-api.http** - 售后模块API测试用例
- **test/service-api.http** - 服务模块API测试用例

## 🎓 学习要点

本项目完美展示了以下面向对象设计原则：

1. **单一职责原则**：每个类只负责一件事
2. **开闭原则**：对扩展开放，对修改关闭
3. **里氏替换原则**：子类可以替换父类
4. **接口隔离原则**：接口职责清晰
5. **依赖倒置原则**：依赖抽象而非具体实现

## 🔧 技术栈

- Java 17
- Spring Boot 3.2.5
- Spring Cloud OpenFeign 2023.0.1
- MyBatis 3.0.3
- MySQL 8.0+
- Maven 3.8+

## 💡 扩展建议

1. **新增售后类型**：只需实现新的Strategy类
2. **添加缓存**：使用Redis缓存售后单信息
3. **异步处理**：使用消息队列处理服务单创建
4. **监控告警**：添加Prometheus监控

## 📞 联系方式

如有问题，请查看：
1. API文档：`README.md`
2. 架构文档：`ARCHITECTURE.md`
3. 测试用例：`test/`目录

---

**项目创建时间**：2025-12
**面向对象设计**：策略模式 + DDD分层架构
**适用场景**：教学、面试、实际项目参考

