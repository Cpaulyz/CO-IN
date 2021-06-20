package com.cosin.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 配置事务管理器，需要使用MySQL事务时需要手动设定 @Transactional(value = "txManager")，否则会使用neo4j事务管理器
 */
@Configuration
@EnableTransactionManagement //开启事务管理
public class MyBatisConfig {

    @Autowired
    DataSource dataSource;


    /*配置事务管理器*/
    @Bean("transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}