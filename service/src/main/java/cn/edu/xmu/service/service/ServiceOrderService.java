package cn.edu.xmu.service.service;

import cn.edu.xmu.service.dao.ServiceOrderRepository;
import cn.edu.xmu.service.model.ServiceOrder;
import cn.edu.xmu.service.model.ServiceOrderStatus;
import cn.edu.xmu.service.model.ServiceOrderType;
import cn.edu.xmu.service.model.strategy.ServiceOrderAcceptStrategy;
import cn.edu.xmu.service.model.strategy.ServiceOrderCancelStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository repository;
    private final List<ServiceOrderAcceptStrategy> acceptStrategies;
    private final List<ServiceOrderCancelStrategy> cancelStrategies;

    /**
     * 创建服务单（被aftersale模块调用）
     */
    @Transactional
    public Long createServiceOrder(Long shopId, Long aftersalesId, Integer type, String consignee) {
        log.info("创建服务单: shopId={}, aftersalesId={}, type={}", shopId, aftersalesId, type);
        
        ServiceOrder order = ServiceOrder.builder()
                .shopId(shopId)
                .aftersalesId(aftersalesId)
                .type(ServiceOrderType.valueOf(type != null ? type : 0))
                .status(ServiceOrderStatus.CREATED)
                .consignee(consignee)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        repository.create(order);
        
        log.info("服务单创建成功: id={}", order.getId());
        return order.getId();
    }

    /**
     * 服务商接受服务单（多态实现）
     */
    @Transactional
    public void acceptServiceOrder(Long serviceProviderId, Long orderId) {
        log.info("开始接受服务单: serviceProviderId={}, orderId={}", serviceProviderId, orderId);
        
        // 1. 查询服务单
        ServiceOrder order = repository.findById(orderId);
        
        // 2. 检查状态
        order.checkCreatedStatus();
        
        // 3. 根据类型选择策略（多态）
        ServiceOrderAcceptStrategy strategy = acceptStrategies.stream()
                .filter(s -> s.support(order.getType().getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的服务类型: " + order.getType()));
        
        // 4. 执行策略
        strategy.accept(order, serviceProviderId);
        
        // 5. 保存更新
        repository.save(order);
        
        log.info("服务单接受完成: orderId={}, status={}", orderId, order.getStatus());
    }

    /**
     * 服务商取消服务单（多态实现）
     */
    @Transactional
    public void cancelServiceOrder(Long serviceProviderId, Long orderId) {
        log.info("开始取消服务单: serviceProviderId={}, orderId={}", serviceProviderId, orderId);
        
        // 1. 查询服务单
        ServiceOrder order = repository.findById(orderId);
        
        // 2. 检查状态
        order.checkAcceptedStatus();
        
        // 3. 根据类型选择策略（多态）
        ServiceOrderCancelStrategy strategy = cancelStrategies.stream()
                .filter(s -> s.support(order.getType().getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的服务类型: " + order.getType()));
        
        // 4. 执行策略
        strategy.cancel(order);
        
        // 5. 保存更新
        repository.save(order);
        
        log.info("服务单取消完成: orderId={}, status={}", orderId, order.getStatus());
    }

    /**
     * 内部接口 - 取消服务单（被aftersale模块调用）
     */
    @Transactional
    public void cancelServiceOrderByAftersale(Long aftersalesId, String reason) {
        log.info("售后模块取消服务单: aftersalesId={}, reason={}", aftersalesId, reason);
        // 实际项目中需要实现取消逻辑
        // 这里简化处理
    }
}

