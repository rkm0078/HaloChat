package com.rishabh.chatapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView usernameText, emailText;

    LinearLayout checkUpdateBtn;
    LinearLayout logoutBtn;

    LinearLayout aboutBtn;

    LinearLayout changePasswordBtn;

    LinearLayout deleteBtn;

    Switch pushSwitch;

    LinearLayout profileSettingsBtn;

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

        profileImage =
                findViewById(R.id.profileImage);

        profileSettingsBtn =
                findViewById(R.id.profileSettingsBtn);

        usernameText =
                findViewById(R.id.usernameText);

        emailText =
                findViewById(R.id.emailText);

        checkUpdateBtn =
                findViewById(R.id.checkUpdateBtn);

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

        String uid = auth.getUid();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                User user =
                                        snapshot.getValue(
                                                User.class
                                        );

                                if (user == null)
                                    return;

                                Glide.with(
                                                SettingsActivity.this
                                        )
                                        .load(user.profileImage)
                                        .placeholder(
                                                R.drawable.default_profile
                                        )
                                        .error(
                                                R.drawable.default_profile
                                        )
                                        .into(profileImage);
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
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
        // PROFILE SETTINGS
        // =========================

        profileSettingsBtn.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            SettingsActivity.this,
                            ProfileActivity.class
                    )
            );

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

            builder.setMessage("Are you sure?");

            builder.setPositiveButton(
                    "Delete",
                    (dialog, which) -> {

                        if (auth.getCurrentUser() != null) {

                            String deleteUid =
                                    auth.getCurrentUser().getUid();

                            FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(deleteUid)
                                    .removeValue();

                            auth.getCurrentUser()
                                    .delete()
                                    .addOnCompleteListener(task -> {

                                        Toast.makeText(
                                                SettingsActivity.this,
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

        checkUpdateBtn.setOnClickListener(v -> {

            String updateUrl =
                    "https://github.com/rkm0078/HaloChat/releases/latest";

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            android.net.Uri.parse(updateUrl)
                    );

            startActivity(intent);
        });

        backBtn =
                findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {

            finish();
        });
    }
}