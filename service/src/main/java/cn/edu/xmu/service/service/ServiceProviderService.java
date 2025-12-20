package cn.edu.xmu.service.service;

import cn.edu.xmu.service.dao.ServiceProviderDraftRepository;
import cn.edu.xmu.service.model.ServiceProviderDraft;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务商服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderDraftRepository repository;

    /**
     * 平台管理员审核服务商变更
     * @param draftId 草稿ID
     * @param conclusion 审核结果 0-拒绝 1-通过
     * @param opinion 审核意见
     */
    @Transactional
    public void reviewDraft(Long draftId, Integer conclusion, String opinion) {
        log.info("开始审核服务商变更: draftId={}, conclusion={}", draftId, conclusion);
        
        // 1. 查询草稿
        ServiceProviderDraft draft = repository.findById(draftId);
        
        // 2. 检查状态
        draft.checkPendingStatus();
        
        // 3. 执行审核
        if (Integer.valueOf(1).equals(conclusion)) {
            // 审核通过
            draft.approve(opinion != null ? opinion : "审核通过");
            log.info("服务商变更审核通过: draftId={}", draftId);
            
            // 后续业务：将草稿内容应用到正式的服务商信息中
            applyDraftToProvider(draft);
        } else if (Integer.valueOf(0).equals(conclusion)) {
            // 审核拒绝
            draft.reject(opinion != null ? opinion : "审核拒绝");
            log.info("服务商变更审核拒绝: draftId={}, opinion={}", draftId, opinion);
        } else {
            throw new IllegalArgumentException("无效的审核结果: " + conclusion);
        }
        
        // 4. 保存更新
        repository.save(draft);
        
        log.info("服务商变更审核完成: draftId={}, status={}", draftId, draft.getStatus());
    }

    /**
     * 将草稿内容应用到正式的服务商信息
     */
    private void applyDraftToProvider(ServiceProviderDraft draft) {
        log.info("应用草稿到服务商: serviceProviderId={}, draftId={}", 
                draft.getServiceProviderId(), draft.getId());
        // 这里应该更新service_provider表的记录
        // 实际项目中需要实现相应的逻辑
    }
}

