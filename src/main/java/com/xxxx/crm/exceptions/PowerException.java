package com.xxxx.crm.exceptions;

/**
 * 自定义参数异常
 */
public class PowerException extends RuntimeException {
    private Integer code=300;
    private String msg="权限异常!";


    public PowerException() {
        super("权限异常!");
    }

    public PowerException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public PowerException(Integer code) {
        super("权限异常!");
        this.code = code;
    }

    public PowerException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
