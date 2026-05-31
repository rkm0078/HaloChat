package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;

    Button loginBtn;

    TextView goRegister;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // BIND UI

        email =
                findViewById(R.id.email);

        password =
                findViewById(R.id.password);

        loginBtn =
                findViewById(R.id.loginBtn);

        goRegister =
                findViewById(R.id.goRegister);

        auth =
                FirebaseAuth.getInstance();

        // LOGIN BUTTON

        loginBtn.setOnClickListener(v -> {

            String e =
                    email.getText()
                            .toString()
                            .trim();

            String p =
                    password.getText()
                            .toString()
                            .trim();

            if (e.isEmpty() || p.isEmpty()) {

                Toast.makeText(
                        this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // LOADING EFFECT

            loginBtn.setEnabled(false);

            loginBtn.setText("Loading...");

            auth.signInWithEmailAndPassword(e, p)

                    .addOnSuccessListener(authResult -> {

                        loginBtn.setEnabled(true);

                        loginBtn.setText("Log In");

                        Toast.makeText(
                                this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        HomeActivity.class
                                )
                        );

                        finish();
                    })

                    .addOnFailureListener(err -> {

                        loginBtn.setEnabled(true);

                        loginBtn.setText("Log In");

                        Toast.makeText(
                                this,
                                err.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });

        // GO REGISTER

        goRegister.setOnClickListener(v ->

                startActivity(
                        new Intent(
                                LoginActivity.this,
                                RegisterActivity.class
                        )
                )
        );
    }

    @Override
    protected void onStart() {

        super.onStart();

        // AUTO LOGIN

        if (FirebaseAuth.getInstance()
                .getCurrentUser() != null) {

            startActivity(
                    new Intent(
                            LoginActivity.this,
                            HomeActivity.class
                    )
            );

            finish();
        }
    }
}