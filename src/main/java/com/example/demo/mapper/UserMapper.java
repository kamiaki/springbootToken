package com.example.demo.mapper;


import com.example.demo.entity.User;

/**
 * @author jinbin
 * @date 2018-07-08 20:44
 */
public interface UserMapper {
	
    User findByUsername(String username);
    
    User findUserById(String Id);
}
