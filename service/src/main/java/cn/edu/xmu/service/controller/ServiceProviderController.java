package cn.edu.xmu.service.controller;

import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.common.model.ReturnObject;
import cn.edu.xmu.service.controller.dto.ReviewDraftRequest;
import cn.edu.xmu.service.service.ServiceProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 服务商管理控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    /**
     * 平台管理员审核服务商变更
     * PUT /draft/{draftid}/review
     */
    @PutMapping("/draft/{draftid}/review")
    public ReturnObject reviewDraft(
            @PathVariable("draftid") Long draftId,
            @Valid @RequestBody ReviewDraftRequest request) {
        
        log.info("平台管理员审核服务商变更API: draftId={}, conclusion={}", 
                draftId, request.getConclusion());
        
        serviceProviderService.reviewDraft(draftId, request.getConclusion(), request.getOpinion());
        
        return new ReturnObject(ReturnNo.OK);
    }
}

