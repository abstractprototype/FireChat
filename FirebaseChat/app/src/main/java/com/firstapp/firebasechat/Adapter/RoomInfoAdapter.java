package com.firstapp.firebasechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.MessageActivity;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomInfoAdapter extends RecyclerView.Adapter<RoomInfoAdapter.ViewHolder> {

    private Context context;
    private String mUsers;
    private int counter;

    //Constructor
    public RoomInfoAdapter(Context context, String mUsers){
        this.context = context;
        this.mUsers = mUsers;
        this.counter = 0;



    }


    @NonNull
    @Override
    public RoomInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("users: " + mUsers);
        View view = LayoutInflater.from(context).inflate(R.layout.classroom_information,
                parent,
                false);

        return new RoomInfoAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RoomInfoAdapter.ViewHolder holder, int position) {
        //Users users = mUsers.get(position);

        System.out.println("users: " + mUsers);
        holder.username_room.setText(mUsers);
        /*holder.username.setText(users.getUsername());



        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUsers.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });

        if(users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            // Adding Glide Library
            Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView);
        }

        //Status check
        if(isChat){
            if(users.getStatus().equals("Online")){
                holder.imageViewON.setVisibility(View.VISIBLE);
                holder.imageViewOFF.setVisibility(View.GONE);
            }else{
                holder.imageViewON.setVisibility(View.GONE);
                holder.imageViewOFF.setVisibility(View.VISIBLE);
            }
        }else{
            holder.imageViewON.setVisibility(View.GONE);
            holder.imageViewOFF.setVisibility(View.GONE);
        }*/




    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView username_room;
        ImageView image_room;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            System.out.println("users: " + mUsers);
            username_room = itemView.findViewById(R.id.room_username);
            //image_room = itemView.findViewById(R.id.room_classroomImage);

        }
    }
}
