package cn.edu.xmu.service.dao;

import cn.edu.xmu.common.exception.BusinessException;
import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.service.dao.po.ServiceProviderDraftPo;
import cn.edu.xmu.service.model.ServiceProviderDraft;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 服务商草稿仓储
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ServiceProviderDraftRepository {

    private final ServiceProviderDraftMapper mapper;

    /**
     * 根据ID查询草稿
     */
    public ServiceProviderDraft findById(Long id) {
        ServiceProviderDraftPo po = mapper.findById(id);
        if (po == null) {
            throw new BusinessException(ReturnNo.SERVICE_DRAFT_NOT_FOUND);
        }
        return ServiceProviderDraft.fromPo(po);
    }

    /**
     * 保存草稿
     */
    public void save(ServiceProviderDraft draft) {
        ServiceProviderDraftPo po = draft.toPo();
        int rows = mapper.updateStatus(po);
        if (rows == 0) {
            throw new BusinessException(ReturnNo.SERVICE_DRAFT_NOT_FOUND);
        }
        log.info("服务商草稿更新成功: id={}, status={}", draft.getId(), draft.getStatus());
    }

    /**
     * 创建草稿（用于测试）
     */
    public ServiceProviderDraft create(ServiceProviderDraft draft) {
        ServiceProviderDraftPo po = draft.toPo();
        mapper.insert(po);
        draft.setId(po.getId());
        log.info("服务商草稿创建成功: id={}", po.getId());
        return draft;
    }
}

