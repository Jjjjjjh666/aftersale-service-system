package cn.edu.xmu.aftersale.model.strategy.impl;

import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleType;
import cn.edu.xmu.aftersale.model.strategy.AftersaleCancelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 换货取消策略
 */
@Slf4j
@Component
public class ExchangeCancelStrategy implements AftersaleCancelStrategy {

    @Override
    public void cancel(AftersaleOrder order, String reason) {
        log.info("执行换货取消策略: orderId={}, reason={}", order.getId(), reason);
        
        order.cancel();
        log.info("换货售后单已取消: orderId={}", order.getId());
        // 后续业务：取消换货物流单、释放新商品库存等
    }

    @Override
    public boolean support(Integer type) {
        return AftersaleType.EXCHANGE.getCode().equals(type);
    }
}

