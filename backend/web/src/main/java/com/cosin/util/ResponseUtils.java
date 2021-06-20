package com.cosin.util;

import com.cosin.model.vo.ResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    private static ObjectMapper objectMapper;
    private static HttpHeaders headersJson;
    static {
        objectMapper = new ObjectMapper();
        headersJson = new HttpHeaders();
        headersJson.setContentType(MediaType.APPLICATION_JSON);
    }

    public static <T> ResponseEntity<T> create(T data, HttpStatus status){
        return new ResponseEntity<>(data,headersJson,status);
    }

    public static <T> ResponseEntity<T> success(T data){
        return create(data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ResponseVO> success(){
        return create(ResponseVO.buildSuccess(), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ResponseVO> success(String message){
        return create(ResponseVO.buildSuccess(message), HttpStatus.OK);
    }

    public static ResponseEntity<ResponseVO> failure(String message){
        return create(ResponseVO.buildFailure(message),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<ResponseVO> failure(ResponseVO data){
        return create(data,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
