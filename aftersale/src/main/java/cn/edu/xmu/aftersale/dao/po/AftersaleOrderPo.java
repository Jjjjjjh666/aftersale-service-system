package cn.edu.xmu.aftersale.dao.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 售后单持久化对象 - 对应数据库表 aftersales
 */
@Data
public class AftersaleOrderPo {
    private Long id;
    private Long shopId;
    private Long orderId;
    private Long orderItemId;
    private Long customerId;
    private Long productId;
    private Integer type; // 0-换货 1-退货 2-维修
    private String reason;
    private Integer status; // 0-待审核 1-已通过 2-已拒绝
    private String conclusion;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}

