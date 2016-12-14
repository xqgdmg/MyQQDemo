package com.test.MyQQ.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.MyQQ.adapter.ContactAdapter;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class SlidBar extends View {
    private String[] names = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private Paint paint;
    private int viewW;
    private int singleHeight;
    private TextView contact_toast;
    private ListView contact_listView;

    public SlidBar(Context context) {
        super(context);
        initView();
    }

    public SlidBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SlidBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#8c8c8c"));
        paint.setTextAlign(Paint.Align.CENTER);//x以字体的x方向中点为坐标点
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewW = w;
        int viewH = h;
        singleHeight = viewH / (names.length + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < names.length; i++) {
            canvas.drawText(names[i], viewW / 2, singleHeight * (i + 1), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //改变背景颜色
                setBackgroundResource(R.drawable.shape_slibar);
                performTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                performTouch(event);
                break;
            case MotionEvent.ACTION_UP:
                //设置背景为透明
                setBackgroundColor(Color.TRANSPARENT);
                //隐藏toast
                if(contact_toast!=null){
                    contact_toast.setVisibility(GONE);
                }
                break;
            default:
                break;
        }
        return true;
    }
    //处理触摸事件
    private void performTouch(MotionEvent event) {
        //确定触摸的是哪个字母
        float eventY = event.getY();
        int index = (int) (eventY / singleHeight);
        if(index<0){
            index=0;
        }else if(index>names.length-1){
            index = names.length-1;
        }
        //获取当前要显示的字母
        String name = names[index];
        //显示toast字母
        RelativeLayout parent = (RelativeLayout) getParent();//获取父容器
        if(contact_toast==null) {
            contact_toast = (TextView) parent.findViewById(R.id.contac_toast);
        }
        contact_toast.setText(name);
        //显示toast
        contact_toast.setVisibility(VISIBLE);

//处理列表section位置

        //初始化listview
        if(contact_listView==null) {
            contact_listView = (ListView) parent.findViewById(R.id.contact_listView);
        }
        //获取listview的适配器
        ContactAdapter adapter = (ContactAdapter) contact_listView.getAdapter();
        //获取sections的集合
        String[] sections = adapter.getSections();
        //当前是否要处理section
        int sectionIndex = -1;
        for (int i = 0; i < sections.length; i++) {
            if(name.equals(sections[i])){
                sectionIndex = i;
            }
        }
        if(sectionIndex==-1){
            return;
        }
        //确定当前section首联系人的position
        int positionForSection = adapter.getPositionForSection(sectionIndex);
        contact_listView.setSelection(positionForSection);
    }
}
