package com.test.MyQQ.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import com.test.MyQQ.adapter.ChatAdapter;
import com.test.MyQQ.listener.MymessageListener;
import itheima.com.qqDemo.R;

/**
 * Created by ThinkPad on 2016/8/14.
 */
public class ChatActivity extends BaseActivity implements TextWatcher, TextView.OnEditorActionListener {

    private String chat_to;
    private Button chat_send;
    private EditText chat_edit;
    private RecyclerView chat_recycleView;
    private ImageView add;
    private TextView my_title;
    private ArrayList<EMMessage> messages = new ArrayList<EMMessage>();
    private ChatAdapter adapter;
    private MymessageListener listener;

    @Override
    protected void initData() {
        //获取当前要聊天的对象
        chat_to = getIntent().getStringExtra("chat_to");
        my_title.setText("正在与" + chat_to + "聊天");
        //加载聊天消息
        loadMessages();
        initMessageReceiveListener();
    }
    //初始化消息接收监听
    private void initMessageReceiveListener() {
        //适配器设计模式
        //收到新消息
        listener = new MymessageListener(){
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //收到新消息
                toast("接收到新消息");
                //更新消息列表
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadMessages();
                    }
                });
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除接收消息监听
        EMClient.getInstance().chatManager().removeMessageListener(listener);
    }

    //加载聊天记录
    private void loadMessages() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(chat_to);
//获取此会话的所有消息
        if (conversation == null) return;
        //标注所有消息为已读
        conversation.markAllMessagesAsRead();
        List<EMMessage> loadMessages = conversation.getAllMessages();
        messages.clear();
        messages.addAll(loadMessages);
        //进行更新
        adapter.notifyDataSetChanged();
        //移动到最后一条
        chat_recycleView.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void initListener() {
        chat_edit.addTextChangedListener(this);
        chat_edit.setOnEditorActionListener(this);
        chat_send.setOnClickListener(this);
        adapter = new ChatAdapter(messages);
        chat_recycleView.setAdapter(adapter);

        chat_recycleView.post(new Runnable() {
            @Override
            public void run() {
                int height = chat_recycleView.getHeight();
                System.out.println("变化前的高度：" + height);
            }
        });
        //添加输入框焦点变化监听
        chat_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //获取变化后的列表高度
                chat_recycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //移除监听
                        chat_recycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        System.out.println("列表变化后的高度：" + chat_recycleView.getHeight());
                        //列表滑动到最后一条
                        chat_recycleView.scrollToPosition(messages.size() - 1);
                    }
                });
            }
        });
        //设置输入框点击事件
        chat_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("执行了点击事件");
                chat_recycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        chat_recycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        chat_recycleView.scrollToPosition(messages.size() - 1);
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        my_title = (TextView) findViewById(R.id.my_title);
        add = (ImageView) findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);
        chat_recycleView = (RecyclerView) findViewById(R.id.chat_recycleView);
        //设置成列表展示
        chat_recycleView.setLayoutManager(new LinearLayoutManager(this));
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_send);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void processClick(View v) {
        if (v.getId() == R.id.chat_send) {
            sendMsg();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //当前是否内容为空
        if (TextUtils.isEmpty(s.toString().trim())) {
            chat_send.setEnabled(false);
        } else {
            chat_send.setEnabled(true);
        }
    }

    //输入法点击监听
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.chat_edit && actionId == EditorInfo.IME_ACTION_SEND) {
            String msg = chat_edit.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                toast("不能发送空消息");
            } else {
                sendMsg();
            }
        }
        return true;
    }

    //发送消息
    private void sendMsg() {
        toast("执行发送消息操作");
        String msg = chat_edit.getText().toString().trim();

        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(msg, chat_to);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                toast("发送成功");
                //更新界面
                notifiDateSetChange();
            }

            @Override
            public void onError(int i, String s) {
                toast("发送失败");
                //更新界面
                notifiDateSetChange();
            }

            @Override
            public void onProgress(int i, String s) {
                toast("正在发送");
                //更新界面
                notifiDateSetChange();
            }
        });
        //添加到消息集合中
        messages.add(message);

        adapter.notifyDataSetChanged();
        //滚动到最后一条消息
        chat_recycleView.scrollToPosition(messages.size() - 1);
        //清空输入框
        chat_edit.getText().clear();
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    protected void notifiDateSetChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

}
