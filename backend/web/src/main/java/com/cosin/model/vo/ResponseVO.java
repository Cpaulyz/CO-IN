package com.cosin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T>  {
    private boolean success;

    private String msg;


    public ResponseVO(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static ResponseVO buildSuccess(String msg) {
        return new ResponseVO(true,msg);
    }
    public static ResponseVO buildSuccess() {
        return new ResponseVO(true,"");
    }
    public static ResponseVO buildFailure(String msg) {
        return new ResponseVO(false,msg);
    }




}
