package com.firstapp.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.Adapter.MediaAdapter;
import com.firstapp.firebasechat.Adapter.MessageAdapter;
import com.firstapp.firebasechat.Model.Chat;
import com.firstapp.firebasechat.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MessageActivity extends AppCompatActivity {

    TextView username;
    ImageView imageView;

    EditText msg_editText;
    ImageButton sendBtn;
    ImageButton imageBtn;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    //Loading Bar
    private ProgressDialog loadingBar;

    //Upload Image
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String checker= "", myUrl="";

    private String mChatUser;
    private String mCurrentUserId;
    private String messageSenderId, messageReceiverId, messageReceiverName, messageReceiverImage;
    Bitmap image;

    MessageAdapter messageAdapter;
    MediaAdapter mediaAdapter;
    List<Chat> mChat;
    ArrayList<String> mediaUriList; //For sending images, adds uri to this list

    RecyclerView recyclerView;
    RecyclerView recyclerViewy, mMedia;
    private RecyclerView.LayoutManager mMediaLayoutManager;
    RecyclerView.Adapter mMediaAdapter;
    String userid;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Widgets
        imageView = findViewById(R.id.imageView_profilepic);
        username = findViewById(R.id.usernamemessage);
        sendBtn = findViewById(R.id.btn_send);
        imageBtn = findViewById(R.id.imgBtn_send);
        msg_editText = findViewById(R.id.text_send);


        //RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

        //Initialize Loading Bar
        loadingBar = new ProgressDialog(this);

//        initializeMedia();

        //Profile Image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("images");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                username.setText(user.getUsername());

                if(user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);
                }
                readMessages(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        //Sending message button listener
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_editText.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg);
                }else{
                    Toast.makeText(MessageActivity.this, "Please send a non empty message", Toast.LENGTH_SHORT).show();
                }
                msg_editText.setText("");
            }
        });

        //When sending image button is pressed...
//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CharSequence options[] = new CharSequence[]
//                {
//                    "Images",
//                    "PDF Files",
//                    "MS Word Files"
//                };
//                //Pop up message prompt asking to pick type of file to send
//                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
//                builder.setTitle("Choose a File");
//
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int i) {
//                        if(i == 0){
//                            checker = "image";
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            intent.setType("image/*");
//                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
//                        }
//                        if(i == 1){
//                            checker = "pdf";
//                        }
//                        if(i == 2){
//                            checker = "docx";
//                        }
//                    }
//                });
//                builder.show();
//            }
//        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
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

        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);
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

                        reference = FirebaseDatabase.getInstance().getReference();

                        //HashMap<String, Object> map = new HashMap<>();
                        messageMap.put("sender", fuser.getUid());
                        messageMap.put("receiver", userid);
                        messageMap.put("message", "Sending Image");
                        messageMap.put("media", mUri);
                        messageMap.put("isseen",false);
                        //reference.updateChildren(messageMap);
                        reference.child("Chats").push().setValue(messageMap);
                        //sendMessage(fuser.getUid(), userid, mUri);

                        progressDialog.dismiss();
                    }
                    else{
                        Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else{
            Toast.makeText(MessageActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(MessageActivity.this, "Upload in progress...", Toast.LENGTH_SHORT).show();
            }else{
                UploadMyImage();
            }
        }
    }

//    private void initializeMedia(){
//        mediaUriList = new ArrayList<>();
//        //mMedia = findViewById(R.id.mediaList); //The recycler view that opens up when you are placing and choosing your images to send
//        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
//        mMedia.setAdapter(mMediaAdapter);
//    }

    //Opens user's gallery
    private void SelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), IMAGE_REQUEST);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            if(requestCode == IMAGE_REQUEST){
//                if(data.getClipData() == null){
//                    mediaUriList.add(data.getData().toString()); //Adds 1 picture to our imageUri list in String
//                }else{
//                    for(int i = 0; i < data.getClipData().getItemCount(); i++){//Adds multiple pictures to our imageUri in String
//                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
//                    }
//                }
//                mMediaAdapter.notifyDataSetChanged();
//            }
//        }
//    }
    

//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null){
//
//            loadingBar.setTitle("Sending File");
//            loadingBar.setMessage("Please wait, your file is sending...");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();
//
//            //Storing file from phone gallery into this fileUri
//            fileUri = data.getData();
//
//            if(!checker.equals("image")){
//                Toast.makeText(this, "Please choose a proper image", Toast.LENGTH_SHORT).show();
//            }
//            else if(checker.equals("image")){
//                storageReference = FirebaseStorage.getInstance().getReference().child("images");
//
//                String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
//                String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;
//
//                DatabaseReference userMessageKeyRef = reference.child("Messages")
//                        .child(messageSenderId).child(messageReceiverId).push();
//
//                final String messagePushID = userMessageKeyRef.getKey();
//
//                //Adds the extension of the image into folder
//                StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
//
//                //Puts image file in this path
//                uploadTask = filePath.putFile(fileUri);
//
//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//
//                        if(!task.isSuccessful()){
//                            throw task.getException();
//                        }
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if(task.isSuccessful()){
//
//                            Uri downloadUrl = task.getResult();
//                            myUrl = downloadUrl.toString();
//
//                            HashMap<String, Object> messagePictureBody = new HashMap<>();
//                            messagePictureBody.put("sender", messageSenderId);
//                            messagePictureBody.put("receiver", messageReceiverId);
//                            messagePictureBody.put("messageID", messagePushID);
//                            messagePictureBody.put("message", myUrl);
//                            messagePictureBody.put("tyoe", checker);
//                            messagePictureBody.put("name", fileUri.getLastPathSegment());
//                            messagePictureBody.put("isseen",false);
////                            hashMap.put("time", currentTime);
////                            hashMap.put("date", currentDate);
//
//                            HashMap<String, Object> messageBodyDetails = new HashMap<>();
//                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageBodyDetails);
//                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageBodyDetails);
//
////                            reference.updateChildren(messageBodyDetails).addOnCompleteListener((task1) -> {
////                                if (task1.isSuccessful()){
////                                    loadingBar.dismiss();
////                                    Toast.makeText(MessageActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
////                                }
////                                else{
////                                    loadingBar.dismiss();
////                                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
////                                }
////
////                            });
//
//                        }
//                    }
//                });
//
//            }
//            else{
//                loadingBar.dismiss();
//                Toast.makeText(MessageActivity.this, "Nothing selected, try again", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }



    //Changes message to seen when receiver sees the sender"s message
    private void SeenMessage(String userid){

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //Adds message info to database
//        HashMap<String, Object> hashMap = new HashMap<>();
        messageMap.put("sender", sender);
        messageMap.put("receiver", receiver);
        messageMap.put("message", message);
        messageMap.put("isseen",false);
        reference.child("Chats").push().setValue(messageMap);

//        if(!mediaUriList.isEmpty()){
//            for(String mediaUri : mediaUriList){
//                String mediaId = reference.child("media").push().getKey();
//                mediaIdList.add(mediaId);
//                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images");
//
//                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
//
//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                hashMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
//
//                                totalMediaUploaded++;
//                                if(totalMediaUploaded == mediaUriList.size()){
//                                    updateDatabaseWithNewMessage(reference, hashMap);
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }else{
//            if(!msg_editText.getText().toString().isEmpty()){
//                updateDatabaseWithNewMessage(reference, hashMap);
//            }
//        }


        //After chatting with user, adds User to chat fragment: Recent chats with contacts
        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(fuser.getUid())
                .child(userid);

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

//    private void updateDatabaseWithNewMessage(DatabaseReference databaseReference, HashMap hashMap ){
//        databaseReference.updateChildren(hashMap);
//        mediaUriList.clear();
//        mediaIdList.clear();
//        mMediaAdapter.notifyDataSetChanged();
//
//    }


//    private void sendImage(String sender, String receiver, String message){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("sender", sender);
//        hashMap.put("receiver", receiver);
//        hashMap.put("message", message);
//        hashMap.put("isseen",false);
//
//        reference.child("Chats").push().setValue(hashMap);
//
//        //Adding User to chat fragment: Latest chats with contacts
//        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
//                .getReference("ChatList")
//                .child(fuser.getUid())
//                .child(userid);
//
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatRef.child("id").setValue(userid);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void readMessages(String myid, String userid, String imageURL){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                        chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){

                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    //Checks online or offline status of a user
    private void CheckStatus(String status){

        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);

    }

    //Online status
    @Override
    protected void onResume(){
        super.onResume();
        CheckStatus("Online");
    }

    //Offline status
    @Override
    protected void onPause(){
        super.onPause();
        reference.removeEventListener(seenListener);
        CheckStatus("Offline");
    }



}