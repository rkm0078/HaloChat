package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class AddFriendsActivity extends AppCompatActivity {

    RecyclerView recyclerUsers;
    RecyclerView requestRecycler;

    ArrayList<User> users;
    ArrayList<User> requestUsers;

    UserRecyclerAdapter adapter;
    FriendRequestAdapter requestAdapter;

    ImageView backBtn;
    ImageView settingsBtn;

    EditText searchBar;

    TextView requestCount;

    LinearLayout emptyRequestBox;

    FirebaseAuth auth;

    String currentUid;

    HashSet<String> friendIds;
    HashSet<String> requestIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_friends);

        // =========================
        // FIND VIEWS
        // =========================

        recyclerUsers =
                findViewById(R.id.recyclerUsers);

        requestRecycler =
                findViewById(R.id.requestRecycler);

        searchBar =
                findViewById(R.id.searchBar);

        requestCount =
                findViewById(R.id.requestCount);

        emptyRequestBox =
                findViewById(R.id.emptyRequestLayout);

        backBtn =
                findViewById(R.id.backBtn);

        settingsBtn =
                findViewById(R.id.settingsBtn);

        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        currentUid = auth.getUid();

        friendIds = new HashSet<>();

        requestIds = new HashSet<>();

        // =========================
        // BACK BUTTON
        // =========================

        backBtn.setOnClickListener(v -> finish());

        settingsBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AddFriendsActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // USERS LIST
        // =========================

        users = new ArrayList<>();

        adapter =
                new UserRecyclerAdapter(
                        this,
                        users,
                        friendIds,
                        requestIds
                );

        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerUsers.setAdapter(adapter);

        // =========================
        // REQUEST LIST
        // =========================

        requestUsers = new ArrayList<>();

        requestAdapter =
                new FriendRequestAdapter(
                        this,
                        requestUsers
                );

        requestRecycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        requestRecycler.setAdapter(requestAdapter);

        // =========================
        // LOAD SENT REQUEST IDS
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("FriendRequests")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                requestIds.clear();

                                for (DataSnapshot targetUser :
                                        snapshot.getChildren()) {

                                    if (targetUser.hasChild(currentUid)) {

                                        requestIds.add(
                                                targetUser.getKey()
                                        );
                                    }
                                }

                                loadUsers();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });

        // =========================
        // LOAD FRIEND IDS
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("Friends")
                .child(currentUid)
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                friendIds.clear();

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {

                                    friendIds.add(
                                            data.getKey()
                                    );
                                }

                                loadUsers();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });

        // =========================
        // LOAD FRIEND REQUESTS
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("FriendRequests")
                .child(currentUid)
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                requestUsers.clear();

                                int count =
                                        (int) snapshot.getChildrenCount();

                                requestCount.setText(
                                        String.valueOf(count)
                                );

                                if (count > 0) {

                                    requestRecycler.setVisibility(
                                            View.VISIBLE
                                    );

                                    emptyRequestBox.setVisibility(
                                            View.GONE
                                    );

                                } else {

                                    requestRecycler.setVisibility(
                                            View.GONE
                                    );

                                    emptyRequestBox.setVisibility(
                                            View.VISIBLE
                                    );
                                }

                                // LOAD REAL USERS

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {

                                    String uid =
                                            data.getKey();

                                    if (uid == null)
                                        continue;

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
                                                                    snapshot.getValue(User.class);

                                                            if (user == null)
                                                                return;

                                                            requestUsers.add(user);

                                                            requestAdapter.notifyDataSetChanged();
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
    }

    // =========================
    // LOAD USERS
    // =========================

    private void loadUsers() {

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                users.clear();

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {

                                    User user =
                                            data.getValue(User.class);

                                    if (user == null)
                                        continue;

                                    if (user.uid == null)
                                        continue;

                                    if (user.uid.equals(currentUid))
                                        continue;

                                    // ALREADY FRIEND

                                    if (friendIds.contains(user.uid))
                                        continue;

                                    // REQUEST ALREADY SENT

                                    if (requestIds.contains(user.uid))
                                        continue;

                                    users.add(user);
                                }

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }

    // =========================
    // SEARCH FILTER
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
                new UserRecyclerAdapter(
                        this,
                        filtered,
                        friendIds,
                        requestIds
                );

        recyclerUsers.setAdapter(adapter);
    }
}