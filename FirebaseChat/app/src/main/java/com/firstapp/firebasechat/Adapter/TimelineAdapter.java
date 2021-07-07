package com.firstapp.firebasechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.MessageActivity;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder>{
    private Context context;
    private List<Users> mUsers;
    private boolean isChat;

    //Constructor
    public TimelineAdapter(Context context, List<Users> mUsers, boolean isChat){
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timeline_item,
                parent,
                false);

        return new TimelineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.ViewHolder holder, int position) {
        Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());

        if(users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            // Adding Glide Library
            Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView imageView;
        public EditText postBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.posterName);
            imageView = itemView.findViewById(R.id.posterProfilePic);
            postBody = itemView.findViewById(R.id.postBody);


        }
    }
}
