package com.test.MyQQ.ui.activity;

import android.os.Handler;
import android.view.View;

import com.hyphenate.chat.EMClient;

import itheima.com.qqDemo.R;


/**
 * Created by ThinkPad on 2016/8/12.
 */
public class SplashActivity extends BaseActivity {
    private Handler handler = new Handler();

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void processClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //当前是否登录过
        if (isLogingBefore()) {
            //已经登录 直接进入主界面
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startNewActivity(MainActivity.class, true);
                }
            }, 2000);
        } else {
            //没有登录 进入登录界面
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startNewActivity(LoginActivity.class, true);
                }
            }, 2000);
        }
    }

    //判断当前是否登录  true为已经登录
    private boolean isLogingBefore() {
        if(EMClient.getInstance().isLoggedInBefore()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
