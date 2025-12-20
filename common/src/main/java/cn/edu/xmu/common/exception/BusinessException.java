package cn.edu.xmu.common.exception;

import cn.edu.xmu.common.model.ReturnNo;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ReturnNo returnNo;

    public BusinessException(ReturnNo returnNo) {
        super(returnNo.getMessage());
        this.returnNo = returnNo;
    }

    public BusinessException(ReturnNo returnNo, String message) {
        super(message);
        this.returnNo = returnNo;
    }
}
