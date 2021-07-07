package com.firstapp.firebasechat.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firstapp.firebasechat.Adapter.UserAdapter;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserFragment extends Fragment {

        private RecyclerView recyclerView;
        private UserAdapter userAdapter;
        private List<Users> mUsers; //List of total existing users in Firebase
        private List<String> chatRoomID;
        private Context context;

        FirebaseAuth auth;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.ducks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        ReadUsers();

        //Edit text widgets for room creation
        EditText roomNameET = view.findViewById(R.id.editChatRoomName);
        EditText roomID = view.findViewById(R.id.editChatRoomName);
        EditText roomPassET = view.findViewById(R.id.editChatRoomPassword);


        Button mCreate = view.findViewById(R.id.createChatRoom);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rName = roomNameET.getText().toString();
                String rPassword = roomPassET.getText().toString();

                createChatRoom(rName, rPassword);

            }
        });

        Button mJoin = view.findViewById(R.id.joinChatRoom);
        mJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rID = roomID.getText().toString();
                //String rPassword = roomPassET.getText().toString();
                System.out.println("clicking join room");

                joinChatRoom(rID);
            }
        });

        return view;

    }


    HashMap newChatRoom = new HashMap<>();

    //User must enter room ID and room password
    private void joinChatRoom(String roomID){

        DatabaseReference classroomID = FirebaseDatabase.getInstance().getReference().child("ChatRooms");

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("MyUsers");
        DatabaseReference chatRoomInfoDb = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(roomID);//Creates a folder "Chat Rooms", saves the Chat Room ID.
//        FirebaseUser firebaseUser = auth.getCurrentUser();
      //  String userid = firebaseUser.getUid();

        newChatRoom.clear();

        //Checking if fields are empty
        if(TextUtils.isEmpty(roomID)){
            //Toast.makeText(Login_Activity.this,"Please fill the Fields", Toast.LENGTH_SHORT).show();

        //Checking if correct room name and password
        }else{

            classroomID.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                        System.out.println("mansplain" + snapshot1);
                        if(roomID.equals(snapshot1.getKey().toString())  ){

                            System.out.println("Getting room ID");

                            newChatRoom.put("users/" + FirebaseAuth.getInstance().getUid(), true);//Puts all user IDs inside chatroom folder
                            //newChatRoom.put("users/" + FirebaseAuth.getInstance().getCurrentUser(), true);

                            chatRoomInfoDb.updateChildren(newChatRoom);

                            newChatRoom.clear();
                            //newChatRoom.put("ChatRooms/" + FirebaseAuth.getInstance().getUid(), true);

                            //userDb.updateChildren(newChatRoom);
                            userDb.child(FirebaseAuth.getInstance().getUid()).child("ChatRooms").child(roomID).setValue(true);
//                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void createChatRoom(String roomName, String roomPassword) {

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").push().getKey(); //Generates the chatroom ID as a key, then assigns the chatroom ID to each participating user
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("MyUsers"); //Declares MyUsers database reference

        DatabaseReference chatRoomInfoDb = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(key);//Creates a folder "Chat Rooms", saves the Chat Room ID.


        newChatRoom.put("users/" + FirebaseAuth.getInstance().getUid(), true);//Puts all user IDs inside chatroom folder
        newChatRoom.put("Room Name/" + roomName, true);
        newChatRoom.put("Room Password/" + roomPassword, true);

        Boolean validChat = false;

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

    //Displays all the users on Firebase, we are removing this before launching app.
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