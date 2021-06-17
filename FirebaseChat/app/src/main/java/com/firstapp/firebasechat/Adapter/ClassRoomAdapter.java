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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.MessageActivity;
import com.firstapp.firebasechat.Model.Classrooms;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;

import java.util.ArrayList;
import java.util.List;


//Gets all the user information from firebase
//This adapter connects classroom_item.xml to ClassRoomFragment
public class ClassRoomAdapter extends RecyclerView.Adapter<ClassRoomAdapter.ViewHolder> {

    private Context context;
    private List<String> mRooms;
    private boolean isChat;

    //Constructor
    public ClassRoomAdapter(Context context, List<String> mRooms, boolean isChat){
        this.context = context;
        this.mRooms = mRooms;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.classroom_item,
                parent,
                false);

        return new ClassRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassRoomAdapter.ViewHolder holder, int position) {

        holder.classroomName.setText(mRooms.get(position));



        //For profile picture of classroom
       /* if(classrooms.getImageURL().equals("default")){
            holder.classroomImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            // Adding Glide Library
            Glide.with(context)
                    .load(classrooms.getImageURL())
                    .into(holder.classroomImage);
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
               // i.putExtra("classroomid", classrooms.getId());
                context.startActivity(i);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView classroomImage;
        public TextView classroomName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            classroomImage = itemView.findViewById(R.id.classroomImage);
            classroomName = itemView.findViewById(R.id.classroomName);

        }
    }


}
