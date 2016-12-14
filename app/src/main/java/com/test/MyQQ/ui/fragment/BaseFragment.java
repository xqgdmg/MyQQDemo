package com.test.MyQQ.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.MyQQ.util.LogUtil;
import com.test.MyQQ.util.ToastUtil;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //parent  会根据parent的布局参数生成当前view的布局参数
        view = inflater.inflate(getLayoutId(),container,false);
        return view;
    }
    public View findViewById(int id){
        return  view.findViewById(id);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initData();

        //处理头中的布局
        ImageView back = (ImageView) findViewById(R.id.back);
        ImageView add = (ImageView) findViewById(R.id.add);
        TextView my_title = (TextView) findViewById(R.id.my_title);
        initHeader(back,my_title,add); 
    }
    //处理头布局
    protected abstract void initHeader(ImageView back, TextView my_title, ImageView add);


    protected abstract void initData();

    protected abstract void initListener();

    protected abstract void initView();

    //获取布局id
    protected abstract int getLayoutId();
    //打印log
    protected void logD(String msg){
        LogUtil.logD(getClass().getName(), msg);
    }
    //弹toast
    protected void toast(final String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(getContext(), msg);
            }
        });
    }
    //创建dialog
    protected ProgressDialog makeDialog(String msg){
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(msg);
        return dialog;
    }
    //开启新activity
    public void startNewActivity(Class clazz,boolean finish){
        Intent intent = new Intent(getContext(),clazz);
        startActivity(intent);
        if(finish){
            getActivity().finish();
        }
    }
}
