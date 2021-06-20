package com.cosin.service;

import com.cosin.COINWebApplication;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.user.UserLoginVO;
import com.cosin.model.vo.user.UserVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    public void testSignUp(){
        UserVO userVO = new UserVO().setUsername("hahhahahah").setPassword("123456").setEmail("test@qq.com");
        ResponseVO responseVO = userService.signUpUser(userVO);
        assert responseVO.isSuccess();
    }

    @Test
    public void testSignUpFail(){
        UserVO userVO = new UserVO().setUsername("admin").setPassword("123456").setEmail("test@qq.com");
        ResponseVO responseVO = userService.signUpUser(userVO);
        assert !responseVO.isSuccess();
    }

    @Test
    public void testLoginSuccess(){
        UserLoginVO userLoginVO = new UserLoginVO().setUsername("admin").setPassword("123456");
        ResponseVO res =userService.login(userLoginVO);
        assert res.isSuccess();
    }

    @Test
    public void testLoginUserNoExist(){
        UserLoginVO userLoginVO = new UserLoginVO().setUsername("adminassss").setPassword("123456");
        ResponseVO res =userService.login(userLoginVO);
        assert !res.isSuccess();
    }

    @Test
    public void testLoginPasswordError(){
        UserLoginVO userLoginVO = new UserLoginVO().setUsername("admin").setPassword("12345655");
        ResponseVO res =userService.login(userLoginVO);
        assert !res.isSuccess();
    }
}
