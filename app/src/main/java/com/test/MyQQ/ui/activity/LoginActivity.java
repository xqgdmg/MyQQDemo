package com.test.MyQQ.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import com.test.MyQQ.util.StringUtil;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private TextView login_regist;
    private Button login_loginBtn;
    private EditText login_password_edit;
    private EditText login_username_edit;
    private ProgressDialog dialog;

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        login_regist.setOnClickListener(this);
        login_username_edit.setOnEditorActionListener(this);
        login_password_edit.setOnEditorActionListener(this);
        login_loginBtn.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        login_username_edit = (EditText) findViewById(R.id.login_username_edit);
        login_password_edit = (EditText) findViewById(R.id.login_password_edit);
        login_loginBtn = (Button) findViewById(R.id.login_loginBtn);
        login_regist = (TextView) findViewById(R.id.login_regist);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void processClick(View v) {
        if (v.getId() == R.id.login_regist) {
            //跳转到注册界面
            startNewActivity(RegistActivity.class, false);
        } else if (v.getId() == R.id.login_loginBtn) {
            login();
        }
    }

    //当activity设置成singletask  后面启动时会执行onnewIntent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String username = intent.getStringExtra(KEY_USERNAME);
        String password = intent.getStringExtra(KEY_PASSWORD);
        //设置用户名和密码输入
        login_username_edit.setText(username);
        login_password_edit.setText(password);
    }

    //输入法事件
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.login_username_edit && actionId == EditorInfo.IME_ACTION_NEXT) {
            String username = login_username_edit.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                toast("用户名不能为空");
            } else {
                //用户名输入框
                //验证当前用户名是否合法
                if (StringUtil.matchUsername(username)) {
                    //如果合法  密码框获取焦点
                    login_password_edit.requestFocus();
                } else {
                    toast("用户名不合法");
                }
            }
        } else if (v.getId() == R.id.login_password_edit && actionId == EditorInfo.IME_ACTION_GO) {
            //密码输入框
            String password = login_password_edit.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                toast("密码不能为空");

            } else {
                //密码是否合法
                if (StringUtil.matchPas(password)) {
                    //登录
                    login();
                } else {
                    toast("密码不合法");
                }
            }
        }
        return true;
    }

    //登录
    private void login() {
        //获取用户名和密码
        String username = login_username_edit.getText().toString().trim();
        String password = login_password_edit.getText().toString().trim();
        //判断用户名和密码是否为空
        if (TextUtils.isEmpty(username)) {
            toast("用户名不能为空");
            login_username_edit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            toast("密码不能为空");
            login_password_edit.requestFocus();
            return;
        }

        //判断用户名和密码是否合法
        if (!StringUtil.matchUsername(username)) {
            toast("用户名不合法");
            login_username_edit.requestFocus();
            return;
        }
        if (!StringUtil.matchPas(password)) {
            toast("密码不合法");
            login_password_edit.requestFocus();
            return;
        }
        //显示对话框
        dialog = makeDialog("正在登录");
        dialog.show();
        //登录
        EMClient.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                if(dialog!=null){
                    dialog.dismiss();
                    dialog=null;
                }
                //登录成功  进入主界面
                startNewActivity(MainActivity.class,true);
            }

            @Override
            public void onError(int i, String s) {
                if(dialog!=null){
                    dialog.dismiss();
                    dialog=null;
                }
                //登录失败
                toast("登录失败,"+s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
