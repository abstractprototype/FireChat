package com.firstapp.firebasechat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firstapp.firebasechat.Adapter.TimelineAdapter;
import com.firstapp.firebasechat.CreatePost;
import com.firstapp.firebasechat.Model.Users;
import com.firstapp.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class TimelineFragment extends Fragment {

    private RecyclerView postsRecycler;
    private Context context;
    private TimelineAdapter timelineAdapter;
    private List<Users> mUsers; //List of total existing users in Firebase
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        postsRecycler = view.findViewById(R.id.ducks);
        postsRecycler.setHasFixedSize(true);
        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Button mPost = view.findViewById(R.id.makePost);
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), CreatePost.class);
                startActivity(intent);

            }
        });

        return view;

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

                    timelineAdapter = new TimelineAdapter(getContext(), mUsers, false);
                    postsRecycler.setAdapter(timelineAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


