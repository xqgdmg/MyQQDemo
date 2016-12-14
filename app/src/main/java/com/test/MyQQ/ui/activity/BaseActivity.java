package com.test.MyQQ.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.test.MyQQ.QQApplication;
import com.test.MyQQ.util.LogUtil;
import com.test.MyQQ.util.ToastUtil;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected String KEY_USERNAME="username";
    protected String KEY_PASSWORD="password";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //添加
        ((QQApplication)getApplication()).insertActivity(this);

        initView();
        initListener();
        initData();
        regCommonBtn();
    }

    //处理公用按钮
    private void regCommonBtn() {
        View back = findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(this);
        }
    }

    //初始化值
    protected abstract void initData();

    //初始化监听
    protected abstract void initListener();

    //初始化view
    protected abstract void initView();

    //获取布局id
    protected abstract int getLayoutId();

    //打印log
    protected void logD(String msg) {
        LogUtil.logD(getClass().getName(), msg);
    }

    //创建一个progressdialog
    protected ProgressDialog makeDialog(String msg) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(msg);
        return dialog;
    }
//    Handler handler = new Handler();
    //弹出toast
    protected void toast(final String msg) {
//        if(Looper.myLooper()==getMainLooper())
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(getApplicationContext(), msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else {
            processClick(v);
        }
    }

    //处理除了back按钮外的点击事件
    protected abstract void processClick(View v);

    //跳转到新界面
    protected void startNewActivity(Class clazz, boolean finishCurrent) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (finishCurrent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((QQApplication)getApplication()).deleteActivity(this);
    }
}
