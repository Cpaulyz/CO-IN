package com.cosin.serviceImpl;

import com.cosin.dao.mysql.UserMapper;
import com.cosin.infrastructure.security.JwtTokenUtils;
import com.cosin.model.po.UserPO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.user.UserInfoVO;
import com.cosin.model.vo.user.UserLoginVO;
import com.cosin.model.vo.user.UserVO;
import com.cosin.service.UserService;
import com.cosin.util.RSAManager;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * 登录专用类
 * 自定义类，实现了UserDetailsService接口，用户登录时调用的第一类
 * @author chenyz
 *
 */
@Service("userServiceImpl")
//public class UserServiceImpl implements UserDetailsService, UserService {
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    // 用于密码加密
    private static PasswordEncoder passwordEncoder;

    static {
        passwordEncoder = new BCryptPasswordEncoder();
    }

//    /**
//     * 登陆验证时，通过username获取用户的所有权限信息，这里我们通过
//     * 并返回UserDetails放到spring的全局缓存SecurityContextHolder中，以供授权器使用
//     */
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        //在这里可以自己调用数据库，对username进行查询，看看在数据库中是否存在
//        UserPO userPO = userMapper.getUserByUsername(username);
//        if(userPO==null){
//            throw new UsernameNotFoundException("账号 "+username+" 不存在");
//        }
//        return userPO;
//    }

    @Override
    public ResponseVO signUpUser(UserVO userVO) {
        userVO.setPassword(RSAManager.decrypt(userVO.getPassword()));
        UserPO userPO = convertUserVO2UserPO(userVO);
        try {
            int res = userMapper.insertUser(userPO);
            if(res>0){
                return ResponseVO.buildSuccess();
            }else{
                return ResponseVO.buildFailure("未知错误");
            }
        }catch (Exception e){
            return ResponseVO.buildFailure("用户名已存在");
        }
    }

    @Override
    public UserInfoVO userInfo() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPO userPO = userMapper.getUserByUsername(userDetails.getUsername());
        return new UserInfoVO(userPO);
    }

    @Override
    public ResponseVO login(UserLoginVO loginVO) {
        loginVO.setPassword(RSAManager.decrypt(loginVO.getPassword()));
        UserPO userPO = userMapper.getUserByUsername(loginVO.getUsername());
        if(userPO==null){
            return ResponseVO.buildFailure("账号不存在");
        }
        boolean match = passwordEncoder.matches(loginVO.getPassword(),userPO.getPassword());
        if(match){
            return ResponseVO.buildSuccess(JwtTokenUtils.generateToken(userPO));
        }else{
            return ResponseVO.buildFailure("密码错误");
        }
    }

    /**
     * UserVO和UserPO之间的转换，涉及到解密加密
     * @param userVO userVO
     * @return 解密加密后的UserPO
     */
    private UserPO convertUserVO2UserPO(UserVO userVO){
        UserPO userPO = new UserPO(userVO);
        userPO.setPassword(passwordEncoder.encode(userPO.getPassword()));
        return userPO;
    }
}