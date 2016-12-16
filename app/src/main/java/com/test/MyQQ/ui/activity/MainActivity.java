package com.test.MyQQ.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import com.test.MyQQ.listener.MymessageListener;
import com.test.MyQQ.ui.fragment.BaseFragment;
import com.test.MyQQ.ui.fragment.ContactFragment;
import com.test.MyQQ.ui.fragment.ConversationFragment;
import com.test.MyQQ.ui.fragment.PluginFragment;
import itheima.com.qqDemo.R;

public class MainActivity extends BaseActivity {
    private int originalIndex = 0;
    private ImageView tab_conversation;
    private ImageView tab_contact;
    private ImageView tab_plugin;
    private TextView main_unread;
    private ArrayList<BaseFragment> fragments = new ArrayList<BaseFragment>();
    private MymessageListener messageListener;

    @Override
    protected void initData() {
        //加载会话
        EMClient.getInstance().chatManager().loadAllConversations();

        //初始化界面
        initFragment();

        //添加联系人监听
        setContactListener();

        //添加消息监听
        setMessageListener();

        //更新未读消息 onResume
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新未读消息
        handleUnread();
    }

    //消息监听
    private void setMessageListener() {
        messageListener = new MymessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新未读条数
                        //接收到新消息
                        handleUnread();
                        //更新会话界面
                        updateConversationFragment();
                    }
                });
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    //更新会话界面
    private void updateConversationFragment() {
        //判断会话界面是否已经添加到界面中
        ConversationFragment fragment = (ConversationFragment) fragments.get(0);
        if (fragment.isAdded()) {
            //更新会话界面
            fragment.loadAllConversation();
        }
    }

    //更新未读条数
    private void handleUnread() {
        //获取当前所有未读消息个数
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount == 0) {
            main_unread.setVisibility(View.GONE);
        } else {
            main_unread.setText(unreadMsgsCount + "");
            main_unread.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 移除消息监听
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    //联系人变化监听
    private void setContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
                System.out.println("好友请求被同意");
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                System.out.println("好友请求被拒绝");
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                System.out.println("收到好友邀请");
                toast("收到好友邀请");
                //直接同意好友邀请
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                System.out.println("被删除好友");
                //更新联系人
                updateContactFragment();
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                System.out.println("增加了联系人");
                //更新联系人界面
                updateContactFragment();
                //通知添加界面更新
                EventBus.getDefault().post("添加成功");
            }
        });
    }

    //更新联系人界面数据
    private void updateContactFragment() {
        //判断联系人界面有没有添加
        ContactFragment contactFragment = (ContactFragment) fragments.get(1);
        if (contactFragment.isAdded()) {
            //更新联系人界面数据
            contactFragment.loadContactsFromServer();
        }
    }

    //初始化要显示的fragment
    private void initFragment() {
        fragments.clear();
        fragments.add(new ConversationFragment());
        fragments.add(new ContactFragment());
        fragments.add(new PluginFragment());


        //需要把其他的隐藏
        if (fragments.get(1).isAdded()) {
            getSupportFragmentManager().beginTransaction().hide(fragments.get(1)).commit();
        }
        if (fragments.get(2).isAdded()) {
            getSupportFragmentManager().beginTransaction().hide(fragments.get(2)).commit();
        }
        //判断第一个fragment是否已经添加
        if (fragments.get(0).isAdded()) {
            //显示第一个fragment  会话界面
            getSupportFragmentManager().beginTransaction().show(fragments.get(0)).commit();
            tab_conversation.setSelected(true);
        } else {
            //显示第一个fragment  会话界面
            getSupportFragmentManager().beginTransaction().add(R.id.main_container, fragments.get(0), "0").commit();
            tab_conversation.setSelected(true);
        }
    }

    @Override
    protected void initListener() {
        tab_conversation.setOnClickListener(this);
        tab_contact.setOnClickListener(this);
        tab_plugin.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        tab_conversation = (ImageView) findViewById(R.id.tab_conversation);
        tab_contact = (ImageView) findViewById(R.id.tab_contact);
        tab_plugin = (ImageView) findViewById(R.id.tab_plugin);
        main_unread = (TextView) findViewById(R.id.main_unread);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processClick(View v) {
        int currentIndex = 0;
        //切换按钮选中状态
        tab_conversation.setSelected(false);
        tab_contact.setSelected(false);
        tab_plugin.setSelected(false);
        switch (v.getId()) {
            case R.id.tab_conversation:
                currentIndex = 0;
                tab_conversation.setSelected(true);
                break;
            case R.id.tab_contact:
                tab_contact.setSelected(true);
                currentIndex = 1;
                break;
            case R.id.tab_plugin:
                tab_plugin.setSelected(true);
                currentIndex = 2;
                break;
            default:
                break;
        }
        //判断是否已经显示了当前界面
        if (currentIndex == originalIndex) {
            return;
        }
        //判断当前的fragment是否已经添加进来了
        //切换fragment
        if (fragments.get(currentIndex).isAdded()) {
            //已经添加进来
            getSupportFragmentManager().beginTransaction().hide(fragments.get(originalIndex)).show(fragments.get(currentIndex)).commit();
        } else {
            //还没有添加
            getSupportFragmentManager().beginTransaction().hide(fragments.get(originalIndex)).add(R.id.main_container, fragments.get(currentIndex)).commit();
        }
        //标记原来的independencex
        originalIndex = currentIndex;

    }
}
