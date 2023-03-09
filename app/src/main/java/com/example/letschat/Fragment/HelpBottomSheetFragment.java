package com.example.letschat.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letschat.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class HelpBottomSheetFragment extends BottomSheetDialogFragment {



    public HelpBottomSheetFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_bottom_sheet, container, false);

        EditText editTextMessage = view.findViewById(R.id.editTextHelpMessage);
        Button buttonSend = view.findViewById(R.id.buttonSendHelpMessage);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextMessage.getText().toString();

                if (message.isEmpty()) {
                    editTextMessage.setError("Write something");
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "akshatdale@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Let's Chat");
                    intent.putExtra(Intent.EXTRA_TEXT, message);

                    //need this to prompts email client only
                    intent.setType("message/rfc822");
                    try {
                        startActivity(Intent.createChooser(intent, "Send Email"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}