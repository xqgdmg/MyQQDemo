package com.test.MyQQ.util;

import com.hyphenate.util.HanziToPinyin;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class StringUtil {
    private static final String usernameRegex  = "[a-zA-Z]\\w{2,19}";
    private static final String passwordRegex = "\\d{3,20}";
    //验证用户名是否合法
    public static boolean matchUsername(String username){
       return username.matches(usernameRegex);
    }
    //验证密码是否合法
    public static boolean matchPas(String pas){
        return pas.matches(passwordRegex);
    }
    //获取名称首字母
    public static String getFirstC(String username){
        HanziToPinyin hanziToPinyin = HanziToPinyin.getInstance();
        ArrayList<HanziToPinyin.Token> tokens = hanziToPinyin.get(username);
        return tokens.get(0).target.substring(0,1).toUpperCase();
    }
}
