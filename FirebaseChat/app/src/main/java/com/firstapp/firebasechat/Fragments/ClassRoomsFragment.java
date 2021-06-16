package com.firstapp.firebasechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firstapp.firebasechat.Adapter.UserAdapter;
import com.firstapp.firebasechat.Model.Classrooms;
import com.firstapp.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//This class WAS for displaying recent chats with users
//I want this class ChatsFragment to turn into a list of group chats
public class ClassRoomsFragment extends Fragment {

    //private UserAdapter userAdapter;
    //private List<Users> mUsers; //List of total existing users on Firebase

    private List<Classrooms> classroomsList; //List of recent classrooms with users

    FirebaseUser fuser;
    DatabaseReference roomReference;

    RecyclerView recyclerView;

    public ClassRoomsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classrooms,
                container,
                false);

        recyclerView = view.findViewById(R.id.classroom_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        classroomsList = new ArrayList<>();//Initialize classrooms array list

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").getKey();
        System.out.println("finding chat room key");
        roomReference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(key);
        System.out.println("saving chat room key");

        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                classroomsList.clear();

                //Gets all the data from Firebase for all Class Rooms:
                for(DataSnapshot snapshot1 : snapshot.getChildren()){

                    Classrooms roomList = snapshot1.getValue(Classrooms.class);
                    classroomsList.add(roomList);//Adds to the view all the classrooms to our array list called classroomsList
                    System.out.println("Getting snapshot of existing chatrooms");
                    System.out.println("Adding existing chatrooms to our array list");
                }

                classroomsList();
                System.out.println("Calling classroomsList function");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void classroomsList() {

        //Getting all recent Chat Rooms :
        classroomsList = new ArrayList<>();
        roomReference = FirebaseDatabase.getInstance().getReference("ChatRooms");
        roomReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();
//
//                for(DataSnapshot snapshot1 : snapshot.getChildren()){
//
//                    Users user = snapshot1.getValue(Users.class);
//                    for(Chatlist chatlist : usersList){
//                        if(user.getId().equals(chatlist.getId())){
//                            mUsers.add(user);
//                        }
//                    }
//                }
//
//                userAdapter = new UserAdapter(getContext(), mUsers, true);
//                recyclerView.setAdapter(userAdapter);
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}