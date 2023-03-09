package com.example.letschat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letschat.Models.UserDetail;
import com.example.letschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText editTextForgotPasswordEmail;
    Button buttonSendLinkPassword;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    UserDetail userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide();
        editTextForgotPasswordEmail = findViewById(R.id.editTextForgotPasswordEmail);
        buttonSendLinkPassword = findViewById(R.id.buttonSendLinkPassword);
        auth = FirebaseAuth.getInstance();
        userDetail = new UserDetail();
        firebaseDatabase = FirebaseDatabase.getInstance();

        buttonSendLinkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable errorDrawable= getResources().getDrawable(R.drawable.ic_nfo);
                errorDrawable.setBounds(0, 0, errorDrawable.getIntrinsicWidth(), errorDrawable.getIntrinsicHeight());


                String emailForReset = editTextForgotPasswordEmail.getText().toString();
//                check email is valid or not null or empty
                if (emailForReset.isEmpty() || emailForReset.equals("null")){
                    editTextForgotPasswordEmail.setError("Email Required",errorDrawable);
                }
                else if (!isValidMail(emailForReset)){
                    editTextForgotPasswordEmail.setError("Invalid Email",errorDrawable);
                }
                else{
//                    send email with reset lind through resetPassword() method
                    resetPassword(emailForReset);
                }

            }
        });

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


    private void resetPassword(String emailForSendLink){

        auth.sendPasswordResetEmail(emailForSendLink)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ForgotPasswordActivity", "Email sent for reset Password.");
                            Toast.makeText(ForgotPasswordActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(ForgotPasswordActivity.this, Objects.requireNonNull(task.getException()).getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}