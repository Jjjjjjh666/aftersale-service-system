package cn.edu.xmu.service.model;

import cn.edu.xmu.common.exception.BusinessException;
import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.service.dao.po.ServiceOrderPo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务单领域对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {
    
    private Long id;
    private Long shopId;
    private Long aftersalesId;
    private Long serviceProviderId; // 服务商ID
    private ServiceOrderType type;
    private ServiceOrderStatus status;
    private String consignee;
    private String address; // 服务地址
    private String trackingNumber; // 物流单号（寄修场景）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 从PO构建领域对象
     */
    public static ServiceOrder fromPo(ServiceOrderPo po) {
        if (po == null) {
            return null;
        }
        return ServiceOrder.builder()
                .id(po.getId())
                .shopId(po.getShopId())
                .aftersalesId(po.getAftersalesId())
                .serviceProviderId(po.getServiceProviderId())
                .type(ServiceOrderType.valueOf(po.getType()))
                .status(convertStatusFromString(po.getStatus()))
                .consignee(po.getConsignee())
                .address(po.getAddress())
                .trackingNumber(po.getTrackingNumber())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    /**
     * 转换为PO
     */
    public ServiceOrderPo toPo() {
        ServiceOrderPo po = new ServiceOrderPo();
        po.setId(this.id);
        po.setShopId(this.shopId);
        po.setAftersalesId(this.aftersalesId);
        po.setServiceProviderId(this.serviceProviderId);
        po.setType(this.type.getCode());
        po.setStatus(convertStatusToString(this.status));
        po.setConsignee(this.consignee);
        po.setAddress(this.address);
        po.setTrackingNumber(this.trackingNumber);
        po.setCreatedAt(this.createdAt);
        po.setUpdatedAt(this.updatedAt);
        return po;
    }

    /**
     * 将数据库状态字符串转换为状态枚举
     */
    private static ServiceOrderStatus convertStatusFromString(String status) {
        if (status == null) {
            return ServiceOrderStatus.CREATED;
        }
        try {
            return ServiceOrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ServiceOrderStatus.CREATED;
        }
    }

    /**
     * 将状态枚举转换为数据库状态字符串
     */
    private static String convertStatusToString(ServiceOrderStatus status) {
        if (status == null) {
            return ServiceOrderStatus.CREATED.name();
        }
        return status.name();
    }

    /**
     * 检查是否为已创建状态
     */
    public void checkCreatedStatus() {
        if (!ServiceOrderStatus.CREATED.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, 
                    "只有已创建状态的服务单才能接受");
        }
    }

    /**
     * 检查是否为已接受状态
     */
    public void checkAcceptedStatus() {
        if (!ServiceOrderStatus.ACCEPTED.equals(this.status)) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, 
                    "只有已接受状态的服务单才能取消");
        }
    }

    /**
     * 接受服务单
     */
    public void accept(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
        this.status = ServiceOrderStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 取消服务单
     */
    public void cancel() {
        this.status = ServiceOrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
}

