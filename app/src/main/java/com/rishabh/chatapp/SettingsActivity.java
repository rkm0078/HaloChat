package com.rishabh.chatapp;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import android.view.View;

import android.app.ProgressDialog;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView usernameText, emailText;

    LinearLayout checkUpdateBtn;
    LinearLayout logoutBtn;

    LinearLayout aboutBtn;

    LinearLayout changePasswordBtn;

    LinearLayout deleteBtn;

    Switch pushSwitch;

    LinearLayout profileSettingsBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ImageView backBtn;
        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        // =========================
        // FIND VIEW BY ID
        // =========================

        profileImage =
                findViewById(R.id.profileImage);

        profileSettingsBtn =
                findViewById(R.id.profileSettingsBtn);

        usernameText =
                findViewById(R.id.usernameText);

        emailText =
                findViewById(R.id.emailText);

        checkUpdateBtn =
                findViewById(R.id.checkUpdateBtn);

        logoutBtn =
                findViewById(R.id.logoutBtn);

        changePasswordBtn =
                findViewById(R.id.changePasswordBtn);

        deleteBtn =
                findViewById(R.id.deleteBtn);

        pushSwitch =
                findViewById(R.id.pushSwitch);
        aboutBtn =
                findViewById(R.id.aboutBtn);

        // =========================
        // USER INFO
        // =========================

        if (auth.getCurrentUser() != null) {

            String email =
                    auth.getCurrentUser().getEmail();

            emailText.setText(email);

            String username =
                    email.split("@")[0];

            usernameText.setText(username);
        }

        String uid = auth.getUid();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
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

                                Glide.with(
                                                SettingsActivity.this
                                        )
                                        .load(user.profileImage)
                                        .placeholder(
                                                R.drawable.default_profile
                                        )
                                        .error(
                                                R.drawable.default_profile
                                        )
                                        .into(profileImage);
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
        // =========================
        // LOGOUT
        // =========================

        logoutBtn.setOnClickListener(v -> {

            auth.signOut();

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finishAffinity();
        });

        // =========================
        // PROFILE SETTINGS
        // =========================

        profileSettingsBtn.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            SettingsActivity.this,
                            ProfileActivity.class
                    )
            );

        });

        // =========================
        // CHANGE PASSWORD
        // =========================

        changePasswordBtn.setOnClickListener(v -> {

            if (auth.getCurrentUser() != null) {

                auth.sendPasswordResetEmail(
                        auth.getCurrentUser().getEmail()
                );

                Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        // =========================
        // DELETE ACCOUNT
        // =========================

        deleteBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle("Delete Account");

            builder.setMessage("Are you sure?");

            builder.setPositiveButton(
                    "Delete",
                    (dialog, which) -> {

                        if (auth.getCurrentUser() != null) {

                            String deleteUid =
                                    auth.getCurrentUser().getUid();

                            FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(deleteUid)
                                    .removeValue();

                            auth.getCurrentUser()
                                    .delete()
                                    .addOnCompleteListener(task -> {

                                        Toast.makeText(
                                                SettingsActivity.this,
                                                "Account deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent =
                                                new Intent(
                                                        SettingsActivity.this,
                                                        RegisterActivity.class
                                                );

                                        startActivity(intent);

                                        finishAffinity();
                                    });
                        }
                    });

            builder.setNegativeButton(
                    "Cancel",
                    null
            );

            builder.show();
        });

        // =========================
        // PUSH SWITCH
        // =========================

        pushSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        Toast.makeText(
                                this,
                                "Notifications ON",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Notifications OFF",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

        // =========================
        // About button
        // =========================

        aboutBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            AboutActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // Check Update
        // =========================
        checkUpdateBtn.setOnClickListener(v -> checkForUpdates());

        backBtn =
                findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {

            finish();
        });
    }

    private void checkForUpdates() {

        String url =
                "https://raw.githubusercontent.com/rkm0078/HaloChat/master/update.json";

        RequestQueue queue =
                Volley.newRequestQueue(this);

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,

                        response -> {

                            try {

                                String latestVersion =
                                        response.getString("latestVersion");

                                String apkUrl =
                                        response.getString("apkUrl");

                                String currentVersion;

                                try {

                                    currentVersion =
                                            getPackageManager()
                                                    .getPackageInfo(
                                                            getPackageName(),
                                                            0
                                                    ).versionName;

                                } catch (Exception e) {

                                    currentVersion = "0.0";
                                }

                                if (!currentVersion.equals(latestVersion)) {

                                    View view = getLayoutInflater()
                                            .inflate(R.layout.dialog_update, null);

                                    AlertDialog dialog =
                                            new AlertDialog.Builder(this)
                                                    .setView(view)
                                                    .create();

                                    TextView updateMessage =
                                            view.findViewById(R.id.updateMessage);

                                    TextView btnUpdate =
                                            view.findViewById(R.id.btnUpdate);

                                    TextView btnLater =
                                            view.findViewById(R.id.btnLater);

                                    updateMessage.setText(
                                            "A new version " + latestVersion +
                                                    " is available with improved performance and new features.\n\nWould you like to update now?"
                                    );

                                    btnUpdate.setOnClickListener(v -> {

                                        downloadAndInstallApk(apkUrl);
                                        dialog.dismiss();
                                    });

                                    btnLater.setOnClickListener(v ->
                                            dialog.dismiss()
                                    );

                                    dialog.show();

                                    dialog.getWindow().setBackgroundDrawableResource(
                                            android.R.color.transparent);

                                    dialog.getWindow().setLayout(
                                            (int)(getResources().getDisplayMetrics().widthPixels * 0.90),
                                            android.view.WindowManager.LayoutParams.WRAP_CONTENT
                                    );

                                } else {

                                    new AlertDialog.Builder(this)
                                            .setTitle("Up To Date")
                                            .setMessage(
                                                    "You are already using the latest version."
                                            )
                                            .setPositiveButton(
                                                    "OK",
                                                    null
                                            )
                                            .show();
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        },

                        error -> {

                            if (error.networkResponse != null) {

                                Toast.makeText(
                                        this,
                                        "HTTP " + error.networkResponse.statusCode,
                                        Toast.LENGTH_LONG
                                ).show();

                            } else {

                                Toast.makeText(
                                        this,
                                        error.toString(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });

        queue.add(request);
    }

    private void downloadAndInstallApk(String apkUrl) {

        ProgressDialog progressDialog =
                new ProgressDialog(this);

        progressDialog.setTitle("Downloading Update");
        progressDialog.setProgressStyle(
                ProgressDialog.STYLE_HORIZONTAL
        );

        progressDialog.setMax(100);
        progressDialog.show();

        OkHttpClient client =
                new OkHttpClient();

        okhttp3.Request request =
                new okhttp3.Request.Builder()
                        .url(apkUrl)
                        .build();

        client.newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {

                        runOnUiThread(() -> {

                            progressDialog.dismiss();

                            Toast.makeText(
                                    SettingsActivity.this,
                                    "Download Failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    } {

                        runOnUiThread(() ->
                                Toast.makeText(
                                        SettingsActivity.this,
                                        "Download Failed",
                                        Toast.LENGTH_LONG
                                ).show());
                    }

                    @Override
                    public void onResponse(
                            Call call,
                            Response response
                    ) throws IOException {

                        File apkFile =
                                new File(
                                        getExternalFilesDir(
                                                Environment.DIRECTORY_DOWNLOADS
                                        ),
                                        "HaloChat.apk"
                                );

                        InputStream input =
                                response.body().byteStream();

                        FileOutputStream output =
                                new FileOutputStream(apkFile);

                        byte[] buffer =
                                new byte[4096];

                        long total =
                                response.body().contentLength();

                        long downloaded = 0;

                        int count;

                        while ((count = input.read(buffer)) != -1) {

                            downloaded += count;

                            output.write(
                                    buffer,
                                    0,
                                    count
                            );

                            int progress =
                                    (int)
                                            (downloaded * 100 / total);

                            runOnUiThread(() ->
                                    progressDialog.setProgress(
                                            progress
                                    ));
                        }

                        output.flush();
                        output.close();
                        input.close();

                        progressDialog.dismiss();

                        runOnUiThread(() ->
                                installApk(apkFile));
                    }
                });
    }

    private void installApk(File apkFile) {

        Uri apkUri =
                FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        apkFile
                );

        Intent intent =
                new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(
                apkUri,
                "application/vnd.android.package-archive"
        );

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(intent);
    }

}