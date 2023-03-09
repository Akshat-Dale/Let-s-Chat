package com.example.letschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.letschat.Models.ChatData;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChattingAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ChatData> chatDataArrayList;

    public ChattingAdapter(Context context, ArrayList<ChatData> chatDataArrayList) {
        this.context = context;
        this.chatDataArrayList = chatDataArrayList;
    }

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE =2;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.send_message,parent,false);
            return  new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.receive_message,parent,false);
            return  new ReceiverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        //checking who send the message
        if (chatDataArrayList.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatData chatData = chatDataArrayList.get(position);

        if (holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMessage.setText(chatData.getMessage());
            ((SenderViewHolder)holder).senderTime.setText(String.valueOf(chatData.getTimeStamp()));
//            ((SenderViewHolder)holder).senderStatus.setText(messageStatus.getStatus());



        }
        else {
            ((ReceiverViewHolder)holder).receiveMessage.setText(chatData.getMessage());
            ((ReceiverViewHolder)holder).receiveTime.setText(String.valueOf(chatData.getTimeStamp()));
//            ((ReceiverViewHolder)holder).receiveStatus.setText(messageStatus.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }



    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiveMessage ,receiveTime, receiveStatus;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiveMessage = itemView.findViewById(R.id.textViewMessageRecieved);
            receiveTime = itemView.findViewById(R.id.textViewRecievedTime);
            receiveStatus = itemView.findViewById(R.id.textViewRecieveStatus);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMessage ,senderTime , senderStatus;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessage = itemView.findViewById(R.id.textViewMessageSend);
            senderTime = itemView.findViewById(R.id.textViewTimeSend);
            senderStatus = itemView.findViewById(R.id.textViewSendStatus);
        }
    }
}
