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
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;

import java.util.List;


//Gets all the user information from firebase
//This adapter connects user_item.xml to UserFragment and ChatsFragment
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> mUsers;
    private boolean isChat;

    //Constructor
    public UserAdapter(Context context, List<Users> mUsers, boolean isChat){
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,
                parent,
                 false);
//        View view2 = LayoutInflater.from(context).inflate(R.layout.user_itemnocheckbox,
//                parent,
//                false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());

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
        CheckBox mAdd;
        public ImageView imageViewON;
        public ImageView imageViewOFF;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.classroomName);
            imageView = itemView.findViewById(R.id.classroomImage);
            mAdd = itemView.findViewById(R.id.addUser);
            imageViewON = itemView.findViewById(R.id.statusimageON);
            imageViewOFF = itemView.findViewById(R.id.statusimageOFF);



        }
    }


}
