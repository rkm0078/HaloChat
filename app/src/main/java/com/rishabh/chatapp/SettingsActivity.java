package com.rishabh.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.chatapp.database.DatabaseClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView profileImage;

    FirebaseAuth auth;

    ImageView qrBtn;
    LinearLayout profileSection;
    TextView usernameText;
    LinearLayout accountRow;
    LinearLayout linkedDevicesRow;
    LinearLayout privacyRow;
    LinearLayout securityRow;
    LinearLayout chatsRow;
    LinearLayout notificationsRow;
    LinearLayout storageRow;
    LinearLayout languageRow;
    LinearLayout helpRow;
    LinearLayout inviteRow;
    LinearLayout aboutRow;
    LinearLayout updateRow;
    LinearLayout logoutRow;
    ImageView backBtn;
    TextView versionText;
    TextView nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        // =========================
        // FIND VIEW BY ID
        // =========================

        profileImage =
                findViewById(R.id.profileImage);

        usernameText =
                findViewById(R.id.usernameText);

        profileSection = findViewById(R.id.profileSection);

        qrBtn = findViewById(R.id.qrBtn);

        accountRow = findViewById(R.id.accountRow);
        linkedDevicesRow = findViewById(R.id.linkedDevicesRow);
        privacyRow = findViewById(R.id.privacyRow);
        securityRow = findViewById(R.id.securityRow);
        chatsRow = findViewById(R.id.chatsRow);
        notificationsRow = findViewById(R.id.notificationsRow);
        storageRow = findViewById(R.id.storageRow);
        languageRow = findViewById(R.id.languageRow);
        helpRow = findViewById(R.id.helpRow);
        inviteRow = findViewById(R.id.inviteRow);
        aboutRow = findViewById(R.id.aboutRow);
        updateRow = findViewById(R.id.updateRow);
        logoutRow = findViewById(R.id.logoutRow);

        versionText = findViewById(R.id.versionText);

        nameText = findViewById(R.id.nameText);
        usernameText = findViewById(R.id.usernameText);
        backBtn = findViewById(R.id.backBtn);

        // =========================
        // BACK BUTTON
        // =========================


        backBtn.setOnClickListener(v -> finish());

        // =========================
        // USER INFO
        // =========================

        String uid = auth.getUid();

        if (uid == null) {
            finish();
            return;
        }

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

                                nameText.setText(
                                        user.getFullName()
                                );

                                if (user.username != null &&
                                        !user.username.isEmpty()) {

                                    usernameText.setText(
                                            "@" + user.username
                                    );

                                } else {

                                    usernameText.setText("@user");
                                }

                                Glide.with(
                                                SettingsActivity.this
                                        )
                                        .load(
                                                user.profileImage == null ||
                                                        user.profileImage.isEmpty()
                                                        ? R.drawable.default_profile
                                                        : user.profileImage
                                        )
                                        .placeholder(
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
        // VERSION
        // =========================

        try {

            String currentVersion =
                    getPackageManager()
                            .getPackageInfo(
                                    getPackageName(),
                                    0
                            ).versionName;

            versionText.setText(
                    "Version " + currentVersion
            );

        } catch (Exception e) {

            versionText.setText(
                    "Version Unknown"
            );
        }

        // =========================
        // PROFILE SECTION
        // =========================

        profileSection.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            SettingsActivity.this,
                            ProfileActivity.class
                    )
            );
        });

        // =========================
        // ABOUT
        // =========================

        aboutRow.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            SettingsActivity.this,
                            AboutActivity.class
                    )
            );
        });

        // =========================
        // UPDATE
        // =========================

        updateRow.setOnClickListener(v ->
                checkForUpdates());

        // =========================
        // LOGOUT
        // =========================

        logoutRow.setOnClickListener(v -> {

            auth.signOut();

            new Thread(() ->
                    DatabaseClient
                            .getInstance(this)
                            .getDatabase()
                            .clearAllTables()
            ).start();

            Intent intent =
                    new Intent(
                            SettingsActivity.this,
                            LoginActivity.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });

        // =========================
        // SETTINGS WHICH ARE COMMING SOON
        // =========================

        qrBtn.setOnClickListener(view ->
                showComingSoonDialog(
                        "Coming Soon",
                        "QR code section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        accountRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Account section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        linkedDevicesRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Linked devices section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        privacyRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Privacy section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        securityRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Security section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        chatsRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Chats section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        notificationsRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Notifications section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        storageRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Storage and data section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        languageRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Language section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        helpRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Help section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        inviteRow.setOnClickListener(v ->
                showComingSoonDialog(
                        "Coming Soon",
                        "Invite section will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

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

                                    btnUpdate.setOnClickListener(v1 -> {

                                        downloadAndInstallApk(apkUrl);
                                        dialog.dismiss();
                                    });

                                    btnLater.setOnClickListener(v1 ->
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
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("0% Downloaded");
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
                    }

                    @Override
                    public void onResponse(

                            Call call,
                            Response response
                    ) throws IOException {

                        if (!response.isSuccessful()) {

                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(
                                        SettingsActivity.this,
                                        "Server Error: " + response.code(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });

                            return;
                        }

                        File apkFile =
                                new File(
                                        getExternalFilesDir(
                                                Environment.DIRECTORY_DOWNLOADS
                                        ),
                                        "HaloChat.apk"
                                );
                        if (apkFile.exists() && !apkFile.delete()) {
                            runOnUiThread(() -> {
                                Toast.makeText(
                                        SettingsActivity.this,
                                        "Unable to replace old APK",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                            return;
                        }

                        if (response.body() == null) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(
                                        SettingsActivity.this,
                                        "Empty server response",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                            return;
                        }

                        InputStream input =
                                response.body().byteStream();

                        FileOutputStream output =
                                new FileOutputStream(apkFile);

                        byte[] buffer =
                                new byte[8192];

                        long total = response.body().contentLength();

                        if (total <= 0) {
                            total = 1;
                        }

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
                                    (int) (downloaded * 100 / total);

                            runOnUiThread(() -> {

                                progressDialog.setProgress(progress);

                                progressDialog.setMessage(
                                        progress + "% Downloaded"
                                );
                            });
                        }

                        try {
                            output.flush();
                        } finally {
                            output.close();
                            input.close();
                        }

                        runOnUiThread(() -> {

                            progressDialog.dismiss();

                            Toast.makeText(
                                    SettingsActivity.this,
                                    "Download Complete",
                                    Toast.LENGTH_SHORT
                            ).show();
                            if (apkFile.exists()) {
                                installApk(apkFile);
                            } else {
                                Toast.makeText(
                                        SettingsActivity.this,
                                        "APK file not found",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    }
                });
    }


    private void installApk(File apkFile) {

        try {

            Toast.makeText(
                    this,
                    "Opening installer...",
                    Toast.LENGTH_SHORT
            ).show();

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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                if (!getPackageManager().canRequestPackageInstalls()) {

                    Intent settingsIntent =
                            new Intent(
                                    android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                    Uri.parse("package:" + getPackageName())
                            );

                    startActivity(settingsIntent);

                    return;
                }
            }
            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "INSTALL ERROR:\n" + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    private void showComingSoonDialog(
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

}