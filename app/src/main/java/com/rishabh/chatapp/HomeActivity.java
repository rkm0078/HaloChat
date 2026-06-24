package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.rishabh.chatapp.fragments.CallsFragment;
import com.rishabh.chatapp.fragments.ChatsFragment;
import com.rishabh.chatapp.fragments.FriendsFragment;

public class HomeActivity extends AppCompatActivity {

    ImageView addFriendBtn, settingsBtn;
    private View navChats;
    private View navCalls;
    private View navFriends;

    private View chatPill;
    private View callPill;
    private View friendPill;

    private ImageView chatIcon;
    private ImageView callIcon;
    private ImageView friendIcon;

    private TextView chatText;
    private TextView callText;
    private TextView friendText;


    private final ChatsFragment chatsFragment = new ChatsFragment();
    private final CallsFragment callsFragment = new CallsFragment();
    private final FriendsFragment friendsFragment = new FriendsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        addFriendBtn =
                findViewById(R.id.addFriendBtn);

        settingsBtn =
                findViewById(R.id.settingsBtn);

        navChats = findViewById(R.id.navChats);
        navCalls = findViewById(R.id.navCalls);
        navFriends = findViewById(R.id.navFriends);
        chatPill = findViewById(R.id.chatPill);
        callPill = findViewById(R.id.callPill);
        friendPill = findViewById(R.id.friendPill);

        chatIcon = findViewById(R.id.chatIcon);
        callIcon = findViewById(R.id.callIcon);
        friendIcon = findViewById(R.id.friendIcon);

        chatText = findViewById(R.id.chatText);
        callText = findViewById(R.id.callText);
        friendText = findViewById(R.id.friendText);



        // ADD FRIEND

        addFriendBtn.setOnClickListener(v ->

                startActivity(
                        new Intent(
                                HomeActivity.this,
                                AddFriendsActivity.class
                        )
                )
        );

        // SETTINGS

        settingsBtn.setOnClickListener(v -> {

            View popupView = getLayoutInflater().inflate(R.layout.menu_popup, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setElevation(20);

            popupWindow.showAsDropDown(settingsBtn, -130, 10);

            popupView.findViewById(R.id.menuSettings)
                    .setOnClickListener(view -> {

                        popupWindow.dismiss();

                        startActivity(new Intent(
                                HomeActivity.this,
                                SettingsActivity.class
                        ));
                    });

            popupView.findViewById(R.id.menuAbout)
                    .setOnClickListener(view -> {

                        popupWindow.dismiss();

                        startActivity(new Intent(
                                HomeActivity.this,
                                AboutActivity.class
                        ));
                    });

            popupView.findViewById(R.id.menuLogout)
                    .setOnClickListener(view -> {

                        popupWindow.dismiss();

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(
                                HomeActivity.this,
                                LoginActivity.class
                        );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    });

        });

        navChats.setOnClickListener(v -> {
            loadFragment(chatsFragment);
            updateNavigation(0);
        });

        navCalls.setOnClickListener(v -> {
            loadFragment(callsFragment);
            updateNavigation(1);
        });

        navFriends.setOnClickListener(v -> {
            loadFragment(friendsFragment);
            updateNavigation(2);
        });

        if (savedInstanceState == null) {

            loadFragment(chatsFragment);
            updateNavigation(0);
        }



    }



    private void loadFragment(Fragment fragment) {

        Fragment current =
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainer);

        if (current != null &&
                current.getClass().equals(fragment.getClass())) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateNavigation(int selected) {

        chatPill.setBackground(null);
        callPill.setBackground(null);
        friendPill.setBackground(null);

        chatIcon.setColorFilter(0xFF666666);
        callIcon.setColorFilter(0xFF666666);
        friendIcon.setColorFilter(0xFF666666);

        chatText.setTextColor(0xFF666666);
        callText.setTextColor(0xFF666666);
        friendText.setTextColor(0xFF666666);

        if (selected == 0) {
            chatPill.setBackgroundResource(R.drawable.bg_selected_nav);
            chatIcon.setColorFilter(0xFF00D2FF);
            chatText.setTextColor(0xFF00D2FF);
        } else if (selected == 1) {
            callPill.setBackgroundResource(R.drawable.bg_selected_nav);
            callIcon.setColorFilter(0xFF00D2FF);
            callText.setTextColor(0xFF00D2FF);
        } else {
            friendPill.setBackgroundResource(R.drawable.bg_selected_nav);
            friendIcon.setColorFilter(0xFF00D2FF);
            friendText.setTextColor(0xFF00D2FF);
        }
    }

}