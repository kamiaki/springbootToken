package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.entity.User;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/***
 * token 下发
 * @Title: TokenService.java
 * @author MRC
 * @date 2019年5月27日 下午5:40:25
 * @version V1.0
 */
@Service("TokenService")
public class TokenService {

    @Autowired
    private UserService userService;

    public String getToken(User user) {
        Date start = new Date();
        long currentTime = System.currentTimeMillis() + 1 * 60 * 1000;// 60秒有效期
        Date end = new Date(currentTime);
        String token;

        token = JWT.create().withAudience(user.getId()).withIssuedAt(start).withExpiresAt(end)
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }


    /**
     * 使用旧token更换新token
     *
     * @param httpServletRequest
     * @return
     * @throws IOException
     */
    public Map<String, String> tokenDelay(HttpServletRequest httpServletRequest) throws IOException {
        Map<String, String> responseMap = new HashMap<>();
        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
        if (token == null) {
            responseMap.put("message", "无token，请重新登陆");
            responseMap.put("code", "510");
            responseMap.put("success", "false");
            responseMap.put("token", "");
            return responseMap;
        }
        // 获取 token 中的 user id
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            responseMap.put("message", "用户ID异常，请重新登录");
            responseMap.put("code", "510");
            responseMap.put("success", "false");
            responseMap.put("token", "");
            return responseMap;
        }
        //TODO 从数据库中查找用户信息
//        Map<String,Object> user = null;
        User user = userService.findUserById(userId);
        if (user == null) {
            responseMap.put("message", "用户不存在，请重新登录");
            responseMap.put("code", "510");
            responseMap.put("success", "false");
            responseMap.put("token", "");
            return responseMap;
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            responseMap.put("message", "token超时,请重新登陆");
            responseMap.put("code", "510");
            responseMap.put("success", "false");
            responseMap.put("token", "");
            return responseMap;
        }
        //创建token
        Date start = new Date();
        long currentTime = System.currentTimeMillis() + 1 * 60 * 1000;// 60秒 有效期
        Date end = new Date(currentTime);
        String newToken;
        //提取用户id
        String id = String.valueOf(user.getId());
        //提取用户加密密码
        String password = user.getPassword();
        //生成token
        newToken = JWT.create().withAudience(id).withIssuedAt(start).withExpiresAt(end)
                .sign(Algorithm.HMAC256(password));
        responseMap.put("message", "");
        responseMap.put("code", "");
        responseMap.put("success", "true");
        responseMap.put("token", newToken);
        return responseMap;
    }

}
