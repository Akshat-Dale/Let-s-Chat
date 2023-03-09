package com.example.letschat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //        hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        try {


            Paper.init(SplashActivity.this);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference("UserAuthentication").child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.i("splashScreen", snapshot.toString());
                    UserDetail userDetail = snapshot.getValue(UserDetail.class);

                    if (userDetail != null) {
                        Paper.book().write("UserDetail", userDetail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.i("splashScreen", error.toString());
                }
            });


            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile_picture")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    if (uri != null) {
                        Paper.book().write("UserImage", uri.toString());
                    }
                }
            });

        }
        catch (Exception e){
            Toast.makeText(this, "Crash", Toast.LENGTH_SHORT).show();
        }


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this,SignInActivity.class));
                }
                finish();
            }
        },4000);
    }
}