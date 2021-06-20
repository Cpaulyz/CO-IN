package com.cosin.dao;

import com.cosin.COINWebApplication;
import com.cosin.dao.mysql.UserMapper;
import com.cosin.model.po.UserPO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testInsertUser(){
        UserPO userPO = new UserPO().setUsername("name").setPassword("123456").setEmail("test@qq.com");
        int res = userMapper.insertUser(userPO);
        assert res>0;
    }
    @Test
    public void  testGetUserByUsername(){
        UserPO userPO = userMapper.getUserByUsername("admin");
        assert userPO!=null;
    }

}
