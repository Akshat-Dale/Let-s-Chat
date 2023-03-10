package com.example.letschat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog;
import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {


    public static final int SELECT_PICTURE = 1;
    CircleImageView circleImageAccount;
    ImageButton imageButtonAddImage,imageButtonPassword;
    EditText editTextNameAccount,editTextBioAccount,editTextEmailAccount;
    Button buttonConfirmAccount;
    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    BeautifulProgressDialog customProgressDialog;
    ProgressBar progressBarCircleImage;
    UserDetail userDetail1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Setting");

        intializeValues();//all variable initialize inside this method
        setDetailsOnText();// getting user details from firebase database and set to textview and edittext


        //custom dialog box
        customProgressDialog = new BeautifulProgressDialog((Activity) AccountActivity.this,
                BeautifulProgressDialog.withGIF,
                "Please wait");
        customProgressDialog.setImageLocation(getResources().getDrawable(R.drawable.lets_chat_logo));
        customProgressDialog.setLayoutColor(getResources().getColor(R.color.white));
        customProgressDialog.setCancelableOnTouchOutside(false);
//        customProgressDialog.show();



        //        add image button clicked
        imageButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_PICTURE);
            }
        });


        buttonConfirmAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show();
                Toast.makeText(AccountActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                String name = editTextNameAccount.getText().toString();
                String bio = editTextBioAccount.getText().toString();


                HashMap<String, String> hashMap = new HashMap<>();
                if (!name.isEmpty() && !bio.isEmpty() ) {
                    hashMap.put("userName", name);
                    hashMap.put("userBio", bio);
                    hashMap.put("userImage", userDetail1.getUserImage());
                    hashMap.put("authenticationType", userDetail1.getAuthenticationType());
                    hashMap.put("email", userDetail1.getEmail());
                    hashMap.put("password", userDetail1.getPassword());

                }


                firebaseDatabase.getReference().child("UserAuthentication").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                customProgressDialog.dismiss();
                                Toast.makeText(AccountActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            if (requestCode==SELECT_PICTURE){

                progressBarCircleImage.setVisibility(View.VISIBLE);
                Uri imageUri = data.getData();//user image location
//                Store user image in firebase storage
                final StorageReference storageReference = firebaseStorage.getReference().child("Profile_picture")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AccountActivity.this, "Profile Picture Updated.", Toast.LENGTH_SHORT).show();

//                        getting user image link from storage
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                firebaseDatabase.getReference().child("UserAuthentication").child(FirebaseAuth.getInstance().getUid())
                                        .child("userImage").setValue(uri.toString());

                                Picasso.get().load(uri).placeholder(R.drawable.user_imageicon).into(circleImageAccount);

                                Toast.makeText(AccountActivity.this, "Profile Picture Uploaded.", Toast.LENGTH_SHORT).show();
                                progressBarCircleImage.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            }
        }



//        update details

    }

    public void setDetailsOnText(){
        firebaseDatabase.getReference().child("UserAuthentication").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         userDetail1 = snapshot.getValue(UserDetail.class);
                        if (userDetail1 != null) {
                            Picasso.get().load(userDetail1.getUserImage()).placeholder(R.drawable.user_imageicon).into(circleImageAccount);
                            editTextBioAccount.setText(userDetail1.getUserBio());
                            editTextNameAccount.setText(userDetail1.getUserName());
                            editTextEmailAccount.setText(userDetail1.getEmail());

                            String bio = userDetail1.getUserBio();
                            if (bio==null || bio.isEmpty()) {
                                editTextBioAccount.setText("Bio not Available");

                            } else {
                                editTextBioAccount.setText(userDetail1.getUserBio());
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    void intializeValues(){

        circleImageAccount = findViewById(R.id.circleImageAccount);
        imageButtonAddImage = findViewById(R.id.imageButtonAddImage);
        imageButtonPassword = findViewById(R.id.imageButtonPassword);
        editTextNameAccount = findViewById(R.id.editTextNameAccount);
        editTextBioAccount = findViewById(R.id.editTextBioAccount);
        editTextEmailAccount = findViewById(R.id.editTextEmailAccount);
        buttonConfirmAccount = findViewById(R.id.buttonConfirmAccount);
        progressBarCircleImage = findViewById(R.id.progressBarCircleImage);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }
}