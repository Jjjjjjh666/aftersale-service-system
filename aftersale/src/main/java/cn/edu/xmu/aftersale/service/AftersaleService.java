package cn.edu.xmu.aftersale.service;

import cn.edu.xmu.aftersale.dao.AftersaleOrderRepository;
import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleStatus;
import cn.edu.xmu.aftersale.model.strategy.AftersaleCancelStrategy;
import cn.edu.xmu.aftersale.model.strategy.AftersaleConfirmStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 售后服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AftersaleService {

    private final AftersaleOrderRepository repository;
    private final List<AftersaleConfirmStrategy> confirmStrategies;
    private final List<AftersaleCancelStrategy> cancelStrategies;

    /**
     * 商户审核售后单（多态实现）
     */
    @Transactional
    public String confirmAftersale(Long shopId, Long id, Boolean confirm, String conclusion) {
        log.info("开始审核售后单: shopId={}, id={}, confirm={}", shopId, id, confirm);
        
        // 1. 查询售后单
        AftersaleOrder order = repository.findById(shopId, id);
        
        // 2. 检查状态
        order.checkPendingStatus();
        
        // 3. 根据类型选择策略（多态）
        AftersaleConfirmStrategy strategy = confirmStrategies.stream()
                .filter(s -> s.support(order.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的售后类型: " + order.getType()));
        
        // 4. 执行策略
        strategy.confirm(order, confirm, conclusion);

        // 5. 如果策略未更新状态，兜底处理
        if (AftersaleStatus.PENDING.equals(order.getStatus())) {
            if (Boolean.TRUE.equals(confirm)) {
                order.approve(conclusion);
            } else {
                order.reject(conclusion);
            }
        }
        
        // 6. 保存更新
        repository.save(order);
        
        log.info("售后单审核完成: id={}, status={}", id, order.getStatus());
        return order.getStatus().getCode();
    }

    /**
     * 商户取消售后单（多态实现）
     */
    @Transactional
    public String cancelAftersale(Long shopId, Long id, String reason) {
        log.info("开始取消售后单: shopId={}, id={}, reason={}", shopId, id, reason);
        
        // 1. 查询售后单
        AftersaleOrder order = repository.findById(shopId, id);
        
        // 2. 检查状态
        order.checkApprovedStatus();
        
        // 3. 根据类型选择策略（多态）
        AftersaleCancelStrategy strategy = cancelStrategies.stream()
                .filter(s -> s.support(order.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的售后类型: " + order.getType()));
        
        // 4. 执行策略
        strategy.cancel(order, reason);

        // 5. 如果策略未更新状态，兜底取消
        if (!AftersaleStatus.CANCELLED.equals(order.getStatus())) {
            order.cancel();
        }
        
        // 6. 保存更新
        repository.save(order);
        
        log.info("售后单取消完成: id={}, status={}", id, order.getStatus());
        return order.getStatus().getCode();
    }
}
