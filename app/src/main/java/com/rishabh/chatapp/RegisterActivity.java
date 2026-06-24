package com.rishabh.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.common.api.ApiException;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    ImageView passwordToggle;

    MaterialButton registerBtn;

    TextView loginText;

    ProgressBar progressBar;

    FirebaseAuth auth;

    EditText fullName;
    EditText confirmPassword;

    MaterialButton googleBtn;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent>
            googleLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // =========================
        // FIND VIEWS
        // =========================


        fullName =
                findViewById(
                        R.id.fullName
                );

        confirmPassword =
                findViewById(
                        R.id.confirmPassword
                );

        email =
                findViewById(R.id.email);

        password =
                findViewById(R.id.password);

        passwordToggle =
                findViewById(R.id.passwordToggle);

        registerBtn =
                findViewById(R.id.registerBtn);

        googleBtn =
                findViewById(
                        R.id.googleBtn
                );

        loginText =
                findViewById(R.id.loginText);

        progressBar =
                findViewById(R.id.progressBar);

        // =========================
        // FIREBASE
        // =========================

        auth =
                FirebaseAuth.getInstance();

        // =========================
        // GOOGLE SIGN IN
        // =========================

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(
                                        R.string.default_web_client_id
                                )
                        )
                        .requestEmail()
                        .build();

        googleSignInClient =
                GoogleSignIn.getClient(
                        this,
                        gso
                );

        // =========================
        // PASSWORD TOGGLE
        // =========================

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

            passwordVisible[0] = !passwordVisible[0];

            password.setSelection(
                    password.getText().length()
            );
        });

        // =========================
        // REGISTER BUTTON
        // =========================

        registerBtn.setOnClickListener(v -> {

            String e =
                    email.getText()
                            .toString()
                            .trim();

            String p =
                    password.getText()
                            .toString()
                            .trim();

            // VALIDATION

            if (fullName.getText().toString().trim().isEmpty()
                    || email.getText().toString().trim().isEmpty()
                    || password.getText().toString().trim().isEmpty()
                    || confirmPassword.getText().toString().trim().isEmpty()) {

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

            // PASSWORD MATCH

            if (!password.getText().toString()
                    .equals(
                            confirmPassword.getText().toString()
                    )) {

                Toast.makeText(
                        this,
                        "Passwords do not match",
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
                        if (auth.getCurrentUser() == null) {

                            progressBar.setVisibility(View.GONE);

                            registerBtn.setEnabled(true);

                            registerBtn.setText("Sign Up");

                            return;
                        }

                        FirebaseUser firebaseUser =
                                FirebaseAuth.getInstance()
                                        .getCurrentUser();

                        if (firebaseUser != null) {

                            firebaseUser.sendEmailVerification()
                                    .addOnSuccessListener(unused -> {

                                        progressBar.setVisibility(View.GONE);

                                        registerBtn.setEnabled(true);

                                        registerBtn.setText("Create Account");

                                        showHaloDialog(
                                                "Verify Your Email",
                                                "A verification email has been sent.\n\nPlease verify your email and then login.",
                                                "GO TO LOGIN",
                                                () -> {

                                                    FirebaseAuth.getInstance()
                                                            .signOut();

                                                    Intent intent =
                                                            new Intent(
                                                                    RegisterActivity.this,
                                                                    LoginActivity.class
                                                            );

                                                    intent.setFlags(
                                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    );

                                                    startActivity(intent);

                                                    finish();
                                                }
                                        );
                                    })

                                    .addOnFailureListener(error -> {

                                        progressBar.setVisibility(View.GONE);

                                        registerBtn.setEnabled(true);

                                        registerBtn.setText("Create Account");

                                        Toast.makeText(
                                                RegisterActivity.this,
                                                error.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });
                        }

                    })

                    .addOnFailureListener(err -> {

                        progressBar.setVisibility(View.GONE);

                        registerBtn.setEnabled(true);

                        registerBtn.setText("Sign Up");

                        Toast.makeText(
                                RegisterActivity.this,
                                err.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });

        // =========================
        // GOOGLE BUTTON
        // =========================

        googleBtn.setOnClickListener(v -> {

            Intent signInIntent =
                    googleSignInClient
                            .getSignInIntent();

            googleLauncher.launch(
                    signInIntent
            );

        });

        // =========================
        // GOOGLE LAUNCHER
        // =========================

        googleLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            try {

                                GoogleSignInAccount account =
                                        GoogleSignIn
                                                .getSignedInAccountFromIntent(
                                                        result.getData()
                                                )
                                                .getResult(
                                                        ApiException.class
                                                );

                                firebaseAuthWithGoogle(
                                        account.getIdToken()
                                );

                            } catch (Exception e) {

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
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

    private void showHaloDialog(
            String title,
            String message,
            String buttonText,
            Runnable action
    ) {

        View dialogView =
                getLayoutInflater().inflate(
                        R.layout.dialog_coming_soon,
                        null
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        TextView titleText =
                dialogView.findViewById(R.id.titleText);

        TextView messageText =
                dialogView.findViewById(R.id.messageText);

        MaterialButton okBtn =
                dialogView.findViewById(R.id.okBtn);

        titleText.setText(title);
        messageText.setText(message);
        okBtn.setText(buttonText);

        okBtn.setOnClickListener(v -> {

            dialog.dismiss();

            if (action != null) {
                action.run();
            }
        });

        dialog.show();
    }

    private void firebaseAuthWithGoogle(
            String idToken
    ) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null
                );

        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {

                    FirebaseUser firebaseUser =
                            result.getUser();

                    String uid =
                            firebaseUser.getUid();

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(uid)
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                if (snapshot.exists()) {

                                    startActivity(
                                            new Intent(
                                                    RegisterActivity.this,
                                                    HomeActivity.class
                                            )
                                    );

                                    finish();

                                } else {

                                    Intent intent =
                                            new Intent(
                                                    RegisterActivity.this,
                                                    CompleteProfileActivity.class
                                            );

                                    intent.putExtra(
                                            "fullName",
                                            firebaseUser.getDisplayName()
                                    );

                                    intent.putExtra(
                                            "email",
                                            firebaseUser.getEmail()
                                    );

                                    intent.putExtra(
                                            "photo",
                                            firebaseUser.getPhotoUrl() != null
                                                    ? firebaseUser.getPhotoUrl().toString()
                                                    : ""
                                    );

                                    intent.putExtra(
                                            "googleLogin",
                                            true
                                    );

                                    startActivity(intent);

                                    finish();
                                }
                            });
                })

                .addOnFailureListener(error -> {

                    Toast.makeText(
                            this,
                            error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}