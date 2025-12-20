package cn.edu.xmu.service.model;

import cn.edu.xmu.common.exception.BusinessException;
import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.service.dao.po.ServiceProviderDraftPo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务商变更草稿领域对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderDraft {
    
    private Long id;
    private Long serviceProviderId;
    private String providerName;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private DraftStatus status;
    private String opinion; // 审核意见
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 从PO构建领域对象
     */
    public static ServiceProviderDraft fromPo(ServiceProviderDraftPo po) {
        if (po == null) {
            return null;
        }
        return ServiceProviderDraft.builder()
                .id(po.getId())
                .serviceProviderId(po.getServiceProviderId())
                .providerName(po.getProviderName())
                .contactPerson(po.getContactPerson())
                .contactPhone(po.getContactPhone())
                .address(po.getAddress())
                .status(DraftStatus.of(po.getStatus()))
                .opinion(po.getOpinion())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    /**
     * 转换为PO
     */
    public ServiceProviderDraftPo toPo() {
        ServiceProviderDraftPo po = new ServiceProviderDraftPo();
        po.setId(this.id);
        po.setServiceProviderId(this.serviceProviderId);
        po.setProviderName(this.providerName);
        po.setContactPerson(this.contactPerson);
        po.setContactPhone(this.contactPhone);
        po.setAddress(this.address);
        po.setStatus(this.status.getCode());
        po.setOpinion(this.opinion);
        po.setCreatedAt(this.createdAt);
        po.setUpdatedAt(this.updatedAt);
        return po;
    }

    /**
     * 检查是否为待审核状态
     */
    public void checkPendingStatus() {
        if (!DraftStatus.PENDING.equals(this.status)) {
            throw new BusinessException(ReturnNo.SERVICE_DRAFT_STATE_INVALID, 
                    "只有待审核状态的草稿才能进行审核");
        }
    }

    /**
     * 审核通过
     */
    public void approve(String opinion) {
        this.status = DraftStatus.APPROVED;
        this.opinion = opinion;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 审核拒绝
     */
    public void reject(String opinion) {
        this.status = DraftStatus.REJECTED;
        this.opinion = opinion;
        this.updatedAt = LocalDateTime.now();
    }
}

