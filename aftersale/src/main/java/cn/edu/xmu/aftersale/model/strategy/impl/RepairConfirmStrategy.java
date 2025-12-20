package cn.edu.xmu.aftersale.model.strategy.impl;

import cn.edu.xmu.aftersale.client.ServiceClient;
import cn.edu.xmu.aftersale.client.dto.CreateServiceOrderRequest;
import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleType;
import cn.edu.xmu.aftersale.model.strategy.AftersaleConfirmStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 维修审核策略
 * 审核通过后需要调用服务模块创建服务单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepairConfirmStrategy implements AftersaleConfirmStrategy {

    private final ServiceClient serviceClient;

    @Override
    public void confirm(AftersaleOrder order, Boolean confirm, String conclusion) {
        log.info("执行维修审核策略: orderId={}, confirm={}", order.getId(), confirm);
        
        if (Boolean.TRUE.equals(confirm)) {
            // 同意维修
            order.approve(conclusion != null ? conclusion : "同意维修");
            log.info("维修审核通过: orderId={}, 准备创建服务单", order.getId());
            
            // 调用服务模块创建服务单（跨模块调用）
            try {
                CreateServiceOrderRequest request = new CreateServiceOrderRequest();
                request.setType(2); // 维修类型
                serviceClient.createServiceOrder(order.getShopId(), order.getId(), request);
                log.info("维修服务单创建成功: aftersaleId={}", order.getId());
            } catch (Exception e) {
                log.error("创建维修服务单失败: aftersaleId={}", order.getId(), e);
                throw new RuntimeException("创建服务单失败: " + e.getMessage(), e);
            }
        } else {
            // 拒绝维修
            order.approve(conclusion != null ? conclusion : "拒绝维修");
            log.info("维修审核拒绝: orderId={}, reason={}", order.getId(), conclusion);
        }
    }

    @Override
    public boolean support(Integer type) {
        return AftersaleType.REPAIR.getCode().equals(type);
    }
}

