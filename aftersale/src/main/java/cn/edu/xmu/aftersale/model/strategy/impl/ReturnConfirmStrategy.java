package cn.edu.xmu.aftersale.model.strategy.impl;

import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleType;
import cn.edu.xmu.aftersale.model.strategy.AftersaleConfirmStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 退货审核策略
 * 审核通过后需要等待客户退货
 */
@Slf4j
@Component
public class ReturnConfirmStrategy implements AftersaleConfirmStrategy {

    @Override
    public void confirm(AftersaleOrder order, Boolean confirm, String conclusion) {
        log.info("执行退货审核策略: orderId={}, confirm={}", order.getId(), confirm);
        
        if (Boolean.TRUE.equals(confirm)) {
            // 同意退货
            order.approve(conclusion != null ? conclusion : "同意退货");
            log.info("退货审核通过: orderId={}, 等待客户退货", order.getId());
            // 后续业务：通知客户退货地址、生成退货物流单等
        } else {
            // 拒绝退货
            order.approve(conclusion != null ? conclusion : "拒绝退货");
            log.info("退货审核拒绝: orderId={}, reason={}", order.getId(), conclusion);
        }
    }

    @Override
    public boolean support(Integer type) {
        return AftersaleType.RETURN.getCode().equals(type);  // type=1
    }
}

