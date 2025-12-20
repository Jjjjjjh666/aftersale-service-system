package cn.edu.xmu.aftersale.model.strategy.impl;

import cn.edu.xmu.aftersale.model.AftersaleOrder;
import cn.edu.xmu.aftersale.model.AftersaleType;
import cn.edu.xmu.aftersale.model.strategy.AftersaleConfirmStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 换货审核策略
 * 审核通过后需要等待客户寄回商品，商家再发出新商品
 */
@Slf4j
@Component
public class ExchangeConfirmStrategy implements AftersaleConfirmStrategy {

    @Override
    public void confirm(AftersaleOrder order, Boolean confirm, String conclusion) {
        log.info("执行换货审核策略: orderId={}, confirm={}", order.getId(), confirm);
        
        if (Boolean.TRUE.equals(confirm)) {
            // 同意换货
            order.approve(conclusion != null ? conclusion : "同意换货");
            log.info("换货审核通过: orderId={}, 等待客户寄回旧商品", order.getId());
            // 后续业务：通知客户寄回地址、准备新商品库存等
        } else {
            // 拒绝换货
            order.approve(conclusion != null ? conclusion : "拒绝换货");
            log.info("换货审核拒绝: orderId={}, reason={}", order.getId(), conclusion);
        }
    }

    @Override
    public boolean support(Integer type) {
        return AftersaleType.EXCHANGE.getCode().equals(type);  // type=0
    }
}

