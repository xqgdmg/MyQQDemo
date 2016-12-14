package com.test.MyQQ.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import com.test.MyQQ.ui.activity.LoginActivity;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class PluginFragment extends BaseFragment {

    private Button plugin_logout;
    private String currentUser;

    @Override
    protected void initHeader(ImageView back, TextView my_title, ImageView add) {
        back.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        my_title.setText("动态");
    }

    @Override
    protected void initData() {
        //获取当前登录的用户名
        currentUser = EMClient.getInstance().getCurrentUser();
        //初始化退出按钮
        plugin_logout.setText(plugin_logout.getText().toString().replace("%",currentUser));
    }

    @Override
    protected void initListener() {
        plugin_logout.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        plugin_logout = (Button) findViewById(R.id.plugin_logout);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_plugin;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.plugin_logout){
            //退出登录
            EMClient.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    //退出登录成功  进入登录界面
                    startNewActivity(LoginActivity.class,true);
                }

                @Override
                public void onError(int i, String s) {
                    //退出登录失败  tost
                    toast("退出登录失败");
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }
}
