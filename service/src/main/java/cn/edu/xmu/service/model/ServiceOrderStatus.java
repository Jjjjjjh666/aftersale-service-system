package cn.edu.xmu.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务单状态枚举
 */
@Getter
@AllArgsConstructor
public enum ServiceOrderStatus {
    CREATED("CREATED", "已创建"),
    ACCEPTED("ACCEPTED", "已接受"),
    IN_PROGRESS("IN_PROGRESS", "进行中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    public static ServiceOrderStatus of(String code) {
        for (ServiceOrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的服务单状态: " + code);
    }
}

