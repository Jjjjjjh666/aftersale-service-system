package cn.edu.xmu.service.model.strategy.impl;

import cn.edu.xmu.service.model.ServiceOrder;
import cn.edu.xmu.service.model.ServiceOrderType;
import cn.edu.xmu.service.model.strategy.ServiceOrderCancelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 寄修取消策略
 * 取消时需要处理物流和退货
 */
@Slf4j
@Component
public class MailInCancelStrategy implements ServiceOrderCancelStrategy {

    @Override
    public void cancel(ServiceOrder order) {
        log.info("执行寄修取消策略: orderId={}", order.getId());
        
        // 取消服务单
        order.cancel();
        
        // 寄修取消特定逻辑
        log.info("寄修服务单已取消: orderId={}", order.getId());
        
        // 根据物流状态判断后续处理
        if (order.getTrackingNumber() != null) {
            log.info("商品已寄出，物流单号: {}", order.getTrackingNumber());
            log.info("后续需要: 1.拦截物流 2.安排退回 3.通知客户");
        } else {
            log.info("商品未寄出，直接通知客户取消即可");
        }
        
        // 实际业务中可能需要：
        // 1. 检查物流状态
        // 2. 如果商品在途中，联系物流拦截
        // 3. 如果商品已到达，安排退回
        // 4. 通知客户取消信息
        // 5. 处理可能产生的物流费用
    }

    @Override
    public boolean support(Integer type) {
        return ServiceOrderType.MAIL_IN.getCode().equals(type);
    }
}

