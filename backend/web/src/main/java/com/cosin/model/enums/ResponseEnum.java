package com.cosin.model.enums;

/**
 * @author Chenyz
 * @description:
 */
public enum ResponseEnum {

    OK("200", "Ok"),
    NOT_FOUND("404", "Not Found"),
    FORBIDDEN("403", "Forbidden"),
    UNAUTHORIZED("401", "unauthorized"),
    SERVER_INTERNAL_ERROR("500", "Server Internal Error"),
    BAD_REQUEST("400", "Bad Request");

    private String code;
    private String msg;


    ResponseEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
