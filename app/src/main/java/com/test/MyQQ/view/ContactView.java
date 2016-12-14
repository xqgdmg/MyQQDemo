package com.test.MyQQ.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.test.MyQQ.adapter.ContactAdapter;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class ContactView extends RelativeLayout {

    private ListView contact_listView;

    public ContactView(Context context) {
        super(context);
        initView();
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    //初始化view
    private void initView() {
        //第三个参数设置为true  直接解析之后添加到当前view中
        LayoutInflater.from(getContext()).inflate(R.layout.coantct_view,this,true);
        contact_listView = (ListView) findViewById(R.id.contact_listView);

    }
    //设置列表的适配器
    public void setAdapter(ContactAdapter adapter) {
        contact_listView.setAdapter(adapter);
    }

    public void setSelection(int i) {
        contact_listView.setSelection(i);
    }

    //给列表添加长按事件
    public void setOnLongPressListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        contact_listView.setOnItemLongClickListener(onItemLongClickListener);
    }
    //列表条目点击事件
    public void setonItemClickListener(AdapterView.OnItemClickListener onItemClickListener ) {
        contact_listView.setOnItemClickListener(onItemClickListener);
    }
}
