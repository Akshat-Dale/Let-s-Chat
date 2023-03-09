package com.example.letschat.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.letschat.Fragment.ChatFragment;
import com.example.letschat.Fragment.ProfileFragment;
import com.example.letschat.Fragment.StorieFragment;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    MeowBottomNavigation meowBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Objects.requireNonNull(getSupportActionBar()).setElevation(5.0f);
        mAuth = FirebaseAuth.getInstance();
        meowBottomNavigation = findViewById(R.id.bottomNavigation);

//        meowBottomNavigation adding menu
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.chat_icon));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.stories_icon));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.profile_icon));

//                set when click on item what should open
        meowBottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Fragment fragment = null;

                switch (model.getId()){

                    case 1:
                        fragment = new ChatFragment();
                        break;

                    case 2:
                        fragment = new StorieFragment();
                        break;

                    case 3:
                        fragment = new ProfileFragment();
                        break;
                }
                loadFragment(fragment);
                return null;
            }
        });

        //                notificaiton count
        meowBottomNavigation.setCount(1,"10");

        //                set fragment default selected
        meowBottomNavigation.show(1,true);

        //            on item clicked.
        meowBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                return null;
            }
        });

        //        on item reselct.
        meowBottomNavigation.setOnReselectListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                return null;
            }
        });


    }


    //loading fragment on frame layout.
    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutHome,fragment).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("User_Presence").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .setValue("online");

    }

}