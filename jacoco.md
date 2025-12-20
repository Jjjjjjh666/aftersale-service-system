# JaCoCo 代码覆盖率测试指南

## 概述

本指南介绍如何使用 JaCoCo 对 aftersale-service-system 项目进行代码覆盖率测试，用于评估代码复用性和测试完整性。

## JaCoCo 配置

### 1. Maven 配置

已在根 `pom.xml` 中配置 JaCoCo 插件：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>post-unit-test</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
            <configuration>
                <dataFile>target/jacoco.exec</dataFile>
                <outputDirectory>target/site/jacoco</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 2. 测试覆盖范围

本项目包含以下测试类：

#### Aftersale 模块 (售后模块)
- `AftersaleServiceTest.java` - 测试售后审核和取消的多态实现
  - 退货审核和取消策略
  - 换货审核和取消策略
  - 维修审核和取消策略

#### Service 模块 (服务模块)
- `ServiceOrderServiceTest.java` - 测试服务单接受和取消的多态实现
  - 上门维修接受和取消策略
  - 寄修接受和取消策略
- `ServiceProviderServiceTest.java` - 测试服务商审核功能
  - 审核通过
  - 审核拒绝

## 运行测试

### 方法一：使用测试脚本（推荐）

```bash
cd /Users/jhlee/Desktop/ooad/aftersale-service-system
./run-jacoco-test.sh
```

此脚本将自动：
1. 清理之前的构建
2. 运行所有单元测试
3. 生成 JaCoCo 覆盖率报告
4. 显示报告位置和覆盖率统计

### 方法二：手动运行 Maven 命令

```bash
# 清理并运行测试
cd /Users/jhlee/Desktop/ooad/aftersale-service-system
mvn clean test

# 生成 JaCoCo 报告
mvn jacoco:report
```

## 查看报告

### 报告位置

测试完成后，每个模块会生成独立的 JaCoCo 报告：

- **Aftersale 模块**: `aftersale/target/site/jacoco/index.html`
- **Service 模块**: `service/target/site/jacoco/index.html`
- **Common 模块**: `common/target/site/jacoco/index.html`

### 在浏览器中打开报告

```bash
# macOS
open aftersale/target/site/jacoco/index.html
open service/target/site/jacoco/index.html

# Linux
xdg-open aftersale/target/site/jacoco/index.html
xdg-open service/target/site/jacoco/index.html
```

## 报告解读

### 覆盖率指标

JaCoCo 报告提供以下覆盖率维度：

1. **指令覆盖率 (Instructions)**
   - 字节码指令的覆盖率
   - 最细粒度的覆盖率指标

2. **分支覆盖率 (Branches)**
   - if/switch 等分支语句的覆盖率
   - 评估条件逻辑的测试完整性

3. **圈复杂度 (Cyclomatic Complexity)**
   - 代码复杂度指标
   - 评估代码可维护性

4. **行覆盖率 (Lines)**
   - 源代码行的覆盖率
   - 最直观的覆盖率指标

5. **方法覆盖率 (Methods)**
   - 方法的覆盖率
   - 评估 API 测试完整性

6. **类覆盖率 (Classes)**
   - 类的覆盖率
   - 评估模块测试完整性

### 颜色编码

- 🟢 **绿色**: 已覆盖的代码
- 🔴 **红色**: 未覆盖的代码
- 🟡 **黄色**: 部分覆盖的分支

### 查看详细信息

1. 点击包名 -> 类名，可以深入查看具体代码的覆盖情况
2. 每行代码左侧会显示覆盖次数
3. 分支语句会显示覆盖的分支数

## 代码复用性分析

### 多态模式的测试验证

本项目使用策略模式实现多态，JaCoCo 可以验证：

#### 1. 售后模块的策略复用
- **审核策略**: 退货/换货/维修使用相同的接口 `AftersaleConfirmStrategy`
- **取消策略**: 退货/换货/维修使用相同的接口 `AftersaleCancelStrategy`
- **测试验证**: 确保每种策略都被正确调用

#### 2. 服务模块的策略复用
- **接受策略**: 上门维修/寄修使用相同的接口 `ServiceOrderAcceptStrategy`
- **取消策略**: 上门维修/寄修使用相同的接口 `ServiceOrderCancelStrategy`
- **测试验证**: 确保策略选择逻辑正确

### 通过 JaCoCo 评估代码复用性

1. **查看接口覆盖率**
   - 策略接口的覆盖率应该很高
   - 表示接口被多个实现类复用

2. **查看抽象类/基类覆盖率**
   - 公共逻辑的覆盖率
   - 表示代码复用程度

3. **查看工具类覆盖率**
   - 通用工具方法的覆盖率
   - 表示工具类的使用频率

## 测试质量标准

### 推荐的覆盖率目标

- **整体覆盖率**: ≥ 70%
- **业务逻辑代码**: ≥ 80%
- **策略模式实现**: ≥ 90%
- **工具类**: ≥ 70%
- **配置类**: 可以较低（30-50%）

### 重点关注区域

1. **多态策略实现**
   - 每个策略实现类都应该有完整的测试
   - 验证策略选择逻辑

2. **状态转换逻辑**
   - 售后单状态转换
   - 服务单状态转换
   - 草稿状态转换

3. **业务规则验证**
   - 状态检查
   - 参数校验
   - 异常处理

## 持续改进

### 提高覆盖率的方法

1. **识别未覆盖代码**
   - 在 JaCoCo 报告中查看红色标记的代码
   - 优先为业务逻辑添加测试

2. **补充边界测试**
   - 添加异常场景测试
   - 添加边界值测试

3. **增加集成测试**
   - 测试模块间交互
   - 测试完整业务流程

### 定期检查

建议在以下场景运行 JaCoCo 测试：

- ✅ 添加新功能后
- ✅ 重构代码后
- ✅ 修复 Bug 后
- ✅ 代码审查前
- ✅ 发布版本前

## 常见问题

### Q1: 为什么有些类覆盖率是 0%？

**A**: 可能原因：
1. 没有为该类编写测试
2. 该类是配置类或启动类（不需要测试）
3. 该类是 DAO/Mapper 接口（可以通过集成测试覆盖）

### Q2: 如何提高分支覆盖率？

**A**: 
1. 为 if/else 的每个分支添加测试
2. 为 switch/case 的每个情况添加测试
3. 测试异常处理分支

### Q3: 100% 覆盖率是必要的吗？

**A**: 
不一定。重点关注：
- 业务逻辑代码需要高覆盖率
- 配置代码可以较低覆盖率
- 追求合理的覆盖率，而不是盲目追求 100%

### Q4: 如何查看聚合报告？

**A**: 
可以使用 JaCoCo 的聚合功能：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>report-aggregate</id>
            <phase>verify</phase>
            <goals>
                <goal>report-aggregate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

然后运行: `mvn verify`

## 参考资源

- [JaCoCo 官方文档](https://www.jacoco.org/jacoco/trunk/doc/)
- [Maven JaCoCo Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- 项目 README: `/Users/jhlee/Desktop/ooad/aftersale-service-system/README.md`

## 总结

JaCoCo 是评估代码质量和复用性的重要工具。通过定期运行测试和查看报告，可以：

1. ✅ 确保代码有充分的测试覆盖
2. ✅ 识别未测试的代码区域
3. ✅ 验证多态设计的正确性
4. ✅ 提高代码质量和可维护性
5. ✅ 评估代码复用程度

记住：**覆盖率是手段，而不是目的。重要的是编写有意义的测试，确保代码的正确性和可维护性。**

