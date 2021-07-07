package com.firstapp.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.TextView;

import com.firstapp.firebasechat.Adapter.MessageAdapter;
import com.firstapp.firebasechat.Adapter.RoomInfoAdapter;
import com.firstapp.firebasechat.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassRoomInfo extends AppCompatActivity {

    TextView cRoomID;

    Intent intent;

    private DatabaseReference reference;
    private List<String> usernames;

    RoomInfoAdapter roomInfoAdapter;

    RecyclerView recyclerView;

    //my nub


    HashMap<String, String> id;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classroom_information);

        cRoomID = findViewById(R.id.classRoomTextID);
        recyclerView = findViewById(R.id.classroomUsersRecycler);

        intent = getIntent();

        //id = intent.getStringExtra("classroom id");
        //id = (HashMap<String, String>) intent.getSerializableExtra("classroomName");
        //id = intent.getSerializableExtra("classroom id");

        String roomID = intent.getStringExtra("counter");

        //System.out.println("id: " + roomID);

        for(int i = 0; i < Integer.parseInt(intent.getStringExtra("counter")); i++){
            System.out.println("id: " + intent.getStringExtra("classroom id" + i));
            getUsernames(intent.getStringExtra("classroom id" + i));
        }



        //cRoomID.setText("Classroom ID: " + id.get(6969));

    }



    public void getUsernames(String username){
        reference = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(username);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernames = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                   if(snapshot.getKey().toString().equals("username")){
                       System.out.println(snapshot.getValue().toString());
                       //cRoomID.setText(snapshot.getValue().toString());
                       //usernames.add(snapshot.getValue().toString());
                       roomInfoAdapter = new RoomInfoAdapter(ClassRoomInfo.this, snapshot.getValue().toString());
                       //recyclerView = findViewById(R.id.classroomUsersRecycler);
                       //recyclerView.setHasFixedSize(true);
                       //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                       //recyclerView.setAdapter(roomInfoAdapter);


                       break;
                   }

                }

                //intent.putExtra("classroom id", information1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
