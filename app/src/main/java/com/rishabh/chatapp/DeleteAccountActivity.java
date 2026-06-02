package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {

    EditText deletePassword;

    ImageView passwordEye;

    MaterialButton deleteAccountBtn;

    TextView cancelBtn;

    ImageView backBtn;

    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delete_account);

        deletePassword =
                findViewById(R.id.deletePassword);

        passwordEye =
                findViewById(R.id.passwordEye);

        deleteAccountBtn =
                findViewById(R.id.deleteAccountBtn);

        cancelBtn =
                findViewById(R.id.cancelBtn);

        backBtn =
                findViewById(R.id.backBtn);

        // PASSWORD TOGGLE

        passwordEye.setOnClickListener(v -> {

            if (passwordVisible) {

                deletePassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                passwordEye.setImageResource(
                        R.drawable.ic_eye
                );

                passwordVisible = false;

            } else {

                deletePassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                passwordEye.setImageResource(
                        R.drawable.ic_eye_off
                );

                passwordVisible = true;
            }

            deletePassword.setSelection(
                    deletePassword.getText().length()
            );
        });

        // BACK

        backBtn.setOnClickListener(v -> finish());

        cancelBtn.setOnClickListener(v -> finish());

        // DELETE ACCOUNT

        deleteAccountBtn.setOnClickListener(v -> {

            String password =
                    deletePassword.getText()
                            .toString()
                            .trim();

            if (password.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter your password",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage(
                            "This action cannot be undone."
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) ->
                                    deleteUser(password)
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .show();
        });
    }

    private void deleteUser(String password) {

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (user == null) return;

        AuthCredential credential =
                EmailAuthProvider.getCredential(
                        user.getEmail(),
                        password
                );

        user.reauthenticate(credential)

                .addOnSuccessListener(unused -> {

                    String uid = user.getUid();

                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Users")
                            .child(uid)
                            .removeValue()

                            .addOnSuccessListener(unused1 -> {
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("Friends")
                                        .child(uid)
                                        .removeValue();

                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("FriendRequests")
                                        .child(uid)
                                        .removeValue();
                                user.delete()

                                        .addOnSuccessListener(unused2 -> {

                                            FirebaseAuth
                                                    .getInstance()
                                                    .signOut();

                                            Toast.makeText(
                                                    this,
                                                    "Account deleted",
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            Intent intent =
                                                    new Intent(
                                                            DeleteAccountActivity.this,
                                                            LoginActivity.class
                                                    );

                                            intent.addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            );

                                            startActivity(intent);

                                            finish();
                                        })

                                        .addOnFailureListener(e ->

                                                Toast.makeText(
                                                        this,
                                                        e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show()
                                        );
                            });
                })

                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                "Wrong password",
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
}