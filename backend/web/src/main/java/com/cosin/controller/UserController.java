package com.cosin.controller;

import com.cosin.infrastructure.security.JwtTokenUtils;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.project.ProjectDescriptionVO;
import com.cosin.model.vo.user.UserInfoVO;
import com.cosin.model.vo.user.UserLoginVO;
import com.cosin.model.vo.user.UserVO;
import com.cosin.service.UserService;
import com.cosin.util.RSAManager;
import com.cosin.util.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册控制类
 */
@Api(tags = "用户控制")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    private static final String SIGNUP_SUCCESS="注册成功";
    private static final String NO_LOGIN="未登录";

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public ResponseEntity<ResponseVO> signUp(@RequestBody UserVO userVO){
        ResponseVO res =  userService.signUpUser(userVO);
        if(res.isSuccess()){
            return ResponseUtils.success(SIGNUP_SUCCESS);
        }else{
            return ResponseUtils.failure(res);
        }
    }

    @GetMapping("/info")
    @ApiOperation("查看当前用户信息")
    @ApiResponses({
            @ApiResponse(code = 200,message = "成功",response = UserInfoVO.class),
            @ApiResponse(code = 500,message = "错误·",response = ResponseVO.class)
    })
    public ResponseEntity userInfo(){
        try {
            return ResponseUtils.success(userService.userInfo());
        }catch (Exception e){
            return ResponseUtils.failure(NO_LOGIN);
        }
    }

    @PostMapping("/login")
    @ApiOperation("登录，返回token在header中")
    public ResponseEntity<ResponseVO> login(UserLoginVO loginVO){
        ResponseVO res = userService.login(loginVO);
        if(res.isSuccess()){
            String token = res.getMsg();
            HttpHeaders headers = new HttpHeaders();
            headers.add(JwtTokenUtils.TOKEN_NAME,token);
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,JwtTokenUtils.TOKEN_NAME);
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(ResponseVO.buildFailure("登陆成功"));
        }else{
            return ResponseUtils.failure(res);
        }
    }

    @PostMapping("/logout")
    @ApiOperation("退出")
    public ResponseEntity<ResponseVO> logout(){
        return ResponseUtils.success("退出成功");
    }


    @GetMapping("/pubKey")
    @ApiOperation("获取rsa-publicKey")
    public ResponseEntity<ResponseVO> rsaPubKey(){
        return ResponseUtils.success(RSAManager.getPublicKey()) ;
    }
}
