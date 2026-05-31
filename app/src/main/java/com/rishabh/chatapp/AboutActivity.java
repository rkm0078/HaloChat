package com.rishabh.chatapp;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        backBtn =
                findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());
    }
}