package cn.edu.xmu.service.dao.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 服务单持久化对象 - 对应数据库表 service_order
 * 注意：数据库字段是 created_at/updated_at，status是VARCHAR类型
 */
@Data
public class ServiceOrderPo {
    private Long id;
    private Long shopId;
    private Long aftersalesId;
    private Long serviceProviderId; // 服务商ID
    private Integer type; // 服务类型 0-上门 1-寄修 2-其他
    private String status; // 状态：CREATED-已创建 ACCEPTED-已接受 CANCELLED-已取消（数据库是VARCHAR）
    private String consignee; // 收件人
    private String address; // 服务地址
    private String trackingNumber; // 物流单号
    private LocalDateTime createdAt; // 对应数据库 created_at
    private LocalDateTime updatedAt; // 对应数据库 updated_at
}

