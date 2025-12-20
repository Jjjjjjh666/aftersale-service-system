package cn.edu.xmu.aftersale.model;

import cn.edu.xmu.aftersale.dao.po.AftersaleOrderPo;
import cn.edu.xmu.common.exception.BusinessException;
import cn.edu.xmu.common.model.ReturnNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 售后单领域对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleOrder {
    
    private Long id;
    private Long shopId;
    private Long orderId;
    private Integer type; // 0-退货 1-换货 2-维修
    private AftersaleStatus status;
    private String reason;
    private String conclusion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 从PO构建领域对象
     */
    public static AftersaleOrder fromPo(AftersaleOrderPo po) {
        if (po == null) {
            return null;
        }
        return AftersaleOrder.builder()
                .id(po.getId())
                .shopId(po.getShopId())
                .orderId(po.getOrderId())
                .type(po.getType())
                .status(convertStatus(po.getStatus()))
                .reason(po.getReason())
                .conclusion(po.getConclusion())
                .createdAt(po.getGmtCreate())
                .updatedAt(po.getGmtModified())
                .build();
    }

    /**
     * 转换为PO
     */
    public AftersaleOrderPo toPo() {
        AftersaleOrderPo po = new AftersaleOrderPo();
        po.setId(this.id);
        po.setShopId(this.shopId);
        po.setOrderId(this.orderId);
        po.setType(this.type);
        po.setStatus(convertStatusToInt(this.status));
        po.setReason(this.reason);
        po.setConclusion(this.conclusion);
        po.setGmtCreate(this.createdAt);
        po.setGmtModified(this.updatedAt);
        return po;
    }

    /**
     * 将数据库状态码转换为状态枚举
     */
    private static AftersaleStatus convertStatus(Integer statusCode) {
        if (statusCode == null) {
            return AftersaleStatus.PENDING;
        }
        switch (statusCode) {
            case 0: return AftersaleStatus.PENDING;   // 待审核
            case 1: return AftersaleStatus.APPROVED;  // 已通过
            case 2: return AftersaleStatus.CANCELLED; // 已拒绝
            default: return AftersaleStatus.PENDING;
        }
    }

    /**
     * 将状态枚举转换为数据库状态码
     */
    private static Integer convertStatusToInt(AftersaleStatus status) {
        if (status == null) {
            return 0;
        }
        switch (status) {
            case PENDING: return 0;
            case APPROVED: return 1;
            case CANCELLED: return 2;
            default: return 0;
        }
    }

    /**
     * 检查是否为待审核状态
     */
    public void checkPendingStatus() {
        if (!AftersaleStatus.PENDING.equals(this.status)) {
            throw new BusinessException(ReturnNo.AFTERSALE_STATE_INVALID, 
                    "只有待审核状态的售后单才能进行审核");
        }
    }

    /**
     * 检查是否为已审核状态
     */
    public void checkApprovedStatus() {
        if (!AftersaleStatus.APPROVED.equals(this.status)) {
            throw new BusinessException(ReturnNo.AFTERSALE_STATE_INVALID, 
                    "只有已审核状态的售后单才能取消");
        }
    }

    /**
     * 更新为已审核状态
     */
    public void approve(String conclusion) {
        this.status = AftersaleStatus.APPROVED;
        this.conclusion = conclusion;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新为已取消状态
     */
    public void cancel() {
        this.status = AftersaleStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 获取售后类型
     */
    public AftersaleType getAftersaleType() {
        return AftersaleType.valueOf(this.type);
    }
}

