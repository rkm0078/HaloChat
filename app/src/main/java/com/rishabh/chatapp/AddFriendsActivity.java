package com.rishabh.chatapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    // =========================
    // Views
    // =========================

    private RecyclerView usersRecycler;

    private RecyclerView requestRecycler;

    private EditText searchBar;

    private ImageView backBtn;

    private TextView requestCount;

    private LinearLayout emptyRequestLayout;

    private View qrSection;

    private View pendingSection;

    private View suggestedSection;

    private LinearLayout inviteFriendsBtn;

    private LinearLayout btnMyQr;

    private LinearLayout btnScanQr;

    // =========================
    // Firebase
    // =========================

    private FirebaseAuth auth;

    private String currentUid;


    // =========================
    // Lists
    // =========================

    private final ArrayList<User> users =
            new ArrayList<>();

    private final ArrayList<User> allUsers =
            new ArrayList<>();

    private final ArrayList<User> requestUsers =
            new ArrayList<>();

    // =========================
    // Status Sets
    // =========================

    private final HashSet<String> friendIds = new HashSet<>();
    private final HashSet<String> sentRequestIds = new HashSet<>();
    private final HashSet<String> receivedRequestIds = new HashSet<>();

    // =========================
    // Adapter
    // =========================

    private UserRecyclerAdapter userAdapter;

    private FriendRequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_friends);

        // =========================
        // FIND VIEWS
        // =========================

        usersRecycler =
                findViewById(R.id.recyclerUsers);

        requestRecycler =
                findViewById(R.id.requestRecycler);

        searchBar =
                findViewById(R.id.searchBar);

        backBtn =
                findViewById(R.id.backBtn);

        requestCount =
                findViewById(R.id.requestCount);

        emptyRequestLayout =
                findViewById(R.id.emptyRequestLayout);

        qrSection =
                findViewById(R.id.qrSection);

        pendingSection =
                findViewById(R.id.pendingSection);

        suggestedSection =
                findViewById(R.id.suggestedSection);

        inviteFriendsBtn =
                findViewById(R.id.inviteFriendsBtn);

        btnMyQr =
                findViewById(R.id.btnMyQr);

        btnScanQr =
                findViewById(R.id.btnScanQr);

        // =========================
        // FIREBASE
        // =========================

        auth = FirebaseAuth.getInstance();

        currentUid = auth.getUid();

        if (currentUid == null) {

            finish();

            return;
        }

        // =========================
        // USER RECYCLER
        // =========================

        usersRecycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        usersRecycler.setHasFixedSize(false);

        userAdapter =
                new UserRecyclerAdapter(
                        this,
                        users,
                        friendIds,
                        sentRequestIds,
                        receivedRequestIds,
                        new UserRecyclerAdapter.OnFriendClickListener() {

                            @Override
                            public void onChatClick(User user) {
                            }

                            @Override
                            public void onVoiceCallClick(User user) {
                            }

                            @Override
                            public void onVideoCallClick(User user) {
                            }
                        }
                );

        usersRecycler.setAdapter(userAdapter);

        // =========================
        // REQUEST RECYCLER
        // =========================

        requestRecycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        requestRecycler.setHasFixedSize(false);

        requestAdapter =
                new FriendRequestAdapter(
                        this,
                        requestUsers
                );

        requestRecycler.setAdapter(requestAdapter);


        // =========================
        // BUTTONS
        // =========================

        backBtn.setOnClickListener(v -> finish());

        btnMyQr.setOnClickListener(v ->

                showComingSoonDialog(
                        "Coming Soon",
                        "Personal QR profiles will be available in a future update."
                )

        );

        btnScanQr.setOnClickListener(v ->

                showComingSoonDialog(
                        "Coming Soon",
                        "QR code scanning will be available in a future update."
                )

        );

        inviteFriendsBtn.setOnClickListener(v ->

                showComingSoonDialog(
                        "Coming Soon",
                        "Invite friends will be available in a future update."
                )

        );

        // =========================
        // LOAD DATA
        // =========================

        loadUsers();

        loadFriendIds();

        loadSentRequests();

        loadIncomingRequests();

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
                }
        );
    }

    // =========================
// LOAD FRIEND IDS
// =========================

    private void loadFriendIds() {

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

                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    friendIds.add(ds.getKey());
                                }

                                userAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }


// =========================
// LOAD SENT REQUESTS
// =========================

    private void loadSentRequests() {

        FirebaseDatabase.getInstance()
                .getReference("FriendRequests")
                .child(currentUid)
                .child("sent")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                sentRequestIds.clear();

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    sentRequestIds.add(ds.getKey());
                                }

                                userAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }


// =========================
// LOAD ALL USERS
// =========================

    private void loadUsers() {

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                allUsers.clear();

                                users.clear();

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    User user =
                                            ds.getValue(User.class);

                                    if (user == null)
                                        continue;

                                    user.uid = ds.getKey();

                                    if (currentUid.equals(user.uid)) {
                                        continue;
                                    }

                                    allUsers.add(user);
                                }

                                users.addAll(allUsers);

                                userAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }

    // =========================
// LOAD INCOMING REQUESTS
// =========================

    private void loadIncomingRequests() {

        FirebaseDatabase.getInstance()
                .getReference("FriendRequests")
                .child(currentUid)
                .child("received")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                receivedRequestIds.clear();

                                requestUsers.clear();

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    String uid = ds.getKey();

                                    if (uid == null)
                                        continue;

                                    receivedRequestIds.add(uid);

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

                                                            user.uid = snapshot.getKey();

                                                            boolean exists = false;

                                                            for (User u : requestUsers) {

                                                                if (u.uid.equals(user.uid)) {

                                                                    exists = true;
                                                                    break;
                                                                }
                                                            }

                                                            if (!exists) {

                                                                requestUsers.add(user);

                                                                requestAdapter.notifyDataSetChanged();

                                                                updateRequestUI();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(
                                                                @NonNull DatabaseError error
                                                        ) {

                                                        }
                                                    });
                                }

                                updateRequestUI();

                                userAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }

    // =========================
// SEARCH USERS
// =========================

    private void filterUsers(String text) {

        boolean searching =
                text != null &&
                        !text.trim().isEmpty();

        if (searching) {

            qrSection.setVisibility(View.GONE);
            pendingSection.setVisibility(View.GONE);
            suggestedSection.setVisibility(View.GONE);
            inviteFriendsBtn.setVisibility(View.GONE);

        } else {

            qrSection.setVisibility(View.VISIBLE);
            pendingSection.setVisibility(View.VISIBLE);
            suggestedSection.setVisibility(View.VISIBLE);
            inviteFriendsBtn.setVisibility(View.VISIBLE);
        }

        users.clear();

        if (text == null ||
                text.trim().isEmpty()) {

            allUsers.sort((u1, u2) -> {

                String name1 = u1.username == null ? "" : u1.username;

                String name2 = u2.username == null ? "" : u2.username;

                return name1.compareToIgnoreCase(name2);
            });

            users.addAll(allUsers);

        } else {

            String query =
                    text.toLowerCase().trim();

            for (User user : allUsers) {

                String username =
                        user.username == null
                                ? ""
                                : user.username.toLowerCase();

                String email =
                        user.email == null
                                ? ""
                                : user.email.toLowerCase();

                String fullName =
                        user.getFullName() == null
                                ? ""
                                : user.getFullName().toLowerCase();

                if (username.contains(query)
                        || email.contains(query)
                        || fullName.contains(query)
                        || user.uid.toLowerCase().contains(query)) {

                    users.add(user);
                }
            }
        }

        userAdapter.notifyDataSetChanged();
    }

    // =========================
// UPDATE REQUEST UI
// =========================

    private void updateRequestUI() {

        requestCount.setText(
                String.valueOf(
                        requestUsers.size()
                )
        );

        if (requestUsers.isEmpty()) {

            emptyRequestLayout.setVisibility(
                    View.VISIBLE
            );

            requestRecycler.setVisibility(
                    View.GONE
            );

        } else {

            emptyRequestLayout.setVisibility(
                    View.GONE
            );

            requestRecycler.setVisibility(
                    View.VISIBLE
            );
        }
    }
    private void showComingSoonDialog(
            String title,
            String message
    ) {

        View dialogView =
                getLayoutInflater().inflate(
                        R.layout.dialog_coming_soon,
                        null
                );

        AlertDialog dialog =
                new AlertDialog.Builder(
                        AddFriendsActivity.this
                )
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

        titleText.setText(title);

        messageText.setText(message);

        dialogView.findViewById(
                R.id.okBtn
        ).setOnClickListener(v ->
                dialog.dismiss()
        );

        dialog.show();
    }
}