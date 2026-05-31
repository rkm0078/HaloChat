package com.rishabh.chatapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    TextView usernameText, emailText;

    LinearLayout checkUpdateBtn;
    LinearLayout logoutBtn;

    LinearLayout aboutBtn;

    LinearLayout changePasswordBtn;

    LinearLayout deleteBtn;

    Switch pushSwitch;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ImageView backBtn;
        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        // =========================
        // FIND VIEW BY ID
        // =========================

        usernameText =
                findViewById(R.id.usernameText);

        emailText =
                findViewById(R.id.emailText);

        logoutBtn =
                findViewById(R.id.logoutBtn);

        changePasswordBtn =
                findViewById(R.id.changePasswordBtn);

        deleteBtn =
                findViewById(R.id.deleteBtn);

        pushSwitch =
                findViewById(R.id.pushSwitch);
        aboutBtn =
                findViewById(R.id.aboutBtn);

        // =========================
        // USER INFO
        // =========================

        if (auth.getCurrentUser() != null) {

            String email =
                    auth.getCurrentUser().getEmail();

            emailText.setText(email);

            String username =
                    email.split("@")[0];

            usernameText.setText(username);
        }

        // =========================
        // LOGOUT
        // =========================

        logoutBtn.setOnClickListener(v -> {

            auth.signOut();

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finishAffinity();
        });

        // =========================
        // CHANGE PASSWORD
        // =========================

        changePasswordBtn.setOnClickListener(v -> {

            if (auth.getCurrentUser() != null) {

                auth.sendPasswordResetEmail(
                        auth.getCurrentUser().getEmail()
                );

                Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        // =========================
        // DELETE ACCOUNT
        // =========================

        deleteBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle("Delete Account");

            builder.setMessage(
                    "Are you sure?"
            );

            builder.setPositiveButton(
                    "Delete",
                    (dialog, which) -> {

                        String uid =
                                auth.getUid();

                        // DELETE FROM DATABASE

                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(uid)
                                .removeValue();

                        // DELETE AUTH ACCOUNT

                        if (auth.getCurrentUser() != null) {

                            auth.getCurrentUser()
                                    .delete()

                                    .addOnCompleteListener(task -> {

                                        Toast.makeText(
                                                this,
                                                "Account deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent =
                                                new Intent(
                                                        SettingsActivity.this,
                                                        RegisterActivity.class
                                                );

                                        startActivity(intent);

                                        finishAffinity();
                                    });
                        }
                    });

            builder.setNegativeButton(
                    "Cancel",
                    null
            );

            builder.show();
        });

        // =========================
        // PUSH SWITCH
        // =========================

        pushSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        Toast.makeText(
                                this,
                                "Notifications ON",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Notifications OFF",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

        // =========================
        // About button
        // =========================

        aboutBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            AboutActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // Check Update
        // =========================

        checkUpdateBtn =
                findViewById(R.id.checkUpdateBtn);

        checkUpdateBtn.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "HaloChat is up to date",
                    Toast.LENGTH_SHORT
            ).show();
        });

        backBtn =
                findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {

            finish();
        });
    }
}