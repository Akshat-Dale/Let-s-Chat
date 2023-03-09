package com.example.letschat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.letschat.Activity.ChattingActivity;
import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class ChatHolderAdapter extends RecyclerView.Adapter<ChatHolderAdapter.ViewHolder> {

    Context context;
    ArrayList<UserDetail> userDetailArrayList;

    public ChatHolderAdapter(Context context, ArrayList<UserDetail> userDetailArrayList) {
        this.context = context;
        this.userDetailArrayList = userDetailArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_holder,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDetail userDetail = userDetailArrayList.get(position);

        Picasso.get().load(userDetail.getUserImage()).placeholder(R.drawable.user_imageicon).into(holder.userImageView);
        holder.userName.setText(userDetail.getUserName());

        //getting using presence add set to textview
        FirebaseDatabase.getInstance().getReference().child("User_Presence").child(userDetail.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    holder.userPresence.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        //getting last message,time from firebase database
        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid()+userDetail.getUserId())
                .orderByChild("timeStamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()){
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()) {

                                Log.i("lastMessage",Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString());
                                holder.lastMessage.setText(Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString());
                                Log.i("lastMessageTime",Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString());
                                holder.lastMessageTime.setText(Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("receiverId",userDetail.getUserId());
                intent.putExtra("userName",userDetail.getUserName());
                intent.putExtra("profileImage",userDetail.getUserImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDetailArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView userName,lastMessage,lastMessageTime,userPresence;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.textViewUserName);
            lastMessage = itemView.findViewById(R.id.textViewlastMessage);
            lastMessageTime = itemView.findViewById(R.id.textViewlastMessagetime);
            userPresence = itemView.findViewById(R.id.textViewPresence);
        }
    }
}
