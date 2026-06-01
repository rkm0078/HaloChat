package com.rishabh.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView profileImage;

    LinearLayout changePhotoBtn;

    Uri selectedImageUri;

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

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            selectedImageUri =
                                    result.getData().getData();

                            profileImage.setImageURI(
                                    selectedImageUri
                            );
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // =========================
        // FIND VIEWS
        // =========================

        profileImage =
                findViewById(R.id.profileImage);

        changePhotoBtn =
                findViewById(R.id.changePhotoBtn);

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

        changePhotoBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");

            imagePickerLauncher.launch(intent);
        });

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
                        if (auth.getCurrentUser() == null) {

                            progressBar.setVisibility(View.GONE);

                            registerBtn.setEnabled(true);

                            registerBtn.setText("Sign Up");

                            return;
                        }

                        String uid =
                                auth.getCurrentUser()
                                        .getUid();

                        if (selectedImageUri != null) {

                            uploadToCloudinaryAndRegister(
                                    selectedImageUri,
                                    uid,
                                    f,
                                    l,
                                    u,
                                    e
                            );

                        } else {

                            User user =
                                    new User(
                                            uid,
                                            f,
                                            l,
                                            u,
                                            e,
                                            "default",
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
                                                RegisterActivity.this,
                                                "Registration Successful",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        startActivity(
                                                new Intent(
                                                        RegisterActivity.this,
                                                        HomeActivity.class
                                                )
                                        );

                                        finish();
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

    private void uploadToCloudinaryAndRegister(
            Uri imageUri,
            String uid,
            String f,
            String l,
            String u,
            String e
    ) {

        String cloudName = "domvygmqx";
        String uploadPreset = "halochat_profiles";

        try {

            byte[] imageBytes =
                    getContentResolver()
                            .openInputStream(imageUri)
                            .readAllBytes();

            RequestBody requestBody =
                    new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                    "file",
                                    "profile.jpg",
                                    RequestBody.create(imageBytes)
                            )
                            .addFormDataPart(
                                    "upload_preset",
                                    uploadPreset
                            )
                            .build();

            Request request =
                    new Request.Builder()
                            .url(
                                    "https://api.cloudinary.com/v1_1/"
                                            + cloudName
                                            + "/image/upload"
                            )
                            .post(requestBody)
                            .build();

            new OkHttpClient()
                    .newCall(request)
                    .enqueue(new Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException ex
                        ) {

                            runOnUiThread(() -> {

                                progressBar.setVisibility(View.GONE);

                                registerBtn.setEnabled(true);

                                registerBtn.setText("Sign Up");

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Image Upload Failed",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) throws IOException {

                            try {

                                JSONObject object =
                                        new JSONObject(
                                                response.body().string()
                                        );

                                String imageUrl =
                                        object.getString(
                                                "secure_url"
                                        );

                                User user =
                                        new User(
                                                uid,
                                                f,
                                                l,
                                                u,
                                                e,
                                                imageUrl,
                                                "Online",
                                                "",
                                                0
                                        );

                                db.child(uid)
                                        .setValue(user)
                                        .addOnSuccessListener(unused -> {

                                            runOnUiThread(() -> {

                                                progressBar.setVisibility(View.GONE);

                                                registerBtn.setEnabled(true);

                                                registerBtn.setText("Sign Up");

                                                Toast.makeText(
                                                        RegisterActivity.this,
                                                        "Registration Successful",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                startActivity(
                                                        new Intent(
                                                                RegisterActivity.this,
                                                                HomeActivity.class
                                                        )
                                                );

                                                finish();
                                            });
                                        });

                            } catch (Exception ex) {

                                runOnUiThread(() -> {

                                    progressBar.setVisibility(View.GONE);

                                    registerBtn.setEnabled(true);

                                    registerBtn.setText("Sign Up");

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            ex.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });
                            }
                        }
                    });

        } catch (Exception ex) {

            progressBar.setVisibility(View.GONE);

            registerBtn.setEnabled(true);

            registerBtn.setText("Sign Up");

            Toast.makeText(
                    this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}