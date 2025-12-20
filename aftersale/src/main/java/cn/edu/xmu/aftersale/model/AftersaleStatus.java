package cn.edu.xmu.aftersale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 售后单状态枚举
 */
@Getter
@AllArgsConstructor
public enum AftersaleStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已审核"),
    CANCELLED("CANCELLED", "已取消"),
    COMPLETED("COMPLETED", "已完成");

    private final String code;
    private final String description;

    public static AftersaleStatus of(String code) {
        for (AftersaleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的售后状态: " + code);
    }
}

