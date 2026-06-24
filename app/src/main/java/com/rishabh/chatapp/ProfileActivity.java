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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.chatapp.database.entity.UserEntity;
import com.rishabh.chatapp.database.repository.UserRepository;

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

    TextView statusText;

    MaterialButton saveBtn;

    FirebaseAuth auth;
    DatabaseReference db;
    TextView nameTitle;
    TextView usernameTitle;

    MaterialButton deleteBtn;

    private Uri selectedImageUri;

    private UserRepository userRepository;

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

        nameTitle = findViewById(R.id.nameTitle);
        usernameTitle = findViewById(R.id.usernameTitle);

        statusText = findViewById(R.id.statusText);

        deleteBtn = findViewById(R.id.deleteBtn);

        auth = FirebaseAuth.getInstance();

        userRepository = new UserRepository(this);

        db = FirebaseDatabase.getInstance()
                .getReference("Users");

        FirebaseUser firebaseUser =
                auth.getCurrentUser();


        if (firebaseUser != null) {

            emailText.setText(
                    firebaseUser.getEmail()
            );
        }

        if (firebaseUser == null) {

            finish();
            return;
        }

        String uid =
                firebaseUser.getUid();

        userRepository.getUser(uid).observe(this, entity -> {

            if (entity == null) {
                return;
            }

            String fullName = entity.firstName;

            if (entity.lastName != null &&
                    !entity.lastName.isEmpty()) {

                fullName += " " + entity.lastName;
            }

            nameEdit.setText(fullName);

            usernameEdit.setText(
                    entity.username
            );

            bioEdit.setText(
                    entity.bio
            );

            emailText.setText(
                    entity.email
            );

            Glide.with(ProfileActivity.this)
                    .load(entity.profileImage)
                    .placeholder(R.drawable.default_profile)
                    .into(profileImage);

        });

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

                                nameTitle.setText(user.getFullName());
                                usernameTitle.setText("@" + user.username);
                                statusText.setText(
                                        user.getSafeStatus()
                                );

                                if (user.email != null &&
                                        !user.email.isEmpty()) {

                                    emailText.setText(user.email);

                                } else {

                                    FirebaseUser firebaseUser =
                                            FirebaseAuth.getInstance()
                                                    .getCurrentUser();

                                    if (firebaseUser != null) {

                                        emailText.setText(
                                                firebaseUser.getEmail()
                                        );
                                    }
                                }

                                FirebaseUser firebaseUser =
                                        FirebaseAuth.getInstance()
                                                .getCurrentUser();

                                if (user.email != null &&
                                        !user.email.isEmpty()) {

                                    emailText.setText(user.email);

                                } else if (firebaseUser != null) {

                                    emailText.setText(
                                            firebaseUser.getEmail()
                                    );
                                }


                                UserEntity entity = new UserEntity();

                                entity.uid = user.uid;
                                entity.firstName = user.firstName;
                                entity.lastName = user.lastName;
                                entity.username = user.username;
                                entity.email = user.email;
                                entity.profileImage = user.profileImage;
                                entity.status = user.status;
                                entity.lastSeen = user.lastSeen;
                                entity.lastMessage = user.lastMessage;
                                entity.lastMessageTime = user.lastMessageTime;
                                entity.unreadCount = user.unreadCount;
                                entity.bio = user.bio;

                                userRepository.insertUser(entity);

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

            HashMap<String,Object> updates =
                    new HashMap<>();

            String fullName = nameEdit.getText().toString().trim();

            if (fullName.isEmpty()) {

                Toast.makeText(
                        ProfileActivity.this,
                        "Name cannot be empty",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String[] parts = fullName.split(" ", 2);

            String firstName = parts[0];

            String lastName = "";

            if (parts.length > 1) {
                lastName = parts[1];
            }

            updates.put("firstName", firstName);
            updates.put("lastName", lastName);

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
                    .updateChildren(updates)
                    .addOnSuccessListener(unused -> {

                        nameTitle.setText(fullName);

                        usernameTitle.setText(
                                "@" + usernameEdit.getText().toString().trim()
                        );

                        Toast.makeText(
                                ProfileActivity.this,
                                "Profile Updated",
                                Toast.LENGTH_SHORT
                        ).show();


                    });

            nameTitle.setText(fullName);

            usernameTitle.setText(
                    "@" + usernameEdit.getText().toString().trim()
            );
        });


        deleteBtn.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            ProfileActivity.this,
                            DeleteAccountActivity.class
                    )
            );

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