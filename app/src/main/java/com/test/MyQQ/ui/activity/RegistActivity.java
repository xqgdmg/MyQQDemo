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

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import com.test.MyQQ.bean.Student;
import com.test.MyQQ.util.StringUtil;
import com.test.MyQQ.util.ThreadUtil;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class RegistActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private Button registBtn;
    private EditText regist_username;
    private EditText regist_password;
    private ProgressDialog dialog;

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        regist_username.setOnEditorActionListener(this);
        regist_password.setOnEditorActionListener(this);
        registBtn.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        regist_password = (EditText) findViewById(R.id.regist_password_edit);
        regist_username = (EditText) findViewById(R.id.regist_username_edit);
        registBtn = (Button) findViewById(R.id.registBtn);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    protected void processClick(View v) {
        if (v.getId() == R.id.registBtn) {
            regist();
        }
    }

    //键盘事件监听
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.regist_username_edit && actionId == EditorInfo.IME_ACTION_NEXT) {
            //点击用户名下一步输入法时 验证用户名是否合法
            String username = regist_username.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                toast("用户名不能为空");
            } else {
                if (!StringUtil.matchUsername(username)) {
                    //用户名不合法 直接返回
                    toast("用户名不合法");
                } else {
                    //用户名合法 焦点移动到密码框
                    regist_password.requestFocus();
                }
            }
        }
        if (v.getId() == R.id.regist_password_edit && actionId == EditorInfo.IME_ACTION_GO) {
            String password = regist_password.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                toast("密码不能为空");
            } else {
                if (!StringUtil.matchPas(password)) {
                    toast("密码不合法");
                } else {
                    regist();
                }
            }
        }
        return true;
    }

    //注册用户
    private void regist() {
        //获取用户名和密码
        final String username = regist_username.getText().toString().trim();
        final String password = regist_password.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            toast("用户名不能为空");
            //移动焦点到用户名
            regist_username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            toast("密码不能为空");
            regist_password.requestFocus();
            return;
        }
        if (!StringUtil.matchUsername(username)) {
            toast("用户名不合法");
            regist_username.requestFocus();
            return;
        }
        if (!StringUtil.matchPas(password)) {
            toast("密码不合法");
            regist_password.requestFocus();
            return;
        }
        //显示对话框
        dialog = makeDialog("正在进行注册");
        dialog.show();

        //在bmob上注册 添加数据到bmob上
        final Student user = new Student();
        user.setUsername(username);
        user.setPassword(password);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    logD("bmob注册成功");
                    ThreadUtil.runOnNewThread(new Runnable() {
                        @Override
                        public void run() {
                            //在环信上注册
                            try {
                                //隐藏对话框
                                if (dialog != null) {
                                    dialog.dismiss();
                                    dialog = null;
                                }

                                EMClient.getInstance().createAccount(username, password);
                                //成功
                                toast("注册成功");
                                logD("环信注册成功");
                                //跳转到登录界面
                                Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                                intent.putExtra(KEY_USERNAME, username);
                                intent.putExtra(KEY_PASSWORD, password);
                                startActivity(intent);
                                finish();
                            } catch (HyphenateException e1) {
                                //隐藏对话框
                                if (dialog != null) {
                                    dialog.dismiss();
                                    dialog = null;
                                }

                                e1.printStackTrace();
                                toast("注册失败");
                                logD("环信注册失败：" + e1.getMessage());
                                //删除在bmob上注册的用户
                                deleBmobuser(user);
                            }
                        }
                    });
                } else {
                    //隐藏对话框
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog=null;
                    }
                    logD("bmob注册失败："+e.getMessage());
                    toast("注册失败");
                }
            }
        });

    }
    //删除bmob上注册的用户
    private void deleBmobuser(Student user) {
        user.delete();
    }
}
