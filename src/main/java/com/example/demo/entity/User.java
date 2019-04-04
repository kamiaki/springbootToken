package com.example.demo.entity;
/* 
* @author MRC 
* @date 2019年4月4日 下午7:04:38 
* @version 1.0 
*/
public class User {

	private String Id;
    private String username;
    private String password;
    
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    
	
}
