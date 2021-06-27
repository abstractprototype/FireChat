package com.firstapp.firebasechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.MessageActivity;
import com.firstapp.firebasechat.Model.Chat;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import java.net.URI;

//This adapter connects chat_item_left and chat_item_right to MessageActivity
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Chat> mChat;
    private String imgURL;
    private Uri image;

    private String messageType = " ";

    //Firebase
    FirebaseUser fuser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(){
        this.messageType = "";
    }

    //Constructor
    public MessageAdapter(Context context, List<Chat> mChat, String imgURL){ //For text messages
        this.context = context;
        this.mChat = mChat;
        this.imgURL = imgURL;
        this.messageType = "text";
    }

    public MessageAdapter(Context context, List<Chat> mChat, Uri image){ //Sending Image only
        this.mChat = mChat;
        this.context = context;
        this.image = image;
        this.messageType = "image";
    }

    public MessageAdapter(Context context, List<Chat> mChat){ //Sending Image only
        this.mChat = mChat;
        this.context = context;
        this.image = image;
        this.messageType = "text";
        this.imgURL ="default";
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        //Displays the chat_item_right layout on right hand side
        if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,
                    parent,
                    false);
            return new MessageAdapter.ViewHolder(view);

        }else {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,
                        parent,
                        false);
                return new MessageAdapter.ViewHolder(view);
            }
    }

    private StorageReference ImagesRef;
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

       // System.out.println("I am here");
        Chat chat = mChat.get(position);


        System.out.println("message type: " + messageType);
       // System.out.println("")
      /* if(chat.getMessage().charAt(0) == 'h' && chat.getMessage().charAt(1) == 't'
                && chat.getMessage().charAt(2) == 't' && chat.getMessage().charAt(3) == 'p'
                && chat.getMessage().charAt(4) == 's'){
            System.out.println("I image");
            messageType ="image";
            holder.show_message.setVisibility(View.GONE);
            holder.show_image.setVisibility(View.VISIBLE);
       }else {
            messageType = "text";
            System.out.println("I text");
            holder.show_image.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
       }*/

        if(chat.getMessageType().equals("image")){
            System.out.println("I image");
            messageType ="image";
            holder.show_message.setVisibility(View.GONE);
            holder.show_image.setVisibility(View.VISIBLE);

        }else {
            messageType = "text";
            System.out.println("I text");
            holder.show_image.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
        }

        //System.out.println("I am here");

        //if(chat.getMessage().equals(" ")){
        //    holder.show_image.setImageURI(Uri.parse(chat.getMessage()));
      // }else
           //
      //  holder.show_image.setImageURI(Uri.parse(chat.getMessage()));
            //holder.profile_image.setImageURI(Uri.parse(chat.getMessage()));
        //ImagesRef = FirebaseStorage.getInstance().getReference().child("images");

       //final StorageReference filePath = ImagesRef.child(image.getLastPathSegment() + ".jpg");
       // Task<Uri> image = filePath.getDownloadUrl();
       // filePath.getFile(Uri.parse(chat.getMessage()));


        try{
            if(messageType.equals("image"))
            Picasso.with(this.context).load(Uri.parse(chat.getMessage())).into(holder.show_image);
            else if(messageType.equals("text"))
                holder.show_message.setText(chat.getMessage());
            //Picasso.with(this.context).load(Uri.parse(chat.getMessage())).into(holder.another_image);
//            if(messageType.equals("text")){
//                holder.show_message.setText(chat.getMessage());
//
//            }else if(messageType.equals("image")){
//                Picasso.with(this.context).load(Uri.parse(chat.getMessage())).into(holder.show_image);
//            }

        }catch(Exception e){

        }

        // Reference to an image file in Cloud Storage
        StorageReference storageReference  = FirebaseStorage.getInstance().getReference().child("yourImageReferencePath");

        //holder.show_image.setImageURI(image.getResult());
        //holder.show_message.setText(chat.getMessage());
        /*if(!chat.getMessage().equals("x")){
             try{


            }catch(Exception e){

            }
        }*/


        if(imgURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(imgURL).into(holder.profile_image);
        }

        //Displays seen or delivered after message is read by user or sent
        if(position == mChat.size() - 1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            }
            else
                {
                holder.txt_seen.setText("Delivered");
                }
        }
        else
            {
                holder.txt_seen.setVisibility(View.GONE);
            }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public ImageView show_image;
        public ImageView another_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen_status);
            show_image = itemView.findViewById(R.id.image_id);
            another_image = itemView.findViewById(R.id.media);
            //show_image = itemView.findViewById(R.id.show_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //if its real sender of message that matches the senders proper id, return message on right side
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}

