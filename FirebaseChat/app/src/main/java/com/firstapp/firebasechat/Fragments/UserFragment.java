package com.firstapp.firebasechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firstapp.firebasechat.Adapter.UserAdapter;
import com.firstapp.firebasechat.Model.Chat;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserFragment extends Fragment {

        private RecyclerView recyclerView;
        private UserAdapter userAdapter;
        private List<Users> mUsers; //List of total existing users in Firebase
        private List<String> chatRoomID;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        //chatRoomID = new ArrayList<>();

        ReadUsers();

        Button mCreate = view.findViewById(R.id.createChatRoom);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChatRoom();
                //new ClassRoomsFragment();
            }
        });

        return view;

    }

    private void createChatRoom() {

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").push().getKey(); //Generates the chatroom ID as a key, then assigns the chatroom ID to each participating user
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("MyUsers"); //Declares MyUsers database reference

        DatabaseReference chatRoomInfoDb = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(key);//Creates a folder "Chat Rooms", saves the Chat Room ID.

        HashMap newChatRoom = new HashMap<>();
        newChatRoom.put("users/" + FirebaseAuth.getInstance().getUid(), true);//Puts all user IDs inside chatroom folder

        Boolean validChat = false;

        System.out.println("I am in chat room");

        //Loops through selected users then puts them inside a chat room
        for(Users oneUser: mUsers){
            if(oneUser.getSelected()){
                validChat = true;
                newChatRoom.put("users/" + oneUser.getId(), true);
                userDb.child(oneUser.getId()).child("ChatRooms").child(key).setValue(true);//Gives the chatroom ID to the selected users inside chatroom
            }
        }

        //Only creates a chat room if at least one user is in it
        if (validChat) {
            //Updates children
            chatRoomInfoDb.updateChildren(newChatRoom);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("ChatRooms").child(key).setValue(true);//Chatroom owner
        }
    }


    private void ReadUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users user = snapshot.getValue(Users.class);

                    assert user != null;
                    if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}