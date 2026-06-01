package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;

    ImageView passwordToggle;

    TextView forgotPassword;

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

        passwordToggle =
                findViewById(R.id.passwordToggle);

        forgotPassword =
                findViewById(R.id.forgotPassword);

        loginBtn =
                findViewById(R.id.loginBtn);

        goRegister =
                findViewById(R.id.goRegister);

        auth =
                FirebaseAuth.getInstance();

        // PASSWORD TOGGLE

        final boolean[] passwordVisible = {false};

        passwordToggle.setOnClickListener(v -> {

            if (passwordVisible[0]) {

                password.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                passwordToggle.setImageResource(
                        R.drawable.ic_eye
                );

            } else {

                password.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                passwordToggle.setImageResource(
                        R.drawable.ic_eye_off
                );
            }

            passwordVisible[0] =
                    !passwordVisible[0];

            password.setSelection(
                    password.getText().length()
            );
        });

        // FORGOT PASSWORD

        forgotPassword.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            LoginActivity.this,
                            ForgotPasswordActivity.class
                    )
            );
        });

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

                        authResult.getUser()
                                .reload()
                                .addOnSuccessListener(unused -> {

                                    if (!authResult.getUser()
                                            .isEmailVerified()) {

                                        FirebaseAuth.getInstance()
                                                .signOut();

                                        loginBtn.setEnabled(true);

                                        loginBtn.setText("Log In");

                                        Toast.makeText(
                                                LoginActivity.this,
                                                "Please verify your email first. Check Inbox or Spam folder.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }

                                    loginBtn.setEnabled(true);

                                    loginBtn.setText("Log In");

                                    Toast.makeText(
                                            LoginActivity.this,
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
                                });
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

        if (FirebaseAuth.getInstance()
                .getCurrentUser() != null) {

            FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .reload()
                    .addOnSuccessListener(unused -> {

                        if (FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .isEmailVerified()) {

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            HomeActivity.class
                                    )
                            );

                            finish();
                        } else {

                            FirebaseAuth.getInstance()
                                    .signOut();
                        }
                    });
        }
    }
}