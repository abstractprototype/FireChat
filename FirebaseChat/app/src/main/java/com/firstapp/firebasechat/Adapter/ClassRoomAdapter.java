package com.firstapp.firebasechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.firebasechat.ClassRoomMessageActivity;
import com.firstapp.firebasechat.Model.Classrooms;
import com.firstapp.firebasechat.R;

import java.util.ArrayList;
import java.util.HashMap;


//Gets all the user information from firebase
//This adapter connects classroom_item.xml to ClassRoomFragment
public class ClassRoomAdapter extends RecyclerView.Adapter<ClassRoomAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> cmRooms;
    private ArrayList<Classrooms> mRooms;
    private boolean isChat;


    //Constructor
    /*public ClassRoomAdapter(Context context, ArrayList<String> mRooms, boolean isChat){
        this.context = context;
        this.mRooms = mRooms;
        this.isChat = isChat;
    }*/

    public ClassRoomAdapter(Context context, ArrayList<Classrooms> mRooms, boolean isChat){
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

        holder.classroomName.setText(mRooms.get(position).getRoomName());

//        //For profile picture of classroom
//        if(classroom.getImageURL().equals("default")){
//            holder.classroomImage.setImageResource(R.mipmap.ic_launcher);
//        }else{
//            // Adding Glide Library
//            Glide.with(context)
//                    .load(classrooms.getImageURL())
//                    .into(holder.classroomImage);
//        }

        holder.classroomName.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                System.out.println("classroom: " + holder.classroomName.getText());
                Intent i = new Intent(context, ClassRoomMessageActivity.class);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("roomName", mRooms.get(position).getRoomName());
                hashMap.put("id", mRooms.get(position).getId());

                i.putExtra("classroomName", hashMap);
                context.startActivity(i);

            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ClassRoomMessageActivity.class);
                //i.putExtra("classroomid", classrooms.getId());
                i.putStringArrayListExtra("classroomid", (ArrayList<String>) mRooms);
                //i.putExtra("classroomid", mRooms.get(holder.getAdapterPosition()));
                context.startActivity(i);

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView classroomImage;
        public TextView classroomName;

        /*public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public ImageView show_image;
        public ImageView another_image;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            classroomImage = itemView.findViewById(R.id.posterProfilePic);
            classroomName = itemView.findViewById(R.id.posterName);

            /*show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen_status);
            show_image = itemView.findViewById(R.id.image_id);
            another_image = itemView.findViewById(R.id.media);*/

        }
    }


}
