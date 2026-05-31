package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText username;
    EditText email;
    EditText password;

    Button registerBtn;

    TextView loginText;

    ProgressBar progressBar;

    FirebaseAuth auth;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // =========================
        // FIND VIEWS
        // =========================

        firstName =
                findViewById(R.id.firstName);

        lastName =
                findViewById(R.id.lastName);

        username =
                findViewById(R.id.username);

        email =
                findViewById(R.id.email);

        password =
                findViewById(R.id.password);

        registerBtn =
                findViewById(R.id.registerBtn);

        loginText =
                findViewById(R.id.loginText);

        progressBar =
                findViewById(R.id.progressBar);

        // =========================
        // FIREBASE
        // =========================

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseDatabase.getInstance()
                        .getReference("Users");

        // =========================
        // REGISTER BUTTON
        // =========================

        registerBtn.setOnClickListener(v -> {

            String f =
                    firstName.getText()
                            .toString()
                            .trim();

            String l =
                    lastName.getText()
                            .toString()
                            .trim();

            String u =
                    username.getText()
                            .toString()
                            .trim();

            String e =
                    email.getText()
                            .toString()
                            .trim();

            String p =
                    password.getText()
                            .toString()
                            .trim();

            // VALIDATION

            if (f.isEmpty()
                    || l.isEmpty()
                    || u.isEmpty()
                    || e.isEmpty()
                    || p.isEmpty()) {

                Toast.makeText(
                        this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (p.length() < 6) {

                Toast.makeText(
                        this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // LOADING

            progressBar.setVisibility(View.VISIBLE);

            registerBtn.setEnabled(false);

            registerBtn.setText("Creating...");

            // FIREBASE REGISTER

            auth.createUserWithEmailAndPassword(e, p)

                    .addOnSuccessListener(authResult -> {

                        String uid =
                                auth.getCurrentUser()
                                        .getUid();

                        User user =
                                new User(
                                        uid,
                                        f,
                                        l,
                                        u,
                                        e,
                                        "",
                                        "Online",
                                        "",
                                        0
                                );

                        db.child(uid)
                                .setValue(user)

                                .addOnSuccessListener(unused -> {

                                    progressBar.setVisibility(View.GONE);

                                    registerBtn.setEnabled(true);

                                    registerBtn.setText("Sign Up");

                                    Toast.makeText(
                                            this,
                                            "Registration Successful",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    Intent intent =
                                            new Intent(
                                                    RegisterActivity.this,
                                                    HomeActivity.class
                                            );

                                    startActivity(intent);

                                    finish();
                                });
                    })

                    .addOnFailureListener(err -> {

                        progressBar.setVisibility(View.GONE);

                        registerBtn.setEnabled(true);

                        registerBtn.setText("Sign Up");

                        String message =
                                err.getMessage();

                        if (message != null
                                && message.contains("already")) {

                            Toast.makeText(
                                    this,
                                    "Email already exists",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {

                            Toast.makeText(
                                    this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        // =========================
        // LOGIN TEXT
        // =========================

        loginText.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finish();
        });
    }
}