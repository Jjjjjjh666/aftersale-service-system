package cn.edu.xmu.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回对象
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnObject {
    private Integer errno;
    private String errmsg;
    private Object data;

    public ReturnObject(ReturnNo returnNo) {
        this.errno = returnNo.getCode();
        this.errmsg = returnNo.getMessage();
    }

    public ReturnObject(ReturnNo returnNo, Object data) {
        this.errno = returnNo.getCode();
        this.errmsg = returnNo.getMessage();
        this.data = data;
    }

    public ReturnObject(Object data) {
        this.errno = ReturnNo.OK.getCode();
        this.errmsg = ReturnNo.OK.getMessage();
        this.data = data;
    }

    public static ReturnObject success() {
        return new ReturnObject(ReturnNo.OK);
    }

    public static ReturnObject success(Object data) {
        return new ReturnObject(data);
    }

    public static ReturnObject error(ReturnNo returnNo) {
        return new ReturnObject(returnNo);
    }

    public static ReturnObject error(ReturnNo returnNo, String customMsg) {
        ReturnObject obj = new ReturnObject(returnNo);
        obj.setErrmsg(customMsg);
        return obj;
    }
}

