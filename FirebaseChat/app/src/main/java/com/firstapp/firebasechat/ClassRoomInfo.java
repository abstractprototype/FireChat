package com.firstapp.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ClassRoomInfo extends AppCompatActivity {

    TextView cRoomID;

    Intent intent;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classroom_information);

        cRoomID = findViewById(R.id.classRoomTextID);

        intent = getIntent();

        String roomID = intent.getStringExtra("classroom id");

        cRoomID.setText("Classroom ID: " + roomID);

    }


}
