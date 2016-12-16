package com.test.MyQQ.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;

import itheima.com.qqDemo.R;


/**
 * 会话
 *
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private ArrayList<EMConversation> conversations;
    private ConversationItemClickListener conversationItemClickListener;

    public interface ConversationItemClickListener{
        void onItemClick(String username);
    }
    public void setConversationItemClickListener(ConversationItemClickListener conversationItemClickListener){
        this.conversationItemClickListener = conversationItemClickListener;
    }
    public ConversationAdapter(ArrayList<EMConversation> conversations){
        this.conversations = conversations;
    }
    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item,parent,false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {

        //聊天对象 EMConversation
        EMConversation conversation = conversations.get(position);
        final String userName = conversation.getUserName();
        holder.conversation_name.setText(userName);

        //最后一条消息 conversation.getLastMessage
        EMMessage lastMessage = conversation.getLastMessage();

        //判断最后一条消息是否为文本消息
        if(lastMessage.getType()== EMMessage.Type.TXT){
            EMTextMessageBody body = (EMTextMessageBody) lastMessage.getBody();
            holder.conversation_last_msg.setText(body.getMessage());
        }

        //最后一条消息时间
        holder.conversation_time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

        //未读消息个数
        int unreadMsgCount = conversation.getUnreadMsgCount();
        System.out.println("当前用胡："+userName+"未读消息个数:"+unreadMsgCount);

        // 有未读消息才显示 未读消息数量的提示，一个 TextView 的图片
        if(unreadMsgCount==0){
            holder.conversation_unread.setVisibility(View.INVISIBLE);
        }else {
            holder.conversation_unread.setText(unreadMsgCount+"");
            holder.conversation_unread.setVisibility(View.VISIBLE);
        }

        //当前条目设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(conversationItemClickListener!=null) {
                    conversationItemClickListener.onItemClick(userName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        private final TextView conversation_name;
        private final TextView conversation_last_msg;
        private final TextView conversation_time;
        private final TextView conversation_unread;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            conversation_name = (TextView) itemView.findViewById(R.id.conversation_name);
            conversation_last_msg = (TextView) itemView.findViewById(R.id.conversation_last_msg);
            conversation_time = (TextView) itemView.findViewById(R.id.conversation_time);
            conversation_unread = (TextView) itemView.findViewById(R.id.conversation_unread);
        }
    }
}
