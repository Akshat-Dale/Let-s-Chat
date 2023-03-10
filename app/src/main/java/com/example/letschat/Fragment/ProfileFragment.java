package com.example.letschat.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.letschat.Activity.AccountActivity;
import com.example.letschat.Activity.SettingActivity;
import com.example.letschat.Activity.SignInActivity;
import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class ProfileFragment extends Fragment {


    ConstraintLayout constraint_profile,constraint_account,constraint_setting,constraint_invite,constraint_help,constraint_logout,constraint_about;
    TextView textViewUserNameProfile,textViewUserBioProfile;
    CircleImageView circleImageProfile;
    View view;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_profile, container, false);

        intializeVariables();

        Paper.init(view.getContext());
        UserDetail userDetail = Paper.book().read("UserDetail");
        String userImage = Paper.book().read("UserImage");

        if (userDetail!=null){

                textViewUserNameProfile.setText(userDetail.getUserName());
                if (userDetail.getUserBio()!=null){
                    textViewUserBioProfile.setText(userDetail.getUserBio());
                }
                else {
                    textViewUserBioProfile.setText("Bio Not Available");
                }



        }

        if (userImage!=null){
            Picasso.get().load(userImage).placeholder(R.drawable.user_imageicon).into(circleImageProfile);
        }



        constraint_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivity(new Intent(view.getContext(), AccountActivity.class));
            }
        });

        constraint_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               HelpBottomSheetFragment helpBottomSheetFragment = new HelpBottomSheetFragment();
               helpBottomSheetFragment.show(getChildFragmentManager(),helpBottomSheetFragment.getTag());
            }
        });

        constraint_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure do you want to logout ?");
                builder.setTitle("Log Out");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(view.getContext(), SignInActivity.class));


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        constraint_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                String shareBody = "Enjoy chatting with let's chat download now ";
                intent2.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent2, "Share via"));



            }
        });

        constraint_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SettingActivity.class));
            }
        });

        constraint_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AboutBottomSheetFragment aboutBottomSheetFragment = new AboutBottomSheetFragment();
                aboutBottomSheetFragment.show(getChildFragmentManager(),aboutBottomSheetFragment.getTag());
            }
        });

        return view;
    }

    private void intializeVariables() {

        constraint_about = view.findViewById(R.id.constraint_about);
        constraint_account = view.findViewById(R.id.constraint_account);
        constraint_help = view.findViewById(R.id.constraint_help);
        constraint_profile = view.findViewById(R.id.constraint_profile);
        constraint_invite = view.findViewById(R.id.constraint_invite);
        constraint_logout = view.findViewById(R.id.constraint_logout);
        constraint_setting = view.findViewById(R.id.constraint_setting);

        textViewUserBioProfile = view.findViewById(R.id.textViewUserBioProfile);
        textViewUserNameProfile = view.findViewById(R.id.textViewUserNameProfile);

        circleImageProfile = view.findViewById(R.id.circleImageProfile);



    }
}