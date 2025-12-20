package cn.edu.xmu.service.model.strategy.impl;

import cn.edu.xmu.service.model.ServiceOrder;
import cn.edu.xmu.service.model.ServiceOrderType;
import cn.edu.xmu.service.model.strategy.ServiceOrderAcceptStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 寄修接受策略
 * 服务商接受后需要提供寄修地址和物流信息
 */
@Slf4j
@Component
public class MailInAcceptStrategy implements ServiceOrderAcceptStrategy {

    @Override
    public void accept(ServiceOrder order, Long serviceProviderId) {
        log.info("执行寄修接受策略: orderId={}, serviceProviderId={}", 
                order.getId(), serviceProviderId);
        
        // 接受服务单
        order.accept(serviceProviderId);
        
        // 寄修特定逻辑
        log.info("寄修服务单已接受: orderId={}", order.getId());
        log.info("后续需要: 1.提供寄修地址 2.等待客户寄送商品 3.生成收货物流单");
        
        // 实际业务中可能需要：
        // 1. 生成寄修地址和联系方式
        // 2. 通知客户寄修信息
        // 3. 等待客户寄送商品
        // 4. 跟踪物流状态
        // 5. 商品到达后开始维修
        
        // 设置寄修地址
        String mailInAddress = "维修中心地址: 北京市朝阳区XX路XX号";
        order.setAddress(mailInAddress);
        log.info("寄修地址: {}", mailInAddress);
    }

    @Override
    public boolean support(Integer type) {
        return ServiceOrderType.MAIL_IN_REPAIR.getCode().equals(type);
    }
}
