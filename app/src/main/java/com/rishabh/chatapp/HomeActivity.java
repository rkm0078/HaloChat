package com.rishabh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;
import com.google.firebase.database.*;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {

    ImageView addFriendBtn, settingsBtn;

    EditText searchBar;

    TextView titleText;
    TextView markReadBtn;

    RecyclerView recyclerUsers;

    TabLayout tabLayout;
    HashMap<String, Integer> unreadMap =
            new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        addFriendBtn =
                findViewById(R.id.addFriendBtn);

        settingsBtn =
                findViewById(R.id.settingsBtn);

        searchBar =
                findViewById(R.id.searchBar);

        titleText =
                findViewById(R.id.titleText);


        markReadBtn =
                findViewById(R.id.markReadBtn);

        recyclerUsers =
                findViewById(R.id.recyclerUsers);

        tabLayout =
                findViewById(R.id.tabLayout);

        tabLayout.removeAllTabs();

        tabLayout.addTab(
                tabLayout.newTab().setText("Chats")
        );

        tabLayout.addTab(
                tabLayout.newTab().setText("Friends")
        );

        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

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

        settingsBtn.setOnClickListener(v ->

                startActivity(
                        new Intent(
                                HomeActivity.this,
                                SettingsActivity.class
                        )
                )
        );

        // TABS

        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(
                            @NonNull TabLayout.Tab tab) {

                        if (tab.getPosition() == 0) {

                            // Chats Tab
                            loadChats();

                        } else {

                            // Friends Tab
                            loadFriends();
                        }
                    }

                    @Override
                    public void onTabUnselected(
                            @NonNull TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(
                            @NonNull TabLayout.Tab tab) {
                    }
                }
        );

        // DEFAULT TAB

        loadChats();
    }

    private void loadChats() {

        titleText.setText(
                "RECENT ACTIVITY"
        );

        markReadBtn.setVisibility(
                View.VISIBLE
        );

        ArrayList<User> chatList =
                new ArrayList<>();

        FrequentAdapter adapter =
                new FrequentAdapter(
                        HomeActivity.this,
                        chatList
                );

        recyclerUsers.setAdapter(adapter);

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

                        filterChats(
                                s.toString(),
                                chatList,
                                adapter
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                });

        FirebaseDatabase.getInstance()
                .getReference("Users")

                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                chatList.clear();

                                String currentUid =
                                        FirebaseAuth.getInstance()
                                                .getCurrentUser()
                                                .getUid();

                                for (DataSnapshot snap :
                                        snapshot.getChildren()) {

                                    User user =
                                            snap.getValue(
                                                    User.class
                                            );
                                    String friendUid = snap.getKey();

                                    String room = currentUid + friendUid;

                                    if (user != null &&
                                            !snap.getKey().equals(currentUid) &&
                                            user.lastMessage != null &&
                                            !user.lastMessage.isEmpty()) {
                                        FirebaseDatabase.getInstance()
                                                .getReference("Chats")
                                                .child(room)
                                                .addValueEventListener(
                                                        new ValueEventListener() {

                                                            @Override
                                                            public void onDataChange(
                                                                    @NonNull DataSnapshot chatSnapshot
                                                            ) {

                                                                int unread = 0;

                                                                for (DataSnapshot msgSnap :
                                                                        chatSnapshot.getChildren()) {

                                                                    Message msg =
                                                                            msgSnap.getValue(
                                                                                    Message.class
                                                                            );

                                                                    if (msg != null &&
                                                                            msg.senderId != null &&
                                                                            !msg.seen &&
                                                                            !msg.senderId.equals(currentUid)) {

                                                                        unread++;
                                                                    }
                                                                }

                                                                user.unreadCount = unread;

                                                                unreadMap.put(
                                                                        user.uid,
                                                                        unread
                                                                );
                                                                System.out.println(
                                                                        user.getFullName()
                                                                                + " unread = "
                                                                                + unread
                                                                );

                                                                adapter.notifyDataSetChanged();
                                                                Collections.sort(
                                                                        chatList,
                                                                        (u1, u2) ->
                                                                                Long.compare(
                                                                                        u2.lastMessageTime,
                                                                                        u1.lastMessageTime
                                                                                )
                                                                );

                                                                adapter.notifyDataSetChanged();
                                                            }

                                                            @Override
                                                            public void onCancelled(
                                                                    @NonNull DatabaseError error
                                                            ) {
                                                            }
                                                        });
                                        if (unreadMap.containsKey(user.uid)) {

                                            user.unreadCount =
                                                    unreadMap.get(user.uid);
                                        }
                                        chatList.add(user);
                                    }
                                }

                                Collections.sort(
                                        chatList,
                                        (u1, u2) ->
                                                Long.compare(
                                                        u2.lastMessageTime,
                                                        u1.lastMessageTime
                                                )
                                );

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {
                            }
                        });
    }

    private void loadFriends() {

        titleText.setText(
                "ALL FRIENDS"
        );

        markReadBtn.setVisibility(
                View.GONE
        );

        String currentUid =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        ArrayList<User> friendsList =
                new ArrayList<>();

        FriendAdapter adapter =
                new FriendAdapter(friendsList);

        recyclerUsers.setAdapter(adapter);

        FirebaseDatabase.getInstance()
                .getReference("Friends")
                .child(currentUid)

                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                friendsList.clear();

                                for (DataSnapshot friend :
                                        snapshot.getChildren()) {

                                    String friendUid =
                                            friend.getKey();

                                    FirebaseDatabase
                                            .getInstance()
                                            .getReference("Users")
                                            .child(friendUid)

                                            .addListenerForSingleValueEvent(
                                                    new ValueEventListener() {

                                                        @Override
                                                        public void onDataChange(
                                                                @NonNull DataSnapshot snap
                                                        ) {

                                                            User user =
                                                                    snap.getValue(
                                                                            User.class
                                                                    );

                                                            if (user != null) {

                                                                friendsList.add(user);

                                                                adapter.notifyDataSetChanged();
                                                            }
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

    private void filterChats(
            String text,
            ArrayList<User> originalList,
            FrequentAdapter adapter
    ) {

        ArrayList<User> filtered =
                new ArrayList<>();

        for (User user : originalList) {

            if (user.getFullName()
                    .toLowerCase()
                    .contains(
                            text.toLowerCase()
                    )) {

                filtered.add(user);
            }
        }

        recyclerUsers.setAdapter(
                new FrequentAdapter(
                        HomeActivity.this,
                        filtered
                )
        );
    }
}