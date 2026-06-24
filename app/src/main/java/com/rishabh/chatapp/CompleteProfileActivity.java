package com.rishabh.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class CompleteProfileActivity
        extends AppCompatActivity {

    private CircleImageView profileImage;
    private FrameLayout photoContainer;
    private ImageView backBtn;

    private EditText username;
    private EditText bio;

    private TextView usernameStatus;
    private TextView bioCount;

    private MaterialButton continueBtn;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    private Uri selectedImageUri;

    private String fullName;
    private String email;

    private EditText fullNameEdit;

    private final ActivityResultLauncher<Intent>
            imagePickerLauncher =
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

        setContentView(
                R.layout.activity_complete_profile
        );

        // =========================
        // FIND VIEWS
        // =========================

        profileImage = findViewById(R.id.profileImage);
        photoContainer = findViewById(R.id.photoContainer);
        backBtn = findViewById(R.id.backBtn);
        fullNameEdit =
                findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        usernameStatus =
                findViewById(R.id.usernameStatus);

        bioCount =
                findViewById(R.id.bioCount);

        continueBtn =
                findViewById(R.id.continueBtn);

        progressBar =
                findViewById(R.id.progressBar);

        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        usersRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference("Users");

        // =========================
        // GET DATA
        // =========================

        fullName =
                getIntent()
                        .getStringExtra("fullName");

        email =
                getIntent()
                        .getStringExtra("email");

        // =========================
        // AUTO USERNAME
        // =========================

        if (fullName != null) {

            fullNameEdit.setText(fullName);

            username.setText(
                    fullName
                            .toLowerCase()
                            .replace(" ", "_")
            );

            updateButtonState();
        }

        // =========================
        // FULL NAME
        // =========================

        fullNameEdit.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        updateButtonState();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                });

        // =========================
        // BIO COUNTER
        // =========================

        bio.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        int left =
                                160 - s.length();

                        bioCount.setText(
                                left + " characters left"
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                });

        // =========================
        // BACK BUTTON
        // =========================

        backBtn.setOnClickListener(v ->
                finish());

        // =========================
        // DISABLE BUTTON INITIALLY
        // =========================

        continueBtn.setEnabled(false);
        continueBtn.setAlpha(0.6f);

        // =========================
        // CONTINUE BUTTON
        // =========================

        continueBtn.setOnClickListener(v -> {

            saveProfile();

        });

        // =========================
        // USERNAME CHECK
        // =========================

        username.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        updateButtonState();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                });

        // =========================
        // PROFILE PHOTO
        // =========================

        photoContainer.setOnClickListener(v -> {

            photoContainer.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {

                        photoContainer.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100);

                        Intent intent =
                                new Intent(
                                        Intent.ACTION_PICK
                                );

                        intent.setType(
                                "image/*"
                        );

                        imagePickerLauncher.launch(
                                intent
                        );
                    });
        });
    }

    // =========================
    // UPDATE BUTTON STATE
    // =========================

    private void updateButtonState() {

        boolean valid =
                !fullNameEdit.getText()
                        .toString()
                        .trim()
                        .isEmpty()
                        &&
                        !username.getText()
                                .toString()
                                .trim()
                                .isEmpty();

        continueBtn.setEnabled(
                valid
        );

        continueBtn.setAlpha(
                valid ? 1f : 0.6f
        );
    }

    // =========================
    // SAVE PROFILE
    // =========================

    private void saveProfile() {

        String fullNameText =
                fullNameEdit.getText()
                        .toString()
                        .trim();

        if (fullNameText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter full name",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String usernameText =
                username.getText()
                        .toString()
                        .trim();

        String bioText =
                bio.getText()
                        .toString()
                        .trim();

        if (usernameText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter username",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        continueBtn.setEnabled(false);

        if (FirebaseAuth.getInstance()
                .getCurrentUser() == null) {

            finish();
            return;
        }

        String uid =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        if (selectedImageUri != null) {

            uploadToCloudinary(
                    selectedImageUri,
                    uid,
                    usernameText,
                    bioText
            );

        } else {

            saveUserToFirebase(
                    uid,
                    usernameText,
                    bioText,
                    "default"
            );
        }
    }

    // =========================
    // UPLOAD TO CLOUDINARY
    // =========================

    private void uploadToCloudinary(
            Uri imageUri,
            String uid,
            String username,
            String bio
    ) {

        String cloudName = "domvygmqx";

        String uploadPreset =
                "halochat_profiles";

        try {

            byte[] imageBytes =
                    getContentResolver()
                            .openInputStream(imageUri)
                            .readAllBytes();

            RequestBody requestBody =
                    new MultipartBody.Builder()
                            .setType(
                                    MultipartBody.FORM
                            )
                            .addFormDataPart(
                                    "file",
                                    "profile.jpg",
                                    RequestBody.create(
                                            imageBytes
                                    )
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
                    .enqueue(
                            new Callback() {

                                @Override
                                public void onFailure(
                                        Call call,
                                        IOException e
                                ) {

                                    runOnUiThread(() -> {

                                        progressBar.setVisibility(
                                                View.GONE
                                        );

                                        continueBtn.setEnabled(
                                                true
                                        );

                                        Toast.makeText(
                                                CompleteProfileActivity.this,
                                                "Upload Failed",
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
                                                        response.body()
                                                                .string()
                                                );

                                        String imageUrl =
                                                object.getString(
                                                        "secure_url"
                                                );

                                        saveUserToFirebase(
                                                uid,
                                                username,
                                                bio,
                                                imageUrl
                                        );

                                    } catch (Exception ex) {

                                        runOnUiThread(() -> {

                                            progressBar.setVisibility(
                                                    View.GONE
                                            );

                                            continueBtn.setEnabled(
                                                    true
                                            );

                                        });
                                    }
                                }
                            });

        } catch (Exception ex) {

            progressBar.setVisibility(
                    View.GONE
            );

            continueBtn.setEnabled(
                    true
            );
        }
    }

    // =========================
    // SAVE USER TO FIREBASE
    // =========================

    private void saveUserToFirebase(
            String uid,
            String usernameText,
            String bioText,
            String imageUrl
    ) {


        String enteredName =
                fullNameEdit.getText()
                        .toString()
                        .trim();

        String[] parts =
                enteredName.split(" ", 2);

        String firstName = "";
        String lastName = "";

        if (parts.length > 0)
            firstName = parts[0];

        if (parts.length > 1)
            lastName = parts[1];


        User user =
                new User(
                        uid,
                        firstName,
                        lastName,
                        usernameText,
                        email,
                        imageUrl,
                        "Online",
                        "",
                        0,
                        bioText
                );

        usersRef.child(uid)
                .setValue(user)
                .addOnSuccessListener(unused -> {

                    progressBar.setVisibility(
                            View.GONE
                    );

                    Intent intent =
                            new Intent(
                                    CompleteProfileActivity.this,
                                    HomeActivity.class
                            );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);

                    finish();
                })

                .addOnFailureListener(e -> {

                    progressBar.setVisibility(
                            View.GONE
                    );

                    continueBtn.setEnabled(
                            true
                    );

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

}