package com.example.letschat.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog;
import com.example.letschat.Adapters.ChatHolderAdapter;
import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class ChatFragment extends Fragment {


    ArrayList<UserDetail> userDetailArrayList;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerViewChat;
    BeautifulProgressDialog customProgressDialog;
    FirebaseAuth firebaseAuth;

    public ChatFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        userDetailArrayList = new ArrayList<>();
        ChatHolderAdapter chatHolderAdapter = new ChatHolderAdapter(getContext(),userDetailArrayList);

        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setAdapter(chatHolderAdapter);

        //custom dialog box
        customProgressDialog = new BeautifulProgressDialog((Activity) view.getContext(),
                BeautifulProgressDialog.withGIF,
                "Please wait");
        customProgressDialog.setImageLocation(getResources().getDrawable(R.drawable.lets_chat_logo));
        customProgressDialog.setLayoutColor(getResources().getColor(R.color.white));
        customProgressDialog.setCancelableOnTouchOutside(false);
        customProgressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        firebaseDatabase.getReference("UserAuthentication").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDetailArrayList.clear();
                customProgressDialog.dismiss();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Log.i("snapshot",data.toString());
                    UserDetail userDetail = data.getValue(UserDetail.class);
                    if (userDetail != null) {
                        userDetail.setUserId(data.getKey());
                        if (!userDetail.getUserId().equals(Objects.requireNonNull(firebaseAuth.getUid()).toString())) {
                            userDetailArrayList.add(userDetail);
                        }
                    }
                }
                chatHolderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        firebaseDatabase.getReference().child("User_Presence").child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue("online");

        ChatHolderAdapter chatHolderAdapter = new ChatHolderAdapter(getContext(),userDetailArrayList);
        chatHolderAdapter.notifyDataSetChanged();

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