package cn.edu.xmu.service.controller;

import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.common.model.ReturnObject;
import cn.edu.xmu.service.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 服务商服务单控制器
 */
@Slf4j
@RestController
@RequestMapping("/serviceprovider/{did}/serviceorders")
@RequiredArgsConstructor
@Validated
public class ServiceProviderOrderController {

    private final ServiceOrderService serviceOrderService;

    /**
     * 服务商接受服务单
     * PUT /serviceprovider/{did}/serviceorders/{id}/accept
     */
    @PutMapping("/{id}/accept")
    public ReturnObject acceptServiceOrder(
            @PathVariable("did") Long serviceProviderId,
            @PathVariable("id") Long orderId) {
        
        log.info("服务商接受服务单API: serviceProviderId={}, orderId={}", serviceProviderId, orderId);
        
        serviceOrderService.acceptServiceOrder(serviceProviderId, orderId);
        
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 服务商取消服务单
     * DELETE /serviceprovider/{did}/serviceorders/{id}/cancel
     */
    @DeleteMapping("/{id}/cancel")
    public ReturnObject cancelServiceOrder(
            @PathVariable("did") Long serviceProviderId,
            @PathVariable("id") Long orderId) {
        
        log.info("服务商取消服务单API: serviceProviderId={}, orderId={}", serviceProviderId, orderId);
        
        serviceOrderService.cancelServiceOrder(serviceProviderId, orderId);
        
        return new ReturnObject(ReturnNo.OK);
    }
}

