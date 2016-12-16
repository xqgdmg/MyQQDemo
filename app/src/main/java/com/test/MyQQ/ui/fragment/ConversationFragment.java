package com.test.MyQQ.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.test.MyQQ.adapter.ConversationAdapter;
import com.test.MyQQ.listener.MymessageListener;
import com.test.MyQQ.ui.activity.ChatActivity;
import itheima.com.qqDemo.R;

/**
 * 会话界面
 * 不监听 新会话 也就是不写 EMClient.getInstance().chatManager().addMessageListener(messageListener);
 * 处理 从环信加载所有会话
 */
public class ConversationFragment extends BaseFragment {

    private RecyclerView chat_recycleView;
    private ArrayList<EMConversation> conversations = new ArrayList<EMConversation>();
    private ConversationAdapter adapter;

    @Override
    protected void initHeader(ImageView back, TextView my_title, ImageView add) {
        back.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        my_title.setText("会话");
    }

    @Override
    protected void initData() {
    }


    /**
     * 从环信加载所有的会话
     * 会话排除空会话
     * 会话进行排序
     */
    public void loadAllConversation() {

        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        conversations.clear();
        conversations.addAll(allConversations.values());
        System.out.println("conversation.size="+conversations.size());

        /**
         * 避免消息出现null，造成空指针
         */
        for (int i = 0; i < conversations.size(); i++) {
            if(conversations.get(i).getLastMessage()==null){
                conversations.remove(i);
            }
        }

        if(conversations.size()>1) {
            //会话排序
            Collections.sort(conversations, new Comparator<EMConversation>() {
                @Override
                public int compare(EMConversation lhs, EMConversation rhs) {// 集合里面的数据 泛型
                    EMMessage aMessage = lhs.getLastMessage();
                    EMMessage bMessage = rhs.getLastMessage();

                    System.out.println("AMESSAGE="+aMessage+"bmessage:"+bMessage);
                    return (int) (bMessage.getMsgTime() - aMessage.getMsgTime());
                }
            });
        }
        //刷新界面
        adapter.notifyDataSetChanged();
    }

    /**
     * onResume
     * 从环信加载所有会话
     */
    @Override
    public void onResume() {
        super.onResume();
        //加载会话
        loadAllConversation();
    }

    /**
     * 条目点击 进入聊天的页面
     */
    @Override
    protected void initListener() {
        adapter = new ConversationAdapter(conversations);
        //添加条目点击监听
        adapter.setConversationItemClickListener(new ConversationAdapter.ConversationItemClickListener() {
            @Override
            public void onItemClick(String username) {
                //跳转到聊天界面
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("chat_to",username);
                startActivity(intent);
            }
        });
        chat_recycleView.setAdapter(adapter);
    }

    @Override
    protected void initView() {

        chat_recycleView = (RecyclerView) findViewById(R.id.conversation_recycleView);
        chat_recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    public void onClick(View v) {

    }
}
