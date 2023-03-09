package com.example.letschat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.letschat.Models.UserPrivacyOption;
import com.example.letschat.R;
import com.example.letschat.databinding.ActivitySettingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding activitySettingBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


         activitySettingBinding = DataBindingUtil.setContentView(SettingActivity.this,R.layout.activity_setting);

         getSwitchIsEnabled();
         getSwitchStatus();



    }

    private void getSwitchStatus() {

        activitySettingBinding.switchPresenceStatusChat.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.i("switch", "switchPresenceStatusChat "+b);
            storeSwitch("PresenceStatusChat",b);
        });

        activitySettingBinding.switchPresenceStatusHome.setOnCheckedChangeListener((compoundButton, b) -> {

            Log.i("switch", "switchPresenceStatusHome "+b);
            storeSwitch("PresenceStatusHome",b);
        });

        activitySettingBinding.switchProfilePicture.setOnCheckedChangeListener((compoundButton, b) -> {

            Log.i("switch", "switchProfilePicture "+b);
            storeSwitch("ProfilePicture",b);
        });

        activitySettingBinding.switchLastSeenHome.setOnCheckedChangeListener((compoundButton, b) -> {

            Log.i("switch", "switchLastSeenHome "+b);
            storeSwitch("LastSeenHome",b);
        });

        activitySettingBinding.switchDarkMode.setOnCheckedChangeListener((compoundButton, b) -> {

            Log.i("switch", "switchDarkMode "+b);
            storeSwitch("DarkMode",b);
        });
    }

    private void getSwitchIsEnabled() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("UserAuthentication").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("User_Privacy_Setting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserPrivacyOption userPrivacyOption = new UserPrivacyOption();

                for (DataSnapshot snap: snapshot.getChildren()) {
                    Log.i("akshatsetting",snap.toString());

                    if (snap.getKey()=="ProfilePicture"){

                        if (Objects.equals(snap.getValue(), true)){
                            userPrivacyOption.setProfilePicture(true);
                        } else {
                            userPrivacyOption.setProfilePicture(false);
                        }

                    }

                    if (snap.getKey()=="PresenceStatusHome"){
                        if (Objects.equals(snap.getValue(), true)) {
                            userPrivacyOption.setPresenceStatusHome(true);
                        }
                        else {
                            userPrivacyOption.setPresenceStatusHome(false);
                        }
                    }

                    if (snap.getKey()=="LastSeenHome"){
                        if (Objects.equals(snap.getValue(), true)) {
                            userPrivacyOption.setLastSeenHome(true);
                        }
                        else {
                            userPrivacyOption.setLastSeenHome(false);
                        }

                    }

                    if (snap.getKey()=="PresenceStatusChat"){

                        if (Objects.equals(snap.getValue(), true)) {
                            userPrivacyOption.setPresenceStatusChat(true);
                        }
                        else {
                            userPrivacyOption.setPresenceStatusChat(false);
                        }
                    }

                    if (snap.getKey()=="DarkMode"){
                        if (Objects.equals(snap.getValue(), true)) {
                            userPrivacyOption.setDarkMode(true);
                        }
                        else {
                            userPrivacyOption.setDarkMode(false);
                        }
                    }


                    if (userPrivacyOption.isDarkMode()){
                        activitySettingBinding.switchDarkMode.setChecked(true);
                    }
                    if (userPrivacyOption.isLastSeenHome()){
                        activitySettingBinding.switchLastSeenHome.setChecked(true);
                    }
                    if (userPrivacyOption.isPresenceStatusChat()){
                        activitySettingBinding.switchPresenceStatusChat.setChecked(true);
                    }
                    if (userPrivacyOption.isProfilePicture()){
                        activitySettingBinding.switchProfilePicture.setChecked(true);
                    }
                    if (userPrivacyOption.isPresenceStatusHome()){
                        activitySettingBinding.switchPresenceStatusHome.setChecked(true);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("akshat setting",error.toString());
                Toast.makeText(SettingActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void storeSwitch(String switchName, boolean isEnabled) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAuthentication");
        databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("User_Privacy_Setting").child(switchName).setValue(isEnabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("User_Presence").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .setValue("online");

    }


}