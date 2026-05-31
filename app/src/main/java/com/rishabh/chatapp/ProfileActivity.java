package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView emailText;

    EditText usernameEdit;
    EditText passwordEdit;

    Button updateUsernameBtn;
    Button updatePasswordBtn;
    Button logoutBtn;
    Button deleteBtn;

    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailText =
                findViewById(R.id.emailText);

//        usernameEdit =
//                findViewById(R.id.usernameEdit);
//
//        passwordEdit =
//                findViewById(R.id.passwordEdit);
//
//        updateUsernameBtn =
//                findViewById(R.id.updateUsernameBtn);
//
//        updatePasswordBtn =
//                findViewById(R.id.updatePasswordBtn);

        logoutBtn =
                findViewById(R.id.logoutBtn);

        deleteBtn =
                findViewById(R.id.deleteBtn);

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance()
                .getReference("Users");

        FirebaseUser user =
                auth.getCurrentUser();

        String uid = user.getUid();

        // 🔥 LOAD USER DATA

        db.child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                User u =
                                        snapshot.getValue(User.class);

                                if (u != null) {

                                    usernameEdit.setText(u.username);

                                    emailText.setText(u.email);
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                            }
                        });

        // 🔥 UPDATE USERNAME

        updateUsernameBtn.setOnClickListener(v -> {

            String newUsername =
                    usernameEdit.getText()
                            .toString()
                            .trim();

            HashMap<String, Object> map =
                    new HashMap<>();

            map.put("username", newUsername);

            db.child(uid)
                    .updateChildren(map)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Username Updated",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

        // 🔥 UPDATE PASSWORD

        updatePasswordBtn.setOnClickListener(v -> {

            String newPassword =
                    passwordEdit.getText()
                            .toString()
                            .trim();

            if (newPassword.length() < 6) {

                Toast.makeText(
                        this,
                        "Password too short",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            user.updatePassword(newPassword)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Password Updated",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });

        // 🔥 LOGOUT

        logoutBtn.setOnClickListener(v -> {

            auth.signOut();

            startActivity(
                    new Intent(
                            this,
                            LoginActivity.class
                    )
            );

            finish();
        });

        // 🔥 DELETE ACCOUNT

        deleteBtn.setOnClickListener(v -> {

            db.child(uid).removeValue();

            user.delete()
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Account Deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        this,
                                        LoginActivity.class
                                )
                        );

                        finish();
                    });
        });
    }
}