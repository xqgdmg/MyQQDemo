package com.test.MyQQ.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class Student extends BmobObject {
    private String username;
    private String password;

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
