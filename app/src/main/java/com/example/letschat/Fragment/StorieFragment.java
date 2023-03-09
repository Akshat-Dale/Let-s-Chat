package com.example.letschat.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.letschat.Adapters.StatusAdapter;
import com.example.letschat.Models.Status;
import com.example.letschat.Models.UserDetail;
import com.example.letschat.Models.UserStatus;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class StorieFragment extends Fragment {


    FloatingActionButton floatButtonAddStatus;
    RecyclerView recyclerViewStatus;
    ArrayList<UserStatus> userStatusArrayList;
    StatusAdapter statusAdapter;
    ProgressDialog progressDialog;

    UserDetail userDetail;
    FirebaseDatabase firebaseDatabase;
    TextView noStoriesTv;

    public StorieFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_storie, container, false);

        floatButtonAddStatus = view.findViewById(R.id.floatButtonAddStatus);
        recyclerViewStatus = view.findViewById(R.id.recyclerViewStatus);
        noStoriesTv = view.findViewById(R.id.noStoriesTv);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Uploading Status...");
        progressDialog.setCancelable(false);

        userStatusArrayList = new ArrayList<>();
        statusAdapter = new StatusAdapter(view.getContext(),userStatusArrayList);
        recyclerViewStatus.setAdapter(statusAdapter);

        if (userStatusArrayList.size()>0){
            recyclerViewStatus.setVisibility(View.VISIBLE);
            noStoriesTv.setVisibility(View.GONE);
        }
        else {
            recyclerViewStatus.setVisibility(View.GONE);
            noStoriesTv.setVisibility(View.VISIBLE);
        }
        Log.i("userstatus","list size fragment global "+userStatusArrayList.size());

        floatButtonAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStatus();
            }
        });


        getStatus();
        firebaseDatabase.getReference("UserAuthentication").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        userDetail = snapshot.getValue(UserDetail.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        return view;
    }

    private void getStatus() {

        firebaseDatabase.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userStatusArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setUserName(snapshot1.child("userName").getValue(String.class));
                        userStatus.setLastUpdated(snapshot1.child("lastUpdated").getValue(String.class));
                        userStatus.setProfileImage(snapshot1.child("profileImage").getValue(String.class));



                        ArrayList<Status> statusArrayList = new ArrayList<>();
                        for (DataSnapshot statusSnapshot:snapshot1.child("Status").getChildren()) {
                            Log.i("status snap", String.valueOf(statusSnapshot));

                            Status status = statusSnapshot.getValue(Status.class);
                                statusArrayList.add(status);



                            Log.i("userstatus","list size fragment local "+userStatusArrayList.size());

                        }

                        userStatus.setStatusArrayList(statusArrayList);
                        userStatusArrayList.add(userStatus);
                        statusAdapter.notifyDataSetChanged();

                        if (userStatusArrayList.size()>0){
                            recyclerViewStatus.setVisibility(View.VISIBLE);
                            noStoriesTv.setVisibility(View.GONE);
                        }
                        else {
                            recyclerViewStatus.setVisibility(View.GONE);
                            noStoriesTv.setVisibility(View.VISIBLE);
                        }
                        Log.i("status list size frag", String.valueOf(snapshot.getKey()));

//                        if (!userDetail.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
//                            userStatusArrayList.add(userStatus);
//                        }




                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addStatus() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date dateee = new Date();
        String date = formatter.format(dateee);

        if (data!=null){

            if (requestCode==10){

                progressDialog.show();
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference().child("Status").child(date+"");
                storageReference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setUserName(userDetail.getUserName());
                                    userStatus.setProfileImage(userDetail.getUserImage());
                                    userStatus.setLastUpdated(date);

                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("userName",userStatus.getUserName());
                                    hashMap.put("profileImage",userStatus.getProfileImage());
                                    hashMap.put("lastUpdated",userStatus.getLastUpdated());


                                    Status status = new Status(uri.toString(),userStatus.getLastUpdated());


                                    firebaseDatabase.getReference().child("stories").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                            .updateChildren(hashMap);

                                    firebaseDatabase.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                                                    .child("Status").push().setValue(status);

                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}