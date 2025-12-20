package cn.edu.xmu.aftersale.controller;

import cn.edu.xmu.aftersale.controller.dto.CancelAftersaleRequest;
import cn.edu.xmu.aftersale.controller.dto.ConfirmAftersaleRequest;
import cn.edu.xmu.aftersale.service.AftersaleService;
import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.common.model.ReturnObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 售后控制器
 */
@Slf4j
@RestController
@RequestMapping("/shops/{shopid}")
@RequiredArgsConstructor
@Validated
public class AftersaleController {

    private final AftersaleService aftersaleService;

    /**
     * 商户审核售后
     * PUT /shops/{shopid}/aftersaleorders/{id}/confirm
     */
    @PutMapping("/aftersaleorders/{id}/confirm")
    public ReturnObject confirmAftersale(
            @PathVariable("shopid") Long shopId,
            @PathVariable("id") Long id,
            @Valid @RequestBody ConfirmAftersaleRequest request) {
        
        log.info("商户审核售后API: shopId={}, id={}, confirm={}", shopId, id, request.getConfirm());
        
        String status = aftersaleService.confirmAftersale(
                shopId, id, request.getConfirm(), request.getConclusion());
        
        Map<String, Object> data = new HashMap<>();
        data.put("errno", ReturnNo.OK.getCode());
        data.put("errmsg", ReturnNo.OK.getMessage());
        data.put("status", status);
        
        return new ReturnObject(data);
    }

    /**
     * 商户取消售后单
     * DELETE /shops/{shopid}/aftersaleorders/{id}/cancel
     */
    @DeleteMapping("/aftersaleorders/{id}/cancel")
    public ReturnObject cancelAftersale(
            @PathVariable("shopid") Long shopId,
            @PathVariable("id") Long id,
            @Valid @RequestBody CancelAftersaleRequest request) {
        
        log.info("商户取消售后API: shopId={}, id={}, reason={}", shopId, id, request.getReason());
        
        // 需要confirm=true才能取消
        if (!Boolean.TRUE.equals(request.getConfirm())) {
            return ReturnObject.error(ReturnNo.BAD_REQUEST, "confirm必须为true");
        }
        
        String status = aftersaleService.cancelAftersale(shopId, id, request.getReason());
        
        Map<String, Object> data = new HashMap<>();
        data.put("errno", ReturnNo.OK.getCode());
        data.put("errmsg", ReturnNo.OK.getMessage());
        data.put("status", status);
        
        return new ReturnObject(data);
    }
}

