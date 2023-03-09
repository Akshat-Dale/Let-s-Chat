package com.example.letschat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.letschat.Activity.HomeActivity;
import com.example.letschat.Fragment.ChatFragment;
import com.example.letschat.Fragment.StorieFragment;
import com.example.letschat.Models.Status;
import com.example.letschat.Models.UserStatus;
import com.example.letschat.R;
import com.example.letschat.databinding.ItemStatusBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    Context context;
    ArrayList<UserStatus> statusArrayList;

    public StatusAdapter(Context context, ArrayList<UserStatus> statusArrayList) {
        this.context = context;
        this.statusArrayList = statusArrayList;
    }

    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, int position) {

        UserStatus userStatus = statusArrayList.get(position);
        holder.itemStatusBinding.userNameStausTV.setText(userStatus.getUserName());
        holder.itemStatusBinding.lastUpdateStatusTV.setText(userStatus.getLastUpdated());
        Picasso.get().load(userStatus.getProfileImage()).placeholder(R.drawable.user_imageicon).into(holder.itemStatusBinding.circleImageStatus);

//        Status lastStatus = userStatus.getStatusArrayList().get(userStatus.getStatusArrayList().size()-1);
        holder.itemStatusBinding.circularStatusView.setPortionsCount(userStatus.getStatusArrayList().size());


        Log.i("status list size", String.valueOf(userStatus.getStatusArrayList().size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStoryArrayList = new ArrayList<>();
                for (Status status: userStatus.getStatusArrayList()) {
                    myStoryArrayList.add(new MyStory(status.getImageUrl()));
                }

                new StoryView.Builder(((FragmentActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStoryArrayList) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getUserName()) // Default is Hidden
                        .setSubtitleText(userStatus.getLastUpdated()) // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage())
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.i("userstatus","list size adapter "+statusArrayList.size());
        return statusArrayList.size();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding itemStatusBinding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemStatusBinding = ItemStatusBinding.bind(itemView);
        }
    }
}
