package com.test.MyQQ.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.greenrobot.event.EventBus;
import com.test.MyQQ.adapter.AddAdapter;
import com.test.MyQQ.bean.Student;
import com.test.MyQQ.util.ThreadUtil;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/13.
 */
public class AddActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private ImageView add_nodata;
    private RecyclerView add_recycleView;
    private ImageView add_searchBtn;
    private EditText add_edit;
    private ImageView add;
    private TextView main_title;
    private ImageView back;
    private ArrayList<Student> students = new ArrayList<Student>();
    private ArrayList<String> contacts = new ArrayList<String>();
    private AddAdapter adapter;
    private String currentUser;
    private ProgressDialog dialog;
    public void onEventMainThread(String msg){
        toast("收到更新联系人请求");
        loadContactFromServer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {
        //注册eventbus
        EventBus.getDefault().register(this);
        //初始化标题
        main_title.setText("添加好友");
        //获取当前用户
        currentUser = EMClient.getInstance().getCurrentUser();
        //加载联系人
        loadContactFromServer();
    }

    //加载网络联系人
    private void loadContactFromServer() {
        ThreadUtil.runOnNewThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> allContactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    contacts.clear();
                    contacts.addAll(allContactsFromServer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initListener() {
        add_edit.setOnEditorActionListener(this);
        add_searchBtn.setOnClickListener(this);
        //创建adapter
        adapter = new AddAdapter(students, contacts);
        add_recycleView.setAdapter(adapter);

        //设置添加按钮的监听
        adapter.setAddListener(new AddAdapter.AddListener() {
            @Override
            public void onAddListen(String username) {
                //添加好友
                addContact(username);
            }
        });
    }
    //添加好友
    private void addContact(final String username) {
        ThreadUtil.runOnNewThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username, "一起玩耍吧");
                    //添加成功
                    toast("添加成功");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //添加失败
                    toast("添加失败，" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        main_title = (TextView) findViewById(R.id.my_title);
        add = (ImageView) findViewById(R.id.add);
        add_edit = (EditText) findViewById(R.id.add_edit);
        add_searchBtn = (ImageView) findViewById(R.id.add_searchBtn);
        add_recycleView = (RecyclerView) findViewById(R.id.add_recycleView);
        add_nodata = (ImageView) findViewById(R.id.add_nodata);
        add.setVisibility(View.INVISIBLE);

        //recycleview的初始化 列表展示
        add_recycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add;
    }

    @Override
    protected void processClick(View v) {
        if (v.getId() == R.id.add_searchBtn) {
            String key = add_edit.getText().toString().trim();
            if (TextUtils.isEmpty(key)) {
                toast("请输入用户名进行搜索");
            } else {
                //搜索好友
                searchContact(key);
            }
        }
    }

    //输入法监听
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.add_edit && actionId == EditorInfo.IME_ACTION_SEARCH) {
            String key = add_edit.getText().toString().trim();
            if (TextUtils.isEmpty(key)) {
                toast("请输入用户名进行搜索");
            } else {
                //搜索好友
                searchContact(key);
            }
        }
        return true;
    }

    //搜索好友
    private void searchContact(String key) {
        //添加对话框
        dialog = makeDialog("正在查询");
        dialog.show();
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.addWhereStartsWith("username", key);
        query.addWhereNotEqualTo("username", currentUser);
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                toast("查询成功");
                if (e == null) {
                    //查询成功 添加到列表中
                    students.clear();
                    students.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    //查询失败
                    toast("查询失败,"+e.getMessage());
                    System.out.println("查询失败，"+e.getMessage());
                }
            }
        });
    }
}
