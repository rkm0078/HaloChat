package com.rishabh.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;

    ImageView passwordToggle;

    TextView forgotPassword;

    MaterialButton loginBtn;

    TextView goRegister;

    FirebaseAuth auth;

    private static final int RC_SIGN_IN = 1001;

    private GoogleSignInClient googleSignInClient;

    private String verificationId;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    private CountDownTimer countDownTimer;

    LinearLayout emailLayout;
    LinearLayout phoneLayout;
    LinearLayout otpLayout;

    TextView emailTab;
    TextView phoneTab;

    EditText phoneNumber;
    EditText otpEditText;

    TextView timerText;
    TextView resendOtpBtn;

    Button sendOtpBtn;
    Button verifyOtpBtn;

    MaterialButton googleBtn;

    TabLayout loginTabLayout;

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

        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);

        otpLayout = findViewById(R.id.otpLayout);

        phoneNumber = findViewById(R.id.phoneNumber);
        otpEditText = findViewById(R.id.otpEditText);

        timerText = findViewById(R.id.timerText);
        resendOtpBtn = findViewById(R.id.resendOtpBtn);

        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);

        googleBtn = findViewById(R.id.googleBtn);

        loginTabLayout = findViewById(R.id.loginTabLayout);

        // TAB LAYOUT

        loginTabLayout.addTab(
                loginTabLayout.newTab().setText("EMAIL"));

        loginTabLayout.addTab(
                loginTabLayout.newTab().setText("PHONE"));

        loginTabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(
                            TabLayout.Tab tab) {

                        if (tab.getPosition() == 0) {

                            emailLayout.setVisibility(View.VISIBLE);
                            phoneLayout.setVisibility(View.GONE);

                        } else {

                            View dialogView = getLayoutInflater()
                                    .inflate(
                                            R.layout.dialog_coming_soon,
                                            null
                                    );

                            AlertDialog dialog =
                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setView(dialogView)
                                            .create();

                            dialog.getWindow()
                                    .setBackgroundDrawable(
                                            new ColorDrawable(
                                                    Color.TRANSPARENT
                                            )
                                    );

                            TextView messageText =
                                    dialogView.findViewById(
                                            R.id.messageText
                                    );

                            messageText.setText(
                                    "Phone Authentication will be available in future update.\n\n" +
                                            "Use Email Login or Google Sign-In for now."
                            );

                            dialogView.findViewById(
                                    R.id.okBtn
                            ).setOnClickListener(v ->
                                    dialog.dismiss()
                            );

                            dialog.show();

                            loginTabLayout.selectTab(
                                    loginTabLayout.getTabAt(0)
                            );

                            emailLayout.setVisibility(View.VISIBLE);
                            phoneLayout.setVisibility(View.GONE);
                            otpLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onTabUnselected(
                            TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(
                            TabLayout.Tab tab) {
                    }
                });

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

                                        showHaloDialog(
                                                "Email Not Verified",
                                                "Please verify your email first.\n\nCheck Inbox or Spam folder.",
                                                "OK",
                                                null
                                        );

                                        return;
                                    }

                                    loginBtn.setEnabled(true);

                                    loginBtn.setText("Log In");

                                    Toast.makeText(
                                            LoginActivity.this,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    FirebaseDatabase.getInstance()
                                            .getReference("Users")
                                            .child(authResult.getUser().getUid())
                                            .get()
                                            .addOnSuccessListener(snapshot -> {

                                                Intent intent;

                                                if (snapshot.exists()) {

                                                    intent =
                                                            new Intent(
                                                                    LoginActivity.this,
                                                                    HomeActivity.class
                                                            );

                                                } else {

                                                    intent =
                                                            new Intent(
                                                                    LoginActivity.this,
                                                                    CompleteProfileActivity.class
                                                            );

                                                    intent.putExtra(
                                                            "fullName",
                                                            authResult.getUser().getDisplayName()
                                                    );

                                                    intent.putExtra(
                                                            "email",
                                                            authResult.getUser().getEmail()
                                                    );
                                                }

                                                intent.setFlags(
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                );

                                                startActivity(intent);
                                                finish();
                                            });
                                });
                    })

                    .addOnFailureListener(err -> {

                        loginBtn.setEnabled(true);

                        loginBtn.setText("Log In");

                        if (err.getMessage() != null &&
                                err.getMessage().contains("INVALID_LOGIN_CREDENTIALS")) {

                            showHaloDialog(
                                    "Account Not Found",
                                    "No account exists with this email or password is incorrect.",
                                    "REGISTER",
                                    () -> startActivity(
                                            new Intent(
                                                    LoginActivity.this,
                                                    RegisterActivity.class
                                            )
                                    )
                            );

                        } else {
                            showHaloDialog(
                                    "Login Failed",
                                    err.getMessage(),
                                    "OK",
                                    null
                            );
                        }
                    });
        });

        // GOOGLE SIGN IN

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(
                                getString(
                                        R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient =
                GoogleSignIn.getClient(this, gso);

        // GOOGLE BUTTON

        googleBtn.setOnClickListener(v -> {

            Intent signInIntent =
                    googleSignInClient.getSignInIntent();

            startActivityForResult(
                    signInIntent,
                    RC_SIGN_IN
            );

        });

        // SEND OTP BUTTON

        sendOtpBtn.setOnClickListener(v -> {

            String phone =
                    phoneNumber.getText()
                            .toString()
                            .trim();

            phone = phone.replace(" ", "");

            if (phone.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter phone number",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!phone.startsWith("+91")) {
                phone = "+91" + phone;
            }

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(callbacks)
                            .build();

            PhoneAuthProvider.verifyPhoneNumber(options);

        });

        // VERIFY OTP BUTTON

        verifyOtpBtn.setOnClickListener(v -> {

            String code =
                    otpEditText.getText()
                            .toString()
                            .trim();

            PhoneAuthCredential credential =
                    PhoneAuthProvider
                            .getCredential(
                                    verificationId,
                                    code
                            );

            signInWithPhoneCredential(
                    credential
            );

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

    // OTP VERIFICATION

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(
                        PhoneAuthCredential credential) {

                    signInWithPhoneCredential(
                            credential
                    );
                }

                @Override
                public void onVerificationFailed(
                        com.google.firebase.FirebaseException e) {

                    Toast.makeText(
                            LoginActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }

                @Override
                public void onCodeSent(
                        String id,
                        PhoneAuthProvider
                                .ForceResendingToken token) {

                    verificationId = id;

                    resendToken = token;

                    otpLayout.setVisibility(View.VISIBLE);

                    startOtpTimer();

                    Toast.makeText(
                            LoginActivity.this,
                            "OTP Sent",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            };

    private void signInWithPhoneCredential(
            PhoneAuthCredential credential){

        auth.signInWithCredential(credential)

                .addOnSuccessListener(result -> {

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    HomeActivity.class
                            );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            LoginActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void startOtpTimer(){

        countDownTimer =
                new CountDownTimer(
                        60000,
                        1000) {

                    @Override
                    public void onTick(
                            long millisUntilFinished) {

                        timerText.setText(
                                String.valueOf(
                                        millisUntilFinished
                                                /1000)
                        );
                    }

                    @Override
                    public void onFinish() {

                        resendOtpBtn.setEnabled(true);

                        resendOtpBtn.setText(
                                "Resend OTP"
                        );
                    }
                };

        countDownTimer.start();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if(requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task =
                    GoogleSignIn
                            .getSignedInAccountFromIntent(
                                    data);

            try {

                GoogleSignInAccount account =
                        task.getResult(
                                ApiException.class);

                firebaseAuthWithGoogle(
                        account.getIdToken()
                );

            } catch (Exception e){

                Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(
            String idToken
    ) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(
                        idToken,
                        null
                );

        auth.signInWithCredential(
                        credential
                )

                .addOnSuccessListener(authResult -> {

                    FirebaseUser firebaseUser =
                            auth.getCurrentUser();

                    if (firebaseUser == null) {
                        return;
                    }

                    FirebaseDatabase
                            .getInstance()
                            .getReference("Users")
                            .child(firebaseUser.getUid())
                            .addListenerForSingleValueEvent(
                                    new ValueEventListener() {

                                        @Override
                                        public void onDataChange(
                                                @NonNull DataSnapshot snapshot
                                        ) {

                                            if (snapshot.exists()) {

                                                Intent intent =
                                                        new Intent(
                                                                LoginActivity.this,
                                                                HomeActivity.class
                                                        );

                                                intent.addFlags(
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                );

                                                startActivity(intent);

                                                finish();

                                            } else {

                                                Intent intent =
                                                        new Intent(
                                                                LoginActivity.this,
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

                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(
                                                @NonNull DatabaseError error
                                        ) {

                                            Toast.makeText(
                                                    LoginActivity.this,
                                                    error.getMessage(),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });

                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            LoginActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

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

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(
                            Color.TRANSPARENT
                    )
            );
        }

        TextView titleText =
                dialogView.findViewById(
                        R.id.titleText
                );

        TextView messageText =
                dialogView.findViewById(
                        R.id.messageText
                );

        MaterialButton okBtn =
                dialogView.findViewById(
                        R.id.okBtn
                );

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


    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser user =
                FirebaseAuth.getInstance()
                        .getCurrentUser();

        if (user == null) {
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {

                    Intent intent;

                    if (snapshot.exists()) {

                        intent =
                                new Intent(
                                        LoginActivity.this,
                                        HomeActivity.class
                                );

                    } else {

                        intent =
                                new Intent(
                                        LoginActivity.this,
                                        CompleteProfileActivity.class
                                );

                        intent.putExtra(
                                "fullName",
                                user.getDisplayName()
                        );

                        intent.putExtra(
                                "email",
                                user.getEmail()
                        );
                    }

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                });
    }
}