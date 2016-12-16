package com.test.MyQQ.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import com.test.MyQQ.bean.Student;
import itheima.com.qqDemo.R;

/**
 * 添加好友
 * 处理 contacts 中包含了想要的加的用户的 Username ，提示已经是好友
 *
 */
public class AddAdapter extends RecyclerView.Adapter<AddAdapter.AddViewHolder> {
    private ArrayList<Student> students;
    private ArrayList<String> contacts;
    private AddListener addListener;
    public AddAdapter(ArrayList<Student> students,ArrayList<String> contacts){
        this.students = students;
        this.contacts = contacts;
    }
    public interface AddListener{
        void onAddListen(String username);
    }
    public void setAddListener(AddListener addListener){
        this.addListener = addListener;
    }
    @Override
    public AddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item,parent,false);
        return new AddViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddViewHolder holder, final int position) {
        //名称
        holder.add_item_name.setText(students.get(position).getUsername());
        //创建时间
        holder.add_item_date.setText(students.get(position).getCreatedAt());
        //添加按钮
        if(contacts.contains(students.get(position).getUsername())){
            holder.add_item_btn.setText("已是好友");
            holder.add_item_btn.setEnabled(false);
        }else {
            holder.add_item_btn.setText("添加");
            holder.add_item_btn.setEnabled(true);
        }
        holder.add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addListener!=null){
                    addListener.onAddListen(students.get(position).getUsername());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class AddViewHolder extends RecyclerView.ViewHolder{

        private final TextView add_item_name;
        private final TextView add_item_date;
        private final Button add_item_btn;

        public AddViewHolder(View itemView) {
            super(itemView);
            add_item_name = (TextView) itemView.findViewById(R.id.add_item_name);
            add_item_date = (TextView) itemView.findViewById(R.id.add_item_date);
            add_item_btn = (Button) itemView.findViewById(R.id.add_item_btn);
        }
    }
}
