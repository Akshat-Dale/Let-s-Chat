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

import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog;
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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1234;
    private FirebaseAuth firebaseAuth;
    EditText editTextInEmail,editTextInPassword;
    Button buttonInConfirm,buttonInRegister;
    TextView textViewForgotPassword;
    ImageView imageGoogleIn,imageFacebookIn;
    private GoogleSignInClient mGoogleSignInClient;
    boolean passwordVisible;
    FirebaseDatabase firebaseDatabase;
    UserDetail userDetail;
    BeautifulProgressDialog customProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //        hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        //        finding id's
        editTextInEmail = findViewById(R.id.editTextInEmail);
        editTextInPassword = findViewById(R.id.editTextInPassword);
        buttonInConfirm = findViewById(R.id.buttonInConfirm);
        buttonInRegister = findViewById(R.id.buttonInRegister);
        textViewForgotPassword = findViewById(R.id.textViewInForgotPassword);
        imageGoogleIn = findViewById(R.id.imageGoogleIn);
        imageFacebookIn = findViewById(R.id.imageFacebookIn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDetail = new UserDetail();
        //custom dialog box
        customProgressDialog = new BeautifulProgressDialog(SignInActivity.this,
                BeautifulProgressDialog.withGIF,
                "Please wait");
        customProgressDialog.setImageLocation(getResources().getDrawable(R.drawable.lets_chat_logo));
        customProgressDialog.setLayoutColor(getResources().getColor(R.color.white));
        customProgressDialog.setCancelableOnTouchOutside(false);
//        createRequestGoogleSign();


        buttonInConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable errorDrawable= getResources().getDrawable(R.drawable.ic_nfo);
                errorDrawable.setBounds(0, 0, errorDrawable.getIntrinsicWidth(), errorDrawable.getIntrinsicHeight());

                String email = editTextInEmail.getText().toString();
                String password = editTextInPassword.getText().toString();

                if (email.isEmpty() && password.isEmpty()){
                    editTextInEmail.setError("Email Required", errorDrawable);
                    editTextInPassword.setError("Password Required", errorDrawable);
                }
                else if (email.isEmpty()) {
                    editTextInEmail.setError("Email Required", errorDrawable);
                }
                else if (password.isEmpty()) {
                    editTextInPassword.setError("Password Required", errorDrawable);
                }
                else if (!isValidMail(email)){
                    editTextInEmail.setError("Invalid Email", errorDrawable);
                }
                else {
                    signInUserWithEmailPassword(email, password);
                }
            }
        });

        buttonInRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }

        });

        imageGoogleIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    signInGoogle();
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
            }
        });

        editTextInPassword.setOnTouchListener((view, motionEvent) -> {
            showHidePassword(motionEvent);
            return false;
        });


    }


    private void showHidePassword(MotionEvent motionEvent) {

        final int right = 2;
        if (motionEvent.getAction()==MotionEvent.ACTION_UP){
            if (motionEvent.getRawX()>=editTextInPassword.getRight()
                    -editTextInPassword.getCompoundDrawables()[right].getBounds().width()){
                int selection = editTextInPassword.getSelectionEnd();
                if (passwordVisible){
                    //set drawable image
                    editTextInPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.showpassword_icon,0);
//                           hide password
                    editTextInPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordVisible = false;

                }else{

                    editTextInPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.hidepassword_icon,0);
                    editTextInPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordVisible = true;
                }
                editTextInPassword.setSelection(selection);
            }
        }


    }

    private void signInUserWithEmailPassword(String email, String password) {
        customProgressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            customProgressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("SignInActivity", "signInWithEmail:success");
                            Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(SignInActivity.this,HomeActivity.class));

                        } else {
                            customProgressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.i("SignInActivity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

//    private void createRequestGoogleSign(){
//        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(
//                GoogleSignInOptions.DEFAULT_SIGN_IN
//        ).requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        // Build a GoogleSignInClient with the options specified by gso.
//        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
//    }

    private void signInGoogle() {
        //    when user click google button call below method
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
//        google signIN check here
        if(requestCode==RC_SIGN_IN)
        {
            // When request code is equal to 100
            // Initialize task
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if(signInAccountTask.isSuccessful())
            {
                customProgressDialog.show();
                if (data != null) {
                    Log.i("google","Intent data activityResult "+ String.valueOf(data.getData()));
                }
                Toast.makeText(SignInActivity.this, "SignIN process", Toast.LENGTH_SHORT).show();
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
                                        if(task.isSuccessful())
                                        { customProgressDialog.dismiss();
                                            //FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(SignInActivity.this, "Google SignInSuccessful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignInActivity.this,HomeActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                            Log.i("google", "Value from task "+String.valueOf(task));
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                            if (firebaseUser != null) {
                                                String name = firebaseUser.getDisplayName();
                                                String email = firebaseUser.getEmail();
                                                String image = String.valueOf(firebaseUser.getPhotoUrl());

                                                if (email != null || name != null) {
                                                    userDetail.setEmail(email);
                                                    userDetail.setUserName(name);
                                                    userDetail.setUserImage(image);
                                                    userDetail.setAuthenticationType("Google");
//                                                     Store user data coming from google in firebaseDatabase
                                                    storeUserSignUpData();
                                                }

                                            }
                                        }
                                        else
                                        {customProgressDialog.dismiss();
                                            Toast.makeText(SignInActivity.this, "Google SignInFailed "+ Objects.requireNonNull(task.getException())
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

        DatabaseReference databaseReference = firebaseDatabase.getReference("UserAuthentication");
//        getting user details from UserDetail class
        databaseReference.child(Objects.requireNonNull(firebaseAuth.getUid())).setValue(userDetail);


    }

    public static boolean isValidMail(String email)
    {
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