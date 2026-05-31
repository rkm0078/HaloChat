package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<User> users;

    RecentChatsAdapter adapter;

    FirebaseAuth auth;

    EditText searchBar;

    LinearLayout fabBtn;

    ImageView addFriendBtn;
    ImageView settingsBtn;

    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // =========================
        // FIND VIEWS
        // =========================

        recyclerView =
                findViewById(R.id.recyclerUsers);

        addFriendBtn =
                findViewById(R.id.addFriendBtn);

        searchBar =
                findViewById(R.id.searchBar);

        fabBtn =
                findViewById(R.id.fabBtn);

        settingsBtn =
                findViewById(R.id.settingsBtn);

        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        currentUid = auth.getUid();

        // =========================
        // ONLINE STATUS
        // =========================

        if (currentUid != null) {

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .child("status")
                    .setValue("Online");
        }

        // =========================
        // USER LIST
        // =========================

        users = new ArrayList<>();

        adapter =
                new RecentChatsAdapter(
                        this,
                        users
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        // =========================
        // LOAD FRIENDS
        // =========================

        loadFriends();

        // =========================
        // SEARCH
        // =========================

        searchBar.addTextChangedListener(
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

                        filterUsers(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                });

        // =========================
        // ADD FRIENDS
        // =========================

        addFriendBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            AddFriendsActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // SETTINGS
        // =========================

        settingsBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // NEW CHAT
        // =========================

        fabBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            NewChatActivity.class
                    );

            startActivity(intent);
        });
    }

    // =========================
    // LOAD FRIENDS
    // =========================

    private void loadFriends() {

        if (currentUid == null)
            return;

        DatabaseReference friendsRef =
                FirebaseDatabase.getInstance()
                        .getReference("Friends")
                        .child(currentUid);

        friendsRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        users.clear();

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            String friendUid =
                                    data.getKey();

                            if (friendUid == null ||
                                    friendUid.isEmpty()) {

                                continue;
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(friendUid)

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

                                                    // IMPORTANT FIX

                                                    user.uid =
                                                            friendUid;

                                                    users.add(user);

                                                    adapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(
                                                        @NonNull DatabaseError error
                                                ) {

                                                }
                                            });
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                    }
                });
    }

    // =========================
    // FILTER USERS
    // =========================

    private void filterUsers(String text) {

        ArrayList<User> filtered =
                new ArrayList<>();

        for (User user : users) {

            String username =
                    user.username != null
                            ? user.username
                            : "";

            if (username.toLowerCase()
                    .contains(text.toLowerCase())) {

                filtered.add(user);
            }
        }

        adapter =
                new RecentChatsAdapter(
                        this,
                        filtered
                );

        recyclerView.setAdapter(adapter);
    }

    // =========================
    // OFFLINE STATUS
    // =========================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (currentUid != null) {

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .child("status")
                    .setValue("Offline");
        }
    }
}