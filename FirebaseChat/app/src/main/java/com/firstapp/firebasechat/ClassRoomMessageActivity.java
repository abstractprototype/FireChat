package com.firstapp.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.Adapter.ClassRoomAdapter;
import com.firstapp.firebasechat.Adapter.MessageAdapter;
import com.firstapp.firebasechat.Model.Chat;
import com.firstapp.firebasechat.Model.Classrooms;
import com.firstapp.firebasechat.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//This class connects to activity_message.xml only used for direct messaging
public class ClassRoomMessageActivity extends AppCompatActivity {

    TextView classRoomTitle;
    ImageView classRoomPic;

    EditText classRoomEditText;
    ImageButton classRoomSendBtn;
    ImageButton classRoomImageBtn;

    FirebaseUser fuser;
    DatabaseReference classRoomReference;
    private Intent intent;

    //Loading Bar
    private ProgressDialog loadingBar;

    //Upload Image
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    String userid;
    private String classroomid;
    private static String cRoomID;
    private HashMap<String, String> map;

    public static List<String> userNames;
    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_message);
        userNames = new ArrayList<>();;
        intent = getIntent();

        map = (HashMap<String, String>) intent.getSerializableExtra("classroomName");
        //classroomid = intent.getStringExtra("classroomName");
        //classroomid = intent.getStringArrayExtra("classroomName");
        /*for (Parcelable e : intent.getParcelableArrayExtra("classroomName")) {
            //classroomid.get(e.getClass());
            classroomid.add(e.toString());
        }*/
        //Widgets
        classRoomPic = findViewById(R.id.chatroom_pic);
        classRoomTitle = findViewById(R.id.chatroom_title);
        classRoomSendBtn = findViewById(R.id.btn_send69);
        classRoomImageBtn = findViewById(R.id.imgBtn_send69);
        classRoomEditText = findViewById(R.id.editTextMessage_send69);

        //RecyclerView
        recyclerView = findViewById(R.id.chatroom_recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //userid = intent.getStringExtra("userid");
       // classroomid = intent.getStringArrayListExtra("classroomid");

        //Initialize Loading Bar
        loadingBar = new ProgressDialog(this);

        //Profile Image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("images");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        classRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(map.get("id"));

        ArrayList<String> classrooms = new ArrayList<>();
        classRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //String classrooms = dataSnapshot.getKey().toString();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    System.out.println("snap: " + snapshot1);
                }
                classRoomTitle.setText(map.get("roomName")); //Displays classroom title
                System.out.println(dataSnapshot);



                //Displays classroom image
                   /* if (classrooms.getClassImageURL() != null && classrooms.getClassImageURL().equals("default")) {
                        classRoomPic.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(ClassRoomMessageActivity.this)
                                .load(classrooms.getClassImageURL())
                                .into(classRoomPic);
                    }*/

                readMessages(fuser.getUid(),map.get("id"));
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        //Sending message button listener
        classRoomSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = classRoomEditText.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), map.get("id"), msg);
                }else{
                    Toast.makeText(ClassRoomMessageActivity.this, "Please send a non empty message", Toast.LENGTH_SHORT).show();
                }
                classRoomEditText.setText("");
            }
        });

        classRoomImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        classRoomTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                classRoomUserNames();

            }
        });

    }

    DatabaseReference reference;
    HashMap<String, String> information1 = new HashMap<>();
    private void classRoomUserNames(){

        reference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(map.get("id")).child("users");

        HashMap<Integer, String> information = new HashMap<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                Intent intent = new Intent(ClassRoomMessageActivity.this, ClassRoomInfo.class);

                int counter = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String chat = snapshot.getKey().toString();
                    System.out.println("ggg: " + chat);
                    intent.putExtra("counter", counter + "");
                    intent.putExtra("classroom id" + counter++, chat);


                }



                //intent.putExtra("classroom id", information1);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        System.out.println("size: " + userNames.size());

        //for(int i = 0; i < userNames.size(); i++) {
           // reference = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(userNames.get(0));


        /*reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users chat = snapshot.getValue(Users.class);

                    System.out.println("no time: " + chat.getUsername());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    HashMap<String, Object> messageMap = new HashMap<>();
    private void UploadMyImage(){

        final ProgressDialog progressDialog = new ProgressDialog(ClassRoomMessageActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(map.get("id")).child("Messages");

        if(imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        classRoomReference = FirebaseDatabase.getInstance().getReference();

                        Calendar c = Calendar.getInstance();

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(c.getTime());

                        //HashMap<String, Object> map = new HashMap<>();
                        messageMap.put("sender", fuser.getUid());
                        //messageMap.put("classroomID", map.get("id")); //The chatroom id will receive this message
                        messageMap.put("message", mUri);
                        messageMap.put("messageType", "image");
                        messageMap.put("Date and Time", formattedDate);
                        //classRoomReference.updateChildren(messageMap);
                        //classRoomReference.child("ChatRooms").child("Messages").push().setValue(messageMap);
                        reference.push().setValue(messageMap);
                        //sendMessage(fuser.getUid(), classroomid, mUri);

                        messageAdapter = new MessageAdapter(ClassRoomMessageActivity.this, mChat, imageUri);

                        progressDialog.dismiss();
                    }
                    else{
                        Toast.makeText(ClassRoomMessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ClassRoomMessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else{
            Toast.makeText(ClassRoomMessageActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(ClassRoomMessageActivity.this, "Upload in progress...", Toast.LENGTH_SHORT).show();
            }else{
                UploadMyImage();

            }
        }
    }

    //Opens user's gallery
    private void SelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), IMAGE_REQUEST);
    }





    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();

    private void sendMessage(String sender, String receiver, String message){

        //String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(classroomid);//Gets the key of the chatroom from Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(map.get("id")).child("Messages");


        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        //Adds message info to database
//        HashMap<String, Object> hashMap = new HashMap<>();
        messageMap.put("sender", sender);
        messageMap.put("classroomID", map.get("id")); //The chatroom id will receive this message
        messageMap.put("message", message);
        messageMap.put("messageType", "message");
        messageMap.put("Date and Time", formattedDate);
        //messageMap.put("Messages/" + , true);
        reference.push().setValue(messageMap);
        //reference.child(key).child("Messages").push().setValue(messageMap); //saves each message into ChatRooms/ Proper chat room/ Messages(not working)



    }

    private void readMessages(String myid, String classroomid){
        mChat = new ArrayList<>();

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").getKey();//Gets the key of the chatroom from Firebase
        classRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(classroomid).child("Messages"); //Retrieves all messages from ChatRooms/Chatroom ID/ Messages

        classRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                System.out.println("I am here my friend");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    System.out.println("I am database");

                    try {

                            mChat.add(chat);

                        //messageAdapter = new MessageAdapter(ClassRoomMessageActivity.this, mChat, imageURL);
                        messageAdapter = new MessageAdapter(ClassRoomMessageActivity.this, mChat);
                        recyclerView.setAdapter(messageAdapter);
                    }catch(Exception e){

                    }

                    // recyclerView.setAdapter(new ClassRoomAdapter(ClassRoomMessageActivity.this, mChat, imageURL));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    //Checks online or offline status of a user
//    private void CheckStatus(String status){
//
//        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//
//        reference.updateChildren(hashMap);
//
//    }
//
//    //Online status
//    @Override
//    protected void onResume(){
//        super.onResume();
//        CheckStatus("Online");
//    }
//
//    //Offline status
//    @Override
//    protected void onPause(){
//        super.onPause();
//        reference.removeEventListener(seenListener);
//        CheckStatus("Offline");
//    }



}