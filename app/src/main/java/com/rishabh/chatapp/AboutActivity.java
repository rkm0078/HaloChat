package com.rishabh.chatapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AboutActivity extends AppCompatActivity {

    private ImageView backBtn;

    private TextView versionText;

    private View versionHistoryLayout;
    private View termsLayout;
    private View privacyLayout;
    private View licensesLayout;

    private ImageView linkedinBtn;
    private ImageView githubBtn;
    private ImageView discordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        initViews();

        loadVersion();

        setupClicks();
    }

    private void initViews() {

        backBtn =
                findViewById(R.id.backBtn);

        versionText =
                findViewById(R.id.versionText);

        versionHistoryLayout =
                findViewById(R.id.versionHistoryLayout);

        termsLayout =
                findViewById(R.id.termsLayout);

        privacyLayout =
                findViewById(R.id.privacyLayout);

        licensesLayout =
                findViewById(R.id.licensesLayout);

        linkedinBtn =
                findViewById(R.id.linkedinBtn);

        githubBtn =
                findViewById(R.id.githubBtn);

        discordBtn =
                findViewById(R.id.discordBtn);
    }

    private void loadVersion() {

        try {

            String currentVersion =
                    getPackageManager()
                            .getPackageInfo(
                                    getPackageName(),
                                    0
                            ).versionName;

            versionText.setText(
                    "v" + currentVersion + " • SECURE"
            );

        } catch (Exception e) {

            versionText.setText(
                    "v1.0 • SECURE"
            );
        }
    }

    private void setupClicks() {

        backBtn.setOnClickListener(v ->
                finish()
        );

        versionHistoryLayout.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "Version History will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        termsLayout.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "Terms of Service page will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        privacyLayout.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "Privacy Policy page will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        licensesLayout.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "Open Source Licenses page will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        linkedinBtn.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "LinkedIn page will be available soon.",
                        "OK",
                        null
                ));

        githubBtn.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "GitHub page will be available soon.",
                        "OK",
                        null
                ));

        discordBtn.setOnClickListener(v ->
                showHaloDialog(
                        "Coming Soon",
                        "Discord community will be available soon.",
                        "OK",
                        null
                ));
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
}