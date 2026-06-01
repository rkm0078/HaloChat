package com.rishabh.chatapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;
    ImageView backBtn;
    ImageView profileImage;
    ProgressBar uploadProgress;
    LinearLayout changePhotoBtn;

    TextView fullName;
    TextView username;

    TextView fullNameDetail;
    TextView usernameDetail;

    TextView emailText;

    FirebaseAuth auth;
    DatabaseReference db;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            selectedImageUri =
                                    result.getData().getData();

                            Glide.with(ProfileActivity.this)
                                    .load(selectedImageUri)
                                    .into(profileImage);

                            uploadToCloudinary(selectedImageUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        backBtn =
                findViewById(R.id.backBtn);

        profileImage =
                findViewById(R.id.profileImage);

        uploadProgress =
                findViewById(R.id.uploadProgress);

        changePhotoBtn =
                findViewById(R.id.changePhotoBtn);

        fullName =
                findViewById(R.id.fullName);

        username =
                findViewById(R.id.username);

        fullNameDetail =
                findViewById(R.id.fullNameDetail);

        usernameDetail =
                findViewById(R.id.usernameDetail);

        emailText =
                findViewById(R.id.emailText);

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance()
                .getReference("Users");

        FirebaseUser firebaseUser =
                auth.getCurrentUser();

        if (firebaseUser == null) {

            finish();
            return;
        }

        String uid =
                firebaseUser.getUid();

        db.child(uid)
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

                                String name =
                                        user.getFullName();

                                fullName.setText(name);

                                fullNameDetail.setText(name);

                                username.setText(
                                        user.username
                                );

                                usernameDetail.setText(
                                        user.username
                                );

                                emailText.setText(
                                        user.email
                                );

                                Glide.with(ProfileActivity.this)
                                        .load(user.profileImage)
                                        .placeholder(R.drawable.default_profile)
                                        .error(R.drawable.default_profile)
                                        .into(profileImage);
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });

        backBtn.setOnClickListener(v -> finish());

        changePhotoBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");

            imagePickerLauncher.launch(intent);

        });
    }

    private void uploadToCloudinary(Uri imageUri) {

        uploadProgress.setVisibility(View.VISIBLE);
        changePhotoBtn.setAlpha(0.6f);
        changePhotoBtn.setEnabled(false);

        Toast.makeText(
                this,
                "Uploading profile photo...",
                Toast.LENGTH_SHORT
        ).show();

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

            OkHttpClient client =
                    new OkHttpClient();

            client.newCall(request)
                    .enqueue(new Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e
                        ) {

                            runOnUiThread(() -> {

                                uploadProgress.setVisibility(View.GONE);
                                changePhotoBtn.setAlpha(1f);
                                changePhotoBtn.setEnabled(true);

                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Upload Failed. Check internet connection.",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) throws IOException {

                            String json =
                                    response.body().string();

                            try {

                                JSONObject object =
                                        new JSONObject(json);

                                String imageUrl =
                                        object.getString(
                                                "secure_url"
                                        );

                                FirebaseUser user =
                                        FirebaseAuth
                                                .getInstance()
                                                .getCurrentUser();

                                if (user == null) {

                                    runOnUiThread(() -> {

                                        uploadProgress.setVisibility(View.GONE);
                                        changePhotoBtn.setAlpha(1f);
                                        changePhotoBtn.setEnabled(true);
                                    });

                                    return;
                                }

                                FirebaseDatabase.getInstance()
                                        .getReference("Users")
                                        .child(user.getUid())
                                        .child("profileImage")
                                        .setValue(imageUrl);

                                runOnUiThread(() -> {

                                    uploadProgress.setVisibility(View.GONE);
                                    changePhotoBtn.setAlpha(1f);

                                    Glide.with(ProfileActivity.this)
                                            .load(imageUrl)
                                            .into(profileImage);

                                    changePhotoBtn.setEnabled(true);

                                    Toast.makeText(
                                            ProfileActivity.this,
                                            "Profile Updated",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });

                            } catch (Exception e) {

                                e.printStackTrace();

                                runOnUiThread(() -> {

                                    uploadProgress.setVisibility(View.GONE);
                                    changePhotoBtn.setAlpha(1f);
                                    changePhotoBtn.setEnabled(true);

                                    Toast.makeText(
                                            ProfileActivity.this,
                                            "JSON Error",
                                            Toast.LENGTH_LONG
                                    ).show();
                                });
                            }
                        }
                    });

        } catch (Exception e) {

            e.printStackTrace();
            uploadProgress.setVisibility(View.GONE);
            changePhotoBtn.setAlpha(1f);
            changePhotoBtn.setEnabled(true);
            Toast.makeText(
                    this,
                    "Image Error",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}