package com.cosin.service;

import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.user.UserInfoVO;
import com.cosin.model.vo.user.UserLoginVO;
import com.cosin.model.vo.user.UserVO;

public interface UserService {
    /**
     * 注册用户
     * @param userVO 用户信息
     * @return 是否成功
     */
    ResponseVO signUpUser(UserVO userVO);

    /**
     * 获取当前用户信息
     * @return 获取当前用户信息
     */
    UserInfoVO userInfo();

    /**
     * 登录
     * @return 如果成功，msg为token； 如果失败，msg为错误信息
     */
    ResponseVO login(UserLoginVO loginVO);
}
