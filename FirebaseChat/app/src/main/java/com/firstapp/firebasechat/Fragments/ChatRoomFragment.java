package com.firstapp.firebasechat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.firebasechat.Adapter.UserAdapter;
import com.firstapp.firebasechat.Login_Activity;
import com.firstapp.firebasechat.Model.Chatlist;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;
import com.firstapp.firebasechat.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoomFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private List<Chatlist> usersList;

    DatabaseReference reference;
    FirebaseUser fuser;
    FirebaseAuth auth;

    Button createChatRoom;
    RecyclerView chatRoomRecycler;

    public ChatRoomFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        usersList = new ArrayList<>();

        //createChatRoom = view.findViewById(R.id.createChatRoom);
        chatRoomRecycler = view.findViewById(R.id.chatRoomList);

        createChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        return view;
    }

//    private void createChatRoom(){
//
//        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").push().getKey();
//        //Locates the Chat Room database then stores the chat room info there
//        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("MyUsers");
//        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("message");
//
//        HashMap newChatRoom = new HashMap<>();
//        newChatRoom.put("id", key);
//        newChatRoom.put("users/" + FirebaseAuth.getInstance().getUid(), true);
//        //reference.child("ChatRooms").push().setValue(newChatRoom);
//
//        Boolean validChat = false;
////        for(User oneUsers: usersList){
////            if(oneUsers.getSelected()){
////                validChat = true;
////                newChatRoom.put("users/" + mUsers.getUid(), true);
////                userDb.child(mUsers.getUid()).child("Chats").child(key).setValue(true);
////            }
////        }
//
//        if(validChat){
//            //Updates children
//            chatInfoDb.updateChildren(newChatRoom);
//            userDb.child(FirebaseAuth.getInstance().getUid()).child("Chats").child(key).setValue(true);
//        }
//
//
//    }




}
