package com.example.letschat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextUpName,editTextUpEmail,editTextUpPassword,editTextUpConfirmPassword;
    Button buttonUpConfirm;
    TextView textViewAlreadyUser;
    ImageView imageViewGoogleUp,imageViewFacebookUp;
    boolean passwordVisible;
    public static final int RC_SIGN_IN = 1234;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UserDetail userDetail;
    BeautifulProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //        hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //        finding id's
        editTextUpName = findViewById(R.id.editTextUpName);
        editTextUpEmail = findViewById(R.id.editTextUpEmail);
        editTextUpPassword = findViewById(R.id.editTextUpPassword);
        editTextUpConfirmPassword = findViewById(R.id.editTextUpConfirmPassword);
        buttonUpConfirm = findViewById(R.id.buttonUpConfirm);
        textViewAlreadyUser = findViewById(R.id.textViewAlreadyUser);
        imageViewGoogleUp = findViewById(R.id.imageViewGoogleUp);
        imageViewFacebookUp = findViewById(R.id.imageViewFacebookUp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        customProgressDialog = new BeautifulProgressDialog(SignUpActivity.this,
                BeautifulProgressDialog.withImage,
                "Please wait");
        customProgressDialog.setImageLocation(getResources().getDrawable(R.drawable.lets_chat_logo));
        customProgressDialog.setLayoutColor(getResources().getColor(R.color.white));
        customProgressDialog.setCancelableOnTouchOutside(false);

//        createRequestGoogleSign();

        //        CONFIRM BUTTON
        buttonUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextUpName.getText().toString();
                String email = editTextUpEmail.getText().toString();
                String password = editTextUpPassword.getText().toString();
                String confirmPassword = editTextUpConfirmPassword.getText().toString();
//                ICON FOR ERROR IN EDITTEXT
                Drawable errorDrawable= getResources().getDrawable(R.drawable.ic_nfo);
                errorDrawable.setBounds(0, 0, errorDrawable.getIntrinsicWidth(), errorDrawable.getIntrinsicHeight());
//                CHECKING ALL FIELDS ALL FILL OR NOT
                if (name.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()){
                    editTextUpName.setError("Name Required", errorDrawable);
                    editTextUpEmail.setError("Email Required", errorDrawable);
                    editTextUpPassword.setError("Password Required", errorDrawable);
                    editTextUpConfirmPassword.setError("Confirm Password Required", errorDrawable);

                } else if (name.isEmpty()) {
                    editTextUpName.setError("Name Required", errorDrawable);
                }
                else if (email.isEmpty()) {
                    editTextUpEmail.setError("Email Required", errorDrawable);
                } else if (password.isEmpty()) {
                    editTextUpPassword.setError("Password Required", errorDrawable);
                } else if (confirmPassword.isEmpty()) {
                    editTextUpConfirmPassword.setError("Confirm Password Required", errorDrawable);
                }
                else if (!isValidMail(email)){
                    editTextUpEmail.setError("Invalid Email", errorDrawable);
                }
                else {
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(SignUpActivity.this, "InCorrect Password !", Toast.LENGTH_SHORT).show();
                    } else {
                        userDetail = new UserDetail(name, email, password);
                        signUpUserWithEmailPass(email, password);
                    }
                }
            }
        });

        imageViewGoogleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });


        textViewAlreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                finish();
            }
        });


        editTextUpConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHidePassword(motionEvent);
                return false;
            }
        });


    }

    private void showHidePassword(MotionEvent motionEvent) {

        final int right = 2;
        if (motionEvent.getAction()==MotionEvent.ACTION_UP){
            if (motionEvent.getRawX()>=editTextUpConfirmPassword.getRight()
                    -editTextUpConfirmPassword.getCompoundDrawables()[right].getBounds().width()){
                int selection = editTextUpConfirmPassword.getSelectionEnd();
                if (passwordVisible){
                    //set drawable image
                    editTextUpConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.showpassword_icon,0);
//                           hide password
                    editTextUpConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordVisible = false;
                }else{

                    editTextUpConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.hidepassword_icon,0);
                    editTextUpConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordVisible = true;

                }
                editTextUpConfirmPassword.setSelection(selection);
            }
        }

    }
    private void signUpUserWithEmailPass(@NonNull String email, @NonNull String password) {
        customProgressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword( email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            customProgressDialog.dismiss();
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUpActivity.this, "Register Successful",Toast.LENGTH_SHORT).show();
                            Log.i("SignUpActivity", "createUserWithEmail:success");
                            startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                            FirebaseUser user = firebaseAuth.getCurrentUser();

//                                Store user info on real time firebaseDatabase
                            userDetail.setAuthenticationType("Email & Password");
                            storeUserSignUpData();

                        } else {
                            customProgressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.i("SignUpActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed "+ Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
//    private void createRequestGoogleSign(){
//        googleSignInOptions = new GoogleSignInOptions.Builder(
//                GoogleSignInOptions.DEFAULT_SIGN_IN
//        ).requestIdToken(getString(R.values.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        // Build a GoogleSignInClient with the options specified by gso.
//        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
//    }
    private void signInWithGoogle() {
        //    when user click google button call below method
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        customProgressDialog.show();
//        google signIN check here
        if(requestCode==RC_SIGN_IN)
        {// When request code is equal to 100
            // Initialize task
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if(signInAccountTask.isSuccessful())
            {

                if (data != null) {
                    Log.i("google", String.valueOf(data.getData()));
                }
                Toast.makeText(SignUpActivity.this, "SignIN process", Toast.LENGTH_SHORT).show();
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if(googleSignInAccount!=null)
                    {// When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken()
                                ,null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if(task.isSuccessful()) {
                                            customProgressDialog.dismiss();
                                            //FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(SignUpActivity.this, "Google SignInSuccessful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this,HomeActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                            Log.i("google", String.valueOf(task));
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                            if (firebaseUser != null) {
                                                String name = firebaseUser.getDisplayName();
                                                String email = firebaseUser.getEmail();
                                                String image = String.valueOf(firebaseUser.getPhotoUrl());

                                                if (email != null || name != null) {
                                                    userDetail = new UserDetail();
                                                    userDetail.setEmail(email);
                                                    userDetail.setUserName(name);
                                                    userDetail.setUserImage(image);
//                                                     Store user data coming from google in firebaseDatabase
                                                    userDetail.setAuthenticationType("Google");
                                                    storeUserSignUpData();
                                                }

                                            }


                                        }
                                        else
                                        {
                                            customProgressDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Google SignInFailed "+ Objects.requireNonNull(task.getException())
                                                    .getMessage() , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
                catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    private void storeUserSignUpData(){

         databaseReference = firebaseDatabase.getReference("UserAuthentication");
//        getting user details from UserDetail class
        databaseReference.child(Objects.requireNonNull(firebaseAuth.getUid())).setValue(userDetail);

    }
    private boolean isValidMail(String email) {

        //Regular Expression
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();// mather matched the pattern and return the true or false
    }
}