package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.entity.User;

import java.util.Date;

import org.springframework.stereotype.Service;

/**
 * @author jinbin
 * @date 2018-07-08 21:04
 */
@Service("TokenService")
public class TokenService {

	public String getToken(User user) {
		Date start = new Date();
		long currentTime = System.currentTimeMillis() + 1* 60 * 1000;
		Date end = new Date(currentTime);
		String token = "";
		
		token = JWT.create().withAudience(user.getId()).withIssuedAt(start).withExpiresAt(end)
				.sign(Algorithm.HMAC256(user.getPassword()));
		return token;
	}
}
