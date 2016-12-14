package com.test.MyQQ.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;

import itheima.com.qqDemo.R;


/**
 * Created by ThinkPad on 2016/8/14.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<EMMessage> messages;
    public ChatAdapter(ArrayList<EMMessage> messages){
        this.messages = messages;
    }
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==0){
            //发送的消息布局
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_send,parent,false);
        }else {
            //接收的消息布局
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_receive,parent,false);
        }
        return new ChatViewHolder(view);
    }
    //发送消息  0  接收receive  1
    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).direct() == EMMessage.Direct.SEND){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        EMMessage message = messages.get(position);
        //处理消息时间
        holder.chat_item_time.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
        if(position==0||!DateUtils.isCloseEnough(messages.get(position - 1).getMsgTime(), messages.get(position).getMsgTime())){
            holder.chat_item_time.setVisibility(View.VISIBLE);
        }else {
            holder.chat_item_time.setVisibility(View.GONE);
        }
        //处理消息显示
        //判断是否为文本消息
        if(message.getType()== EMMessage.Type.TXT){
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            holder.chat_item_msg.setText(body.getMessage());
        }
        //处理消息发送状态
        if(holder.chat_item_status==null) return;
        switch (message.status()){
            case INPROGRESS:
                //设置发送动画
                holder.chat_item_status.setImageResource(R.drawable.chat_sending_anim);
                //开启动画
                AnimationDrawable drawable = (AnimationDrawable) holder.chat_item_status.getDrawable();
                drawable.start();
                holder.chat_item_status.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                holder.chat_item_status.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.chat_item_status.setVisibility(View.VISIBLE);
                holder.chat_item_status.setImageResource(R.drawable.msg_error);
                break;
            case CREATE:
                holder.chat_item_status.setImageResource(R.drawable.chat_sending_anim);
                //开启动画
                AnimationDrawable drawable1 = (AnimationDrawable) holder.chat_item_status.getDrawable();
                drawable1.start();
                holder.chat_item_status.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        private final TextView chat_item_time;
        private final TextView chat_item_msg;
        private final ImageView chat_item_status;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chat_item_time = (TextView) itemView.findViewById(R.id.chat_item_time);
            chat_item_msg = (TextView) itemView.findViewById(R.id.chat_item_msg);
            chat_item_status = (ImageView) itemView.findViewById(R.id.chat_item_status);

        }
    }
}
