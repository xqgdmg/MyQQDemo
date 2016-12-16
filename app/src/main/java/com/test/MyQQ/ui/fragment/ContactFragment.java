package com.test.MyQQ.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.test.MyQQ.adapter.ContactAdapter;
import com.test.MyQQ.db.DBUtil;
import com.test.MyQQ.ui.activity.AddActivity;
import com.test.MyQQ.ui.activity.ChatActivity;
import com.test.MyQQ.util.StringUtil;
import com.test.MyQQ.util.ThreadUtil;
import com.test.MyQQ.view.ContactView;
import itheima.com.qqDemo.R;

/**
 * 联系人
 *
 */
public class ContactFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private ArrayList<String> contacts = new ArrayList<String>();
    private ContactView contactView;
    private ContactAdapter adapter;
    private String currentUser;
    private SwipeRefreshLayout contact_refresh;

    @Override
    protected void initHeader(ImageView back, TextView my_title, ImageView add) {
        back.setVisibility(View.INVISIBLE);
        my_title.setText("联系人");
        add.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        //显示刷新控件
        contact_refresh.post(new Runnable() {
            @Override
            public void run() {
                contact_refresh.setRefreshing(true);
            }
        });
        //获取当前登录用户
        currentUser = EMClient.getInstance().getCurrentUser();
        //从数据库加载联系人数据
        loadContactsFromDB();
        //从环信加载联系人数据
        loadContactsFromServer();
    }

    /**
     * 从本地数据库中加载联系人数据
     * 通过当前登录用户的用户名进行查询
     * 查询结果进行排序
     */
    private void loadContactsFromDB() {
        //contacts集合清空
        contacts.clear();
        contacts.addAll(DBUtil.queryContact(getContext(), currentUser));
        //排序
        sort();
        //刷新列表
        adapter.notifyDataSetChanged();
    }

    /**
     * 集合排序 Collections
     * compare
     */
    public void sort() {
        Collections.sort(contacts, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String lhsC = StringUtil.getFirstC(lhs);
                String rhsC = StringUtil.getFirstC(rhs);
                return lhsC.compareTo(rhsC);// String 类的 compareTo
            }
        });
    }

    /**
     * 从环信上加载联系人
     * 和数据库 操作的是同一个集合
     * 同样需要排序
     * 并加入到数据库，更新数据库的联系人
     */
    public void loadContactsFromServer() {
        ThreadUtil.runOnNewThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //获取联系人成功
                    contacts.clear();
                    //更新contacts集合
                    contacts.addAll(usernames);
                    //排序
                    sort();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //列表刷新
                            adapter.notifyDataSetChanged();
                        }
                    });
                    //更新数据库
                    DBUtil.insertContacts(getContext(), currentUser, contacts);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //获取联系人失败
                    toast("从服务器获取联系人失败," + e.getMessage());
                } finally {
                    //隐藏刷新控件
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contact_refresh.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initListener() {
        adapter = new ContactAdapter(contacts);
        contactView.setAdapter(adapter);
        //设置刷新监听
        contact_refresh.setOnRefreshListener(this);
        //给联系人列表添加长按事件
        contactView.setOnLongPressListener(this);
        //添加联系人条目点击事件
        contactView.setonItemClickListener(this);
    }

    @Override
    protected void initView() {
        contactView = (ContactView) findViewById(R.id.contactView);
        contact_refresh = (SwipeRefreshLayout) contactView.findViewById(R.id.contact_refresh);
        //设置颜色
        contact_refresh.setColorSchemeColors(Color.GREEN, Color.YELLOW, Color.RED);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    /**
     * 点击事件
     *
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.add){
            //跳转到添加联系人界面
            startNewActivity(AddActivity.class,false);
        }
    }

    //刷新控件
    @Override
    public void onRefresh() {
        //从网络上获取联系人数据
        loadContactsFromServer();
        //设置列表滚动到第一条
//        contactView.setSelection(0);
    }

    /**
     * 联系人条目长按事件
     * 删除好友
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDeleteDialog(position);
        return true;
    }

    /**
     * 显示删除联系人对话框
     */
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("警告");
        builder.setMessage("你确定和" + contacts.get(position) + "友尽了吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //执行删除联系人
                deleContact(position);
            }
        }).show();
    }

    /**
     * 删除联系人
     * 从环信中删除
     */
    private void deleContact(final int position) {
        ThreadUtil.runOnNewThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contacts.get(position));
                    //删除成功
                    //更新联系人界面
//                    loadContactsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //删除失败
                    toast("删除好友失败");
                }
            }
        });
    }

    /**
     * 条目点击
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //进入聊天界面
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("chat_to",contacts.get(position));
        startActivity(intent);
    }
}
