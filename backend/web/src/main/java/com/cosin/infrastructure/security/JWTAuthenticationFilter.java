package com.cosin.infrastructure.security;

import com.cosin.model.po.UserPO;
import com.cosin.model.vo.ResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private ObjectMapper objectMapper;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.objectMapper = new ObjectMapper();
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
//            UsernamePasswordAuthenticationToken 继承 AbstractAuthenticationToken 实现 Authentication
//            所以当在页面中输入用户名和密码之后首先会进入到 UsernamePasswordAuthenticationToken验证(Authentication)，
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (NoneTokenException e) {
            System.out.println(e.getMessage());
//            throw new IOException(e.getMessage());
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            PrintWriter out = response.getWriter();
//            ResponseVO res= ResponseVO.buildFailure("未登录");
//            out.write(objectMapper.writeValueAsString(res));
//            out.flush();
//            out.close();
//            return;
        } catch (InvalidTokenException e){
            System.out.println(e.getMessage());
//            throw new IOException(e.getMessage());
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            PrintWriter out = response.getWriter();
//            ResponseVO res= ResponseVO.buildFailure("验证失败");
//            out.write(objectMapper.writeValueAsString(res));
//            out.flush();
//            out.close();
//            return;
        } catch (ExpiredTokenException e){
            System.out.println(e.getMessage());
//            return;
//            throw new IOException(e.getMessage());
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            PrintWriter out = response.getWriter();
//            ResponseVO res= ResponseVO.buildFailure("登录过期");
//            out.write(objectMapper.writeValueAsString(res));
//            out.flush();
//            out.close();
//            return;
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws NoneTokenException, InvalidTokenException,ExpiredTokenException {
        String header = request.getHeader(JwtTokenUtils.TOKEN_NAME);
        if(header==null){
            throw new NoneTokenException("无验证");
        }
        try{
            JwtTokenUtils.getClaimsFromToken(header);
        }catch (ExpiredJwtException e){
            throw new ExpiredTokenException("token已过期");
        }catch (Exception e){
            throw new InvalidTokenException("解析失败");
        }
        String username = JwtTokenUtils.getUsernameFromToken(header);
        int id = JwtTokenUtils.getIdFromToken(header);
        List<GrantedAuthority> authorities = new ArrayList<>();
        logger.info("username：" + username);
        logger.info("id：" + id);
        logger.info("create time：" + JwtTokenUtils.getCreatedDateFromToken(header));
        logger.info("expired time：" + JwtTokenUtils.getExpirationDateFromToken(header));
//            踩坑提醒 此处password不能为null
        UserPO principal = new UserPO().setUsername(username).setId(id);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    private class InvalidTokenException extends Exception {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    private class NoneTokenException extends Exception {
        public NoneTokenException(String message) {
            super(message);
        }
    }

    private class ExpiredTokenException extends Exception {
        public ExpiredTokenException(String message) {
            super(message);
        }
    }

}

