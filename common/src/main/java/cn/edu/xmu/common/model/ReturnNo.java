package cn.edu.xmu.common.model;

import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
public enum ReturnNo {
    // 成功
    OK(0, "成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    RESOURCE_NOT_FOUND(404, "资源不存在"),
    
    // 业务错误 5xx
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),
    STATENOTALLOW(507, "当前状态不允许此操作"),
    
    // 售后业务错误 6xx
    AFTERSALE_NOT_FOUND(601, "售后单不存在"),
    AFTERSALE_STATE_INVALID(602, "售后单状态不允许此操作"),
    
    // 服务业务错误 7xx
    SERVICE_DRAFT_NOT_FOUND(701, "服务商变更草稿不存在"),
    SERVICE_DRAFT_STATE_INVALID(702, "草稿状态不允许此操作");

    private final Integer code;
    private final String message;

    ReturnNo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
