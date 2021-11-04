package com.example.demo.api;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.annotation.UserLoginToken;
import com.example.demo.entity.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.TokenUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class UserApi {
	@Autowired
	UserService userService;
	@Autowired
	TokenService tokenService;

	/**
	 * http://localhost:8888/login?password=123
	 * 登录
 	 */
	@GetMapping("/login")
	public Object login(User user, HttpServletResponse response) {
		JSONObject jsonObject = new JSONObject();
		User byUsername = userService.findByUsername(user);

		if (!byUsername.getPassword().equals(user.getPassword())) {
			jsonObject.put("message", "登录失败,密码错误");
			return jsonObject;
		} else {
			String token = tokenService.getToken(byUsername);
			jsonObject.put("token", token);

			Cookie cookie = new Cookie("token", token);
			cookie.setPath("/");
			response.addCookie(cookie);

			return jsonObject;

		}
	}

	/***
	 * 这个请求需要验证token才能访问
	 * 
	 * @author: MRC
	 * @date 2019年5月27日 下午5:45:19
	 * @return String 返回类型
	 *
	 * post man 中 headers 带上token 和 token值 如eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxIiwiZXhwIjoxNjMxNjMyNTE2LCJpYXQiOjE2MzE2Mjg5MTZ9.SwgZ3sRx_ahvBPq8vOT-1LHZsm_WeGZh3_rDX-gcnjI
	 * http://localhost:8888/getMessage
	 *
	 * 1.在vue中，向后台发送请求，不管是get或post，url要带上userId，headers要带上token值（本地存储的token，window.localStorage[‘token’]）
	 *
	 */
	@UserLoginToken
	@GetMapping("/getMessage")
	public String getMessage() {
		System.out.println("111111111");
		// 取出token中带的用户id 进行操作
		System.out.println(TokenUtil.getTokenUserId());

		return "你已通过验证";
	}

	/**
	 * 使用久token获取新token
	 * @param httpServletRequest
	 * @return
	 */
	@UserLoginToken
	@PostMapping("tokenDelay")
	@CrossOrigin(allowCredentials="true",maxAge = 3600)
	public Map<String,String> tokenDelay (HttpServletRequest httpServletRequest){
		Map<String,String> responseMap = new HashMap<>();
		try {
			responseMap = tokenService.tokenDelay(httpServletRequest);
		} catch (IOException e) {
			responseMap.put("message","token延时异常，请重新登陆");
			responseMap.put("code","510");
			responseMap.put("success","false");
			responseMap.put("token","");
			e.printStackTrace();
		}
		return responseMap;
	}
}
