package com.rishabh.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
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
    CircleImageView profileImage;
    ProgressBar uploadProgress;
   ImageView changePhotoBtn;
    TextView emailText;
    EditText nameEdit;
    EditText usernameEdit;
    EditText bioEdit;

    ImageView saveBtn;

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

        nameEdit =
                findViewById(R.id.nameEdit);

        usernameEdit =
                findViewById(R.id.usernameEdit);

        bioEdit =
                findViewById(R.id.bioEdit);


        emailText =
                findViewById(R.id.emailText);

        saveBtn =
                findViewById(R.id.saveBtn);

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

                                nameEdit.setText(user.getFullName());

                                usernameEdit.setText(user.username);

                                bioEdit.setText(user.bio);

                                if (user.bio != null) {

                                    bioEdit.setText(
                                            user.bio
                                    );
                                }

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

        saveBtn.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Profile Saved",
                    Toast.LENGTH_SHORT
            ).show();

            HashMap<String,Object> updates =
                    new HashMap<>();

            updates.put(
                    "firstName",
                    nameEdit.getText().toString()
            );

            updates.put(
                    "username",
                    usernameEdit.getText().toString()
            );

            updates.put(
                    "bio",
                    bioEdit.getText().toString()
            );
            String currentUid =
                    FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid();
            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .updateChildren(updates);
        });

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