package com.heasy.knowroute.bean;

/**
 * Created by Administrator on 2020/9/27.
 */
public class UserBean {
	private int id;
    private String account;
    private String password;
    private String role;

    public UserBean(){

    }

    public UserBean(int id, String account, String password, String role){
    	this.id = id;
        this.account = account;
        this.password = password;
        this.role = role;
    }

    public UserBean(String account, String password, String role){
        this.account = account;
        this.password = password;
        this.role = role;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
