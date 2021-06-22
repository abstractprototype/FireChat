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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    Intent intent;

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
    ArrayList<String> classroomid;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_message);

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

        intent = getIntent();
        //userid = intent.getStringExtra("userid");
        classroomid = intent.getStringArrayListExtra("classroomid");

        //Initialize Loading Bar
        loadingBar = new ProgressDialog(this);

        //Profile Image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("images");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        classRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms");

        classRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Classrooms classrooms = dataSnapshot.getValue(Classrooms.class);
                classRoomTitle.setText(classrooms.getId());


                    if (classrooms.getClassImageURL() != null && classrooms.getClassImageURL().equals("default")) {
                        classRoomPic.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(ClassRoomMessageActivity.this)
                                .load(classrooms.getClassImageURL())
                                .into(classRoomPic);
                    }


                readMessages(fuser.getUid(),classroomid, classrooms.getClassImageURL());
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
                    sendMessage(fuser.getUid(), classroomid.toString(), msg);
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

        SeenMessage(userid);

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
                        messageMap.put("receiver", userid);
                        messageMap.put("message", mUri);
                        messageMap.put("isseen",false);
                        messageMap.put("messageType", "image");
                        messageMap.put("Date and Time", formattedDate);
                        //reference.updateChildren(messageMap);
                        classRoomReference.child("ChatRooms").child("Messages").push().setValue(messageMap);
                        //sendMessage(fuser.getUid(), userid, mUri);

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



    //Changes message to seen when receiver sees the sender"s message
    private void SeenMessage(String userid){

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").getKey();//Gets the key of the chatroom from Firebase
        classRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(key).child("Messages");

        seenListener = classRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);

                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid) ){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }


    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();

    private void sendMessage(String sender, String receiver, String message){

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").getKey();//Gets the key of the chatroom from Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(key);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        //Adds message info to database
//        HashMap<String, Object> hashMap = new HashMap<>();
        messageMap.put("sender", sender);
        messageMap.put("receiver", classroomid); //The chatroom id will receive this message
        messageMap.put("message", message);
        messageMap.put("isseen",false);
        messageMap.put("messageType", "message");
        messageMap.put("Date and Time", formattedDate);
        messageMap.put("Messages/" + reference.child(key).push().setValue(messageMap), true);
        //reference.child(key).child("Messages").push().setValue(messageMap); //saves each message into ChatRooms/ Proper chat room/ Messages(not working)



        //After chatting with user, adds User to chat fragment: Recent chats with contacts
        //Don't need this code because we have chatrooms now instead of individual messaging(save for future use)
        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(fuser.getUid())
                .child(userid); //Creating a unique chat with receiver and sender in Chatlist folder. Includes the user ID

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessages(String myid, ArrayList<String> classroomid, String imageURL){
        mChat = new ArrayList<>();

        String key = FirebaseDatabase.getInstance().getReference().child("ChatRooms").getKey();//Gets the key of the chatroom from Firebase
        classRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(key).child("Messages"); //Retrieves all messages from ChatRooms/Chatroom ID/ Messages

        classRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){

                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(ClassRoomMessageActivity.this, mChat, imageURL);
                    recyclerView.setAdapter(messageAdapter);
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