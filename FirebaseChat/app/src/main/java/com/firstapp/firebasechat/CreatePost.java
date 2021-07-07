package com.firstapp.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CreatePost extends AppCompatActivity {

    ImageView posterProfilePic;
    EditText writePost;
    Button sendPost;

    FirebaseUser fuser;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        posterProfilePic = findViewById(R.id.posterProfilePic);
        writePost = findViewById(R.id.writePost);
        sendPost = findViewById(R.id.sendPost);

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myPost = writePost.getText().toString();
                createPost(myPost);
                writePost.setText("");

            }
        });

        //intent = getIntent();
        //id = intent.getStringExtra("classroom id");
        //id = (HashMap<String, String>) intent.getSerializableExtra("classroomName");
        //id = intent.getSerializableExtra("classroom id");
//        String roomID = intent.getStringExtra("counter");
        //System.out.println("id: " + roomID);
//        for(int i = 0; i < Integer.parseInt(intent.getStringExtra("counter")); i++){
//            System.out.println("id: " + intent.getStringExtra("classroom id" + i));
//            getUsernames(intent.getStringExtra("classroom id" + i));
//        }
        //cRoomID.setText("Classroom ID: " + id.get(6969));

    }

    HashMap postsHashMap = new HashMap<>();
    private void createPost(String myPost){

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("MyUsers");
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        postsHashMap.put("Message", myPost);
        postsHashMap.put("Created By", fuser.getUid());
        postsHashMap.put("Date and Time", formattedDate);
        postsReference.push().setValue(postsHashMap);

    }

}
