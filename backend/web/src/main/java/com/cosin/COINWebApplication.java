package com.cosin;

import com.cosin.util.HeartBeating;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement  //开启事务
public class COINWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(COINWebApplication.class);
        HeartBeating.startBeating();
    }
}
