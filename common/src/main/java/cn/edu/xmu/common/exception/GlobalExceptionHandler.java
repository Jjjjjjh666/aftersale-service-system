package cn.edu.xmu.common.exception;

import cn.edu.xmu.common.model.ReturnNo;
import cn.edu.xmu.common.model.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ReturnObject handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ReturnObject.error(e.getReturnNo(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ReturnObject handleValidationException(Exception e) {
        log.warn("参数校验异常: {}", e.getMessage());
        return ReturnObject.error(ReturnNo.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ReturnObject handleException(Exception e) {
        log.error("系统异常", e);
        return ReturnObject.error(ReturnNo.INTERNAL_SERVER_ERROR);
    }
}

