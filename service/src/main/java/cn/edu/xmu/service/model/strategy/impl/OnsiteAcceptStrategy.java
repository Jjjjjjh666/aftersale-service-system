package cn.edu.xmu.service.model.strategy.impl;

import cn.edu.xmu.service.model.ServiceOrder;
import cn.edu.xmu.service.model.ServiceOrderType;
import cn.edu.xmu.service.model.strategy.ServiceOrderAcceptStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 上门维修接受策略
 * 服务商接受后需要安排上门时间和技师
 */
@Slf4j
@Component
public class OnsiteAcceptStrategy implements ServiceOrderAcceptStrategy {

    @Override
    public void accept(ServiceOrder order, Long serviceProviderId) {
        log.info("执行上门维修接受策略: orderId={}, serviceProviderId={}", 
                order.getId(), serviceProviderId);
        
        // 接受服务单
        order.accept(serviceProviderId);
        
        // 上门维修特定逻辑
        log.info("上门维修服务单已接受: orderId={}", order.getId());
        log.info("后续需要: 1.分配技师 2.联系客户预约上门时间 3.准备维修工具");
        
        // 实际业务中可能需要：
        // 1. 通知客户服务商已接受
        // 2. 分配技师
        // 3. 安排上门时间
        // 4. 准备维修所需零件和工具
    }

    @Override
    public boolean support(Integer type) {
        return ServiceOrderType.ONSITE_REPAIR.getCode().equals(type);
    }
}
