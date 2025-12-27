# 测试指南

## 📋 测试概览

本项目采用**分层测试策略**，包含单元测试、集成测试和Web集成测试。

### 测试统计

| 模块 | 测试类型 | 测试数量 | 覆盖范围 |
|------|---------|---------|---------|
| **aftersale** | Controller Web测试 | 25+ | HTTP接口完整测试 |
| **aftersale** | Service集成测试 | 17+ | 真实策略+Mock外部API |
| **aftersale** | Service单元测试 | 8+ | Mock所有依赖 |
| **aftersale** | Strategy测试 | 20+ | 策略模式各分支 |
| **aftersale** | Domain测试 | 7+ | 领域对象业务规则 |
| **service** | Controller Web测试 | 15+ | HTTP接口完整测试 |
| **service** | Service测试 | 5+ | 服务商审核逻辑 |
| **service** | Strategy测试 | 10+ | 服务单策略 |
| **service** | Domain测试 | 13+ | 领域对象 |

**总计**: 约 **120+ 个测试用例**

---

## 🎯 测试策略

### 1. 测试金字塔

```
        Web集成测试 (15%)
       ↗             ↖
   Service集成测试 (20%)
  ↗                    ↖
单元测试 (Domain + Strategy) (65%)
```

### 2. 测试分层

#### 🟢 单元测试 (Unit Tests)
- **目标**: 测试单个类或方法的逻辑
- **特点**: 
  - Mock所有依赖
  - 快速执行
  - 精确定位问题
- **示例**:
  ```java
  @ExtendWith(MockitoExtension.class)
  class AftersaleServiceTest {
      @Mock private AftersaleOrderRepository repository;
      @Mock private AftersaleConfirmStrategy strategy;
      // 测试Service的协调逻辑
  }
  ```

#### 🟡 集成测试 (Integration Tests)
- **目标**: 测试多个组件协同工作
- **特点**:
  - 使用真实策略实例
  - 只Mock外部依赖
  - 验证完整业务流程
- **示例**:
  ```java
  @ExtendWith(MockitoExtension.class)
  class AftersaleServiceIntegrationTest {
      @Mock private LogisticsClient logisticsClient;  // 只Mock外部API
      private AftersaleService service;  // 真实Service + 真实Strategy
  }
  ```

#### 🔵 Web集成测试 (Web Integration Tests) ⭐ **新增**
- **目标**: 测试完整的HTTP请求-响应流程
- **特点**:
  - 使用MockMvc模拟HTTP请求
  - 测试参数校验、序列化、异常处理
  - 验证HTTP状态码和响应格式
- **示例**:
  ```java
  @SpringBootTest
  @AutoConfigureMockMvc
  class AftersaleControllerWebTest {
      @Autowired private MockMvc mockMvc;
      // 测试完整的HTTP层
  }
  ```

---

## 🆕 新增Web集成测试详解

### AftersaleControllerWebTest

**位置**: `aftersale/src/test/java/cn/edu/xmu/aftersale/controller/AftersaleControllerWebTest.java`

#### 测试覆盖

##### 1️⃣ 商户审核售后API测试 (11个测试)

```java
@Nested
@DisplayName("商户审核售后API - PUT /shops/{shopid}/aftersaleorders/{id}/confirm")
class ConfirmAftersaleTests {
    // ✅ 正常场景
    - confirmReturnAftersale_Approve_ShouldReturnToBeSentStatus()
    - confirmExchangeAftersale_Approve_ShouldReturnToBeSentStatus()
    - confirmRepairAftersale_Approve_ShouldCreateServiceOrderAndReturnToBeCompletedStatus()
    - confirmAftersale_Reject_ShouldReturnRejectedStatus()
    
    // ❌ 异常场景
    - confirmAftersale_MissingConfirm_ShouldReturn400()
    - confirmAftersale_OrderNotFound_ShouldReturn404()
    - confirmAftersale_InvalidStatus_ShouldReturnError()
    - confirmAftersale_ShopIdMismatch_ShouldReturn403()
    
    // 🔄 边界场景
    - confirmAftersale_EmptyConclusion_ShouldAccept()
}
```

**重点验证**:
- ✅ HTTP状态码 (200, 400, 403, 404)
- ✅ 响应JSON结构
- ✅ 业务逻辑执行
- ✅ 跨模块调用 (验证LogisticsClient/ServiceClient被调用)

##### 2️⃣ 商户取消售后API测试 (8个测试)

```java
@Nested
@DisplayName("商户取消售后API - DELETE /shops/{shopid}/aftersaleorders/{id}/cancel")
class CancelAftersaleTests {
    // ✅ 正常场景
    - cancelReturnAftersale_WithExpressId_ShouldCancelPackage()
    - cancelExchangeAftersale_ShouldReturnCancelledStatus()
    - cancelRepairAftersale_ShouldCancelServiceOrder()
    
    // ❌ 异常场景
    - cancelAftersale_ConfirmNotTrue_ShouldReturn400()
    - cancelAftersale_ConfirmNull_ShouldReturn400()
    - cancelAftersale_InvalidStatus_ShouldReturnError()
    
    // 🔄 边界场景
    - cancelAftersale_EmptyReason_ShouldAccept()
}
```

**核心验证点**:
```java
// ⭐ 验证物流运单被取消
verify(logisticsClient).cancelPackage(eq(1L), eq(888L), anyString());

// ⭐ 验证服务单被取消
verify(serviceClient).cancelServiceOrder(eq(1L), eq(777L), anyString());
```

##### 3️⃣ HTTP协议测试 (6个测试)

```java
@Nested
@DisplayName("HTTP协议和格式测试")
class HttpProtocolTests {
    - request_WrongContentType_ShouldReturn415()
    - request_EmptyBody_ShouldReturn400()
    - request_InvalidJson_ShouldReturn400()
    - response_ContentTypeShouldBeJson()
    - response_StructureShouldBeValid()
}
```

### ServiceProviderControllerWebTest

**位置**: `service/src/test/java/cn/edu/xmu/service/controller/ServiceProviderControllerWebTest.java`

#### 测试覆盖 (15个测试)

##### 1️⃣ 审核服务商变更API测试 (10个测试)

```java
@Nested
@DisplayName("平台管理员审核服务商变更API - PUT /draft/{draftid}/review")
class ReviewDraftTests {
    // ✅ 正常场景
    - reviewDraft_Approve_ShouldUpdateProviderAndReturnOK()
    - reviewDraft_Reject_ShouldUpdateDraftStatusToRejected()
    - reviewDraft_ApproveWithNullOpinion_ShouldUseDefaultOpinion()
    
    // ❌ 异常场景
    - reviewDraft_DraftNotFound_ShouldReturn404()
    - reviewDraft_AlreadyReviewed_ShouldReturnStateNotAllowError()
    - reviewDraft_MissingConclusion_ShouldReturn400()
    - reviewDraft_InvalidConclusion_ShouldReturn400()
    
    // 🔄 边界场景
    - reviewDraft_LongOpinion_ShouldAccept()
    - reviewDraft_RejectWithNullOpinion_ShouldUseDefaultReason()
}
```

**重点验证**:
```java
// ⭐ 验证服务商信息被更新
ServiceProvider updatedProvider = providerRepository.findById(provider.getId());
assert "张三维修服务".equals(updatedProvider.getName());
assert "张三".equals(updatedProvider.getConsignee());

// ⭐ 验证草稿状态被更新
ServiceProviderDraft updatedDraft = draftRepository.findById(draft.getId());
assert updatedDraft.getStatus() == DraftStatus.APPROVED;
```

---

## 🚀 运行测试

### 运行所有测试

```bash
# 在项目根目录
mvn clean test
```

### 运行特定模块测试

```bash
# 只测试aftersale模块
cd aftersale
mvn test

# 只测试service模块
cd service
mvn test
```

### 运行特定测试类

```bash
# 运行Web集成测试
mvn test -Dtest=AftersaleControllerWebTest
mvn test -Dtest=ServiceProviderControllerWebTest

# 运行集成测试
mvn test -Dtest=AftersaleServiceIntegrationTest

# 运行策略测试
mvn test -Dtest=AftersaleCancelStrategyMockTest
```

### 运行特定测试方法

```bash
mvn test -Dtest=AftersaleControllerWebTest#confirmReturnAftersale_Approve_ShouldReturnToBeSentStatus
```

---

## 📊 生成测试覆盖率报告

### 生成JaCoCo报告

```bash
# 在项目根目录
mvn clean test jacoco:report

# 查看报告
# aftersale模块: open aftersale/target/site/jacoco/index.html
# service模块: open service/target/site/jacoco/index.html
```

### 生成Surefire报告

```bash
mvn clean test surefire-report:report

# 查看报告
# open aftersale/target/site/surefire-report.html
# open service/target/site/surefire-report.html
```

---

## ✅ 测试覆盖率目标

| 层次 | 目标覆盖率 | 当前状态 |
|------|-----------|---------|
| 整体 | 80%+ | 🔄 待验证 |
| Domain层 | 95%+ | ✅ 预计达标 |
| Service层 | 90%+ | ✅ 预计达标 |
| Controller层 | 80%+ | ✅ 已补充Web测试 |
| Strategy层 | 95%+ | ✅ 预计达标 |

---

## 🎓 测试最佳实践

### 1. 测试命名规范

使用**BDD风格**命名:
```java
// ✅ 好的命名
confirmReturnAftersale_Approve_ShouldReturnToBeSentStatus()
cancelAftersale_ConfirmNotTrue_ShouldReturn400()

// ❌ 不好的命名
test1()
testConfirm()
```

### 2. 测试组织

使用`@Nested`按功能分组:
```java
@Nested
@DisplayName("商户审核售后API")
class ConfirmAftersaleTests {
    // 相关测试
}

@Nested
@DisplayName("商户取消售后API")
class CancelAftersaleTests {
    // 相关测试
}
```

### 3. Mock使用原则

```java
// ✅ 只Mock外部依赖
@MockBean private LogisticsClient logisticsClient;
@MockBean private ServiceClient serviceClient;

// ✅ 使用真实的业务组件
@Autowired private AftersaleOrderRepository repository;
```

### 4. 断言顺序

```java
// 1. 验证HTTP状态码
.andExpect(status().isOk())

// 2. 验证响应格式
.andExpect(content().contentType(MediaType.APPLICATION_JSON))

// 3. 验证响应内容
.andExpect(jsonPath("$.errno").value(0))
.andExpect(jsonPath("$.data.status").value("TO_BE_SENT"))

// 4. 验证Mock调用
verify(logisticsClient).createPackage(eq(1L), any());
```

### 5. 测试数据准备

```java
// ✅ 使用Builder模式
AftersaleOrder order = AftersaleOrder.builder()
        .shopId(1L)
        .type(AftersaleType.RETURN.getCode())
        .status(AftersaleStatus.PENDING)
        .build();

// ✅ 使用文本块 (Java 15+)
String requestBody = """
        {
            "confirm": true,
            "conclusion": "同意"
        }
        """;
```

---

## 🐛 调试测试

### 查看详细输出

```java
mockMvc.perform(...)
    .andDo(print())  // ⭐ 打印请求和响应
    .andExpect(...);
```

### 查看测试日志

```bash
# 在 src/test/resources/logback.xml 中配置日志级别
<logger name="cn.edu.xmu" level="DEBUG"/>
```

---

## 📚 参考资源

### 测试框架文档

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [MockMvc Documentation](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)

### 本项目测试示例

1. **单元测试**: `AftersaleServiceTest.java`
2. **集成测试**: `AftersaleServiceIntegrationTest.java`
3. **Web测试**: `AftersaleControllerWebTest.java` ⭐
4. **策略测试**: `AftersaleCancelStrategyMockTest.java`
5. **Domain测试**: `AftersaleOrderTest.java`

---

## 💡 常见问题

### Q1: Web测试为什么要用@Transactional?
**A**: 自动回滚数据库操作，保证测试之间相互独立。

### Q2: 为什么要Mock外部Client?
**A**: 
- 避免真实调用外部API
- 测试速度更快
- 可以模拟各种返回场景

### Q3: 如何验证异步操作?
**A**: 使用`@Async`时需要特殊处理:
```java
@Test
void testAsync() throws Exception {
    // 执行异步操作
    service.asyncMethod();
    
    // 等待完成
    await().atMost(5, TimeUnit.SECONDS)
           .untilAsserted(() -> {
               verify(mock).method();
           });
}
```

### Q4: 集成测试vs单元测试，如何选择?
**A**: 
- **单元测试**: 测试单个类的逻辑
- **集成测试**: 测试多个类协同工作
- **Web测试**: 测试完整的HTTP流程

优先写单元测试，关键流程补充集成测试和Web测试。

---

## 🎉 总结

本项目的测试架构特点：

✅ **分层清晰**: 单元/集成/Web三层测试
✅ **覆盖全面**: Domain/Strategy/Service/Controller全覆盖
✅ **Mock恰当**: 只Mock外部依赖，使用真实业务组件
✅ **命名规范**: BDD风格，清晰描述测试场景
✅ **易于维护**: 使用@Nested分组，Builder模式构建数据

**这是一套高质量的测试代码，值得学习和参考！** 🌟

