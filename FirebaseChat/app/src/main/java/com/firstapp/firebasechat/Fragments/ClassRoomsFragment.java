package com.firstapp.firebasechat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firstapp.firebasechat.Adapter.ClassRoomAdapter;
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

    private ClassRoomAdapter classRoomAdapter;
    //private List<Users> mUsers; //List of total existing users on Firebase

    private List<Classrooms> classroomsList; //List of recent classrooms with users
    private ArrayList<String> classrooms;
    private List<String> myUsers;

    FirebaseUser fuser;
    DatabaseReference roomReference;

    RecyclerView recyclerView;

    public ClassRoomsFragment() {
        // Required empty public constructor
        classrooms = new ArrayList<>();
        myUsers = new ArrayList<>();

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
        roomReference = FirebaseDatabase.getInstance().getReference("ChatRooms")//Gets the users inside the Chatroom folder
                .child(fuser.getUid());

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").getKey(); //This code is not doing anything?
        //roomReference = FirebaseDatabase.getInstance().getReference().child("ChatRooms");
        roomReference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child("Room Name");//This code also not doing anything?

        classroomsList.clear();
        classroomsList();

        return view;
    }

    private void classroomsList() {

        //Getting all recent Chat Rooms :
        classroomsList = new ArrayList<>();
        roomReference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid())
                .child("ChatRooms");

        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //classroomsList.clear();
                classrooms.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){

                    //classrooms.add(snapshot1.getKey().toString());//gives me room id
                    classrooms.add(snapshot1.getKey().toString());

//                    for(DataSnapshot snapshot2 : snapshot1.getChildren()){
//                        classrooms.add(snapshot2.getKey().toString());
//                    }

                    /*Classrooms cRooms = snapshot1.getValue(Classrooms.class);
                    for(Classrooms classList : classroomsList){
                        //if(cRooms.getId().equals(classList.getId())){
                        //    System.out.println("I am here " + cRooms.getId());
                            classroomsList.add(cRooms);
                        System.out.println("I am here " + cRooms.getId());
                     //   }
                        classroomsList.add(cRooms);
                    }*/

                    myUsers.add(snapshot1.getValue().toString());
                }

                classRoomAdapter = new ClassRoomAdapter(getContext(), classrooms, true);
                recyclerView.setAdapter(classRoomAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}