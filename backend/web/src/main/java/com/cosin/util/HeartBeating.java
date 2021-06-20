package com.cosin.util;

import com.cosin.model.vo.qa.HelperQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * 心跳线程
 */
public class HeartBeating {


    public static int intervalTime = 10000;

    public static int timeOut = 1000;

    public static boolean alive = true;

    public static void startBeating() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                String url = "http://127.0.0.1:5000/health";
                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                requestFactory.setConnectTimeout(timeOut);
                requestFactory.setReadTimeout(timeOut);
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                System.out.println(intervalTime);
                while (true) {
                    ResponseEntity<Boolean> responseEntity;
                    try {
                        responseEntity = restTemplate.getForEntity(url, Boolean.class);
                        if (responseEntity.getBody() == null || !responseEntity.getBody()) {
                            alive = false;
                        } else {
                            alive = true;
                        }
                    } catch (Exception e) {
                        alive = false;
                    }
                    try {
                        Thread.sleep(intervalTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}