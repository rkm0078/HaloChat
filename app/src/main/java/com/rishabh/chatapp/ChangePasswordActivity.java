package com.rishabh.chatapp;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity
        extends AppCompatActivity {

    EditText currentPassword;
    EditText newPassword;
    EditText confirmPassword;

    ImageView currentEye;
    ImageView newEye;
    ImageView confirmEye;

    Button changeBtn;

    TextView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_change_password
        );

        currentPassword =
                findViewById(R.id.currentPassword);

        newPassword =
                findViewById(R.id.newPassword);

        confirmPassword =
                findViewById(R.id.confirmPassword);

        currentEye =
                findViewById(R.id.currentEye);

        newEye =
                findViewById(R.id.newEye);

        confirmEye =
                findViewById(R.id.confirmEye);

        changeBtn =
                findViewById(R.id.changeBtn);

        backBtn =
                findViewById(R.id.backBtn);

        setupPasswordToggle(
                currentPassword,
                currentEye
        );

        setupPasswordToggle(
                newPassword,
                newEye
        );

        setupPasswordToggle(
                confirmPassword,
                confirmEye
        );

        backBtn.setOnClickListener(v ->
                finish());

        changeBtn.setOnClickListener(v -> {

            FirebaseUser user =
                    FirebaseAuth.getInstance()
                            .getCurrentUser();

            if (user == null) {

                Toast.makeText(
                        this,
                        "User not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String current =
                    currentPassword.getText()
                            .toString()
                            .trim();

            String newPass =
                    newPassword.getText()
                            .toString()
                            .trim();

            String confirm =
                    confirmPassword.getText()
                            .toString()
                            .trim();

            if (current.isEmpty()
                    || newPass.isEmpty()
                    || confirm.isEmpty()) {

                Toast.makeText(
                        this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (newPass.length() < 6) {

                Toast.makeText(
                        this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!newPass.equals(confirm)) {

                Toast.makeText(
                        this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (current.equals(newPass)) {

                Toast.makeText(
                        this,
                        "New password must be different",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            changeBtn.setEnabled(false);

            changeBtn.setText("Updating...");

            AuthCredential credential =
                    EmailAuthProvider.getCredential(
                            user.getEmail(),
                            current
                    );

            user.reauthenticate(credential)

                    .addOnSuccessListener(unused ->

                            user.updatePassword(newPass)

                                    .addOnSuccessListener(unused2 -> {

                                        changeBtn.setEnabled(true);

                                        changeBtn.setText(
                                                "Update Password"
                                        );

                                        Toast.makeText(
                                                this,
                                                "Password changed successfully",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        finish();
                                    })

                                    .addOnFailureListener(error -> {

                                        changeBtn.setEnabled(true);

                                        changeBtn.setText(
                                                "Update Password"
                                        );

                                        Toast.makeText(
                                                this,
                                                error.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    })
                    )

                    .addOnFailureListener(error -> {

                        changeBtn.setEnabled(true);

                        changeBtn.setText(
                                "Update Password"
                        );

                        Toast.makeText(
                                this,
                                "Current password is incorrect",
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }

    private void setupPasswordToggle(
            EditText editText,
            ImageView eye
    ) {

        final boolean[] visible = {false};

        eye.setOnClickListener(v -> {

            if (visible[0]) {

                editText.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                eye.setImageResource(
                        R.drawable.ic_eye
                );

            } else {

                editText.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                eye.setImageResource(
                        R.drawable.ic_eye_off
                );
            }

            visible[0] = !visible[0];

            editText.setSelection(
                    editText.getText().length()
            );
        });
    }
}