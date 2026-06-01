package com.rishabh.chatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity
        extends AppCompatActivity {

    EditText inputField;

    Button sendBtn;

    TextView backLogin;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_forgot_password
        );

        inputField =
                findViewById(R.id.inputField);

        sendBtn =
                findViewById(R.id.sendBtn);

        backLogin =
                findViewById(R.id.backLogin);

        auth = FirebaseAuth.getInstance();

        backLogin.setOnClickListener(v ->
                finish());

        sendBtn.setOnClickListener(v -> {

            String input =
                    inputField.getText()
                            .toString()
                            .trim();

            if (TextUtils.isEmpty(input)) {

                Toast.makeText(
                        this,
                        "Enter email or username",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            sendBtn.setEnabled(false);

            sendBtn.setText("Sending...");

            // EMAIL

            if (input.contains("@")) {

                sendResetEmail(input);

            } else {

                // USERNAME

                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .orderByChild("username")
                        .equalTo(input)
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {

                                    @Override
                                    public void onDataChange(
                                            @NonNull DataSnapshot snapshot) {

                                        if (!snapshot.exists()) {

                                            resetButton();

                                            Toast.makeText(
                                                    ForgotPasswordActivity.this,
                                                    "Username not found",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            return;
                                        }

                                        for (DataSnapshot data :
                                                snapshot.getChildren()) {

                                            String email =
                                                    data.child("email")
                                                            .getValue(String.class);

                                            if (email != null) {

                                                sendResetEmail(email);

                                                return;
                                            }
                                        }

                                        resetButton();

                                        Toast.makeText(
                                                ForgotPasswordActivity.this,
                                                "Email not found",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    @Override
                                    public void onCancelled(
                                            @NonNull DatabaseError error) {

                                        resetButton();

                                        Toast.makeText(
                                                ForgotPasswordActivity.this,
                                                error.getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
            }
        });
    }

    private void sendResetEmail(String email) {

        auth.sendPasswordResetEmail(email)

                .addOnSuccessListener(unused -> {

                    resetButton();

                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Password reset link sent",
                            Toast.LENGTH_LONG
                    ).show();
                })

                .addOnFailureListener(error -> {

                    resetButton();

                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void resetButton() {

        sendBtn.setEnabled(true);

        sendBtn.setText("Send Link");
    }
}