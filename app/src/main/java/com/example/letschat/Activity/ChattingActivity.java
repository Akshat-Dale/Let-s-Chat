package com.example.letschat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.letschat.Adapters.ChattingAdapter;
import com.example.letschat.Fragment.UserProfileFragment;
import com.example.letschat.Models.ChatData;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingActivity extends AppCompatActivity {


    ImageButton imageButtonBack,imageButtonMenu,imageButtonSend;
    EditText editTextTypeMessage;
    TextView textViewUserNameChat;
    CircleImageView imageViewUser;
    TextView textViewPresenceInChat;
    RecyclerView recyclerViewChatting;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonMenu = findViewById(R.id.imageButtonMenu);
        imageButtonSend = findViewById(R.id.imageButtonSend);
        editTextTypeMessage = findViewById(R.id.editTextTypeMessage);
        textViewPresenceInChat = findViewById(R.id.textViewPresenceChat);
        textViewUserNameChat = findViewById(R.id.textViewUserNameChat);
        imageViewUser = findViewById(R.id.imageViewUser);
        recyclerViewChatting = findViewById(R.id.recyclerViewChatting);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        //        get data from ChatHolderAdapter through intent passing
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String userProfileImage = intent.getStringExtra("profileImage");


        final String senderId = firebaseAuth.getUid();// i am sender
        String recieverId = intent.getStringExtra("receiverId");// jiske name pr click kiya vo reciever
        Log.i("senderID",senderId);
        Log.i("recieverID",recieverId);


        //getting user presence online or offline
        firebaseDatabase.getReference().child("User_Presence").child(recieverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    textViewPresenceInChat.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //        set user data getting from intent
        textViewUserNameChat.setText(userName);
        Picasso.get().load(userProfileImage).placeholder(R.drawable.user_imageicon).into(imageViewUser);

        //        click on top back arrow
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChattingActivity.this,HomeActivity.class));
                finish();
            }
        });

        // ON CLICK USER IMAGE
        imageViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.show(getSupportFragmentManager(),userProfileFragment.getTag());



            }
        });


        final ArrayList<ChatData> chatDataArrayList = new ArrayList<>();
        final ChattingAdapter chattingAdapter = new ChattingAdapter(ChattingActivity.this,chatDataArrayList);

        recyclerViewChatting.setAdapter(chattingAdapter);

        final String senderRoom = senderId + recieverId;
        final String recieverRoom = recieverId + senderId;

        firebaseDatabase.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatDataArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    ChatData chatData = dataSnapshot.getValue(ChatData.class);
                    chatDataArrayList.add(chatData);//add message in arraylist which show in recyclerview
                }
                chattingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextTypeMessage.getText().toString();

                final  ChatData chatData = new ChatData(senderId,message);
                //set time when message is send
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                chatData.setTimeStamp(dateFormat.format(date));
                editTextTypeMessage.setText("");// clear edittext after send message

//                send message to firebasedatabase
                firebaseDatabase.getReference().child("Chats").child(senderRoom).push()
                        .setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {


                                Log.i("ChatData", chatData.toString());
                                firebaseDatabase.getReference().child("Chats").child(recieverRoom).push()
                                        .setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });


                            }
                        });
            }
        });

        //set typing status while typing
        final Handler handler = new Handler();

        editTextTypeMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (senderId != null) {
                    firebaseDatabase.getReference().child("User_Presence").child(senderId).setValue("Typing...");
                }
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runnable,500);

            }
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (senderId != null) {
                        firebaseDatabase.getReference().child("User_Presence").child(senderId).setValue("online");
                    }
                }
            };
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        firebaseDatabase.getReference().child("User_Presence").child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue("online");

    }

    @Override
    public void onPause() {
        super.onPause();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String time = dateFormat.format(date);

        firebaseDatabase.getReference().child("User_Presence").child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue("offline at "+time);

    }
}