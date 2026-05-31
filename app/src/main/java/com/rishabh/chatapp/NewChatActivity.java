package com.rishabh.chatapp;

import android.os.Bundle;
import android.widget.ImageView;

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

public class NewChatActivity extends AppCompatActivity {

    ImageView backBtn;

    RecyclerView friendsRecycler;
    RecyclerView frequentRecycler;

    ArrayList<User> users;
    ArrayList<User> frequentUsers;

    NewChatAdapter adapter;
    FrequentAdapter frequentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_chat);

        backBtn = findViewById(R.id.backBtn);

        friendsRecycler =
                findViewById(R.id.friendsRecycler);

        frequentRecycler =
                findViewById(R.id.frequentRecycler);

        users = new ArrayList<>();

        frequentUsers = new ArrayList<>();

        adapter =
                new NewChatAdapter(
                        this,
                        users
                );

        frequentAdapter =
                new FrequentAdapter(
                        this,
                        frequentUsers
                );

        friendsRecycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        friendsRecycler.setAdapter(adapter);

        LinearLayoutManager horizontalLayout =
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        frequentRecycler.setLayoutManager(
                horizontalLayout
        );

        frequentRecycler.setAdapter(
                frequentAdapter
        );

        loadUsers();

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadUsers() {

        String currentUid =
                FirebaseAuth.getInstance()
                        .getUid();

        if (currentUid == null)
            return;

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(currentUid)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        users.clear();

                        frequentUsers.clear();

                        for (DataSnapshot dataSnapshot :
                                snapshot.getChildren()) {

                            String friendUid =
                                    dataSnapshot.getKey();

                            if (friendUid == null)
                                continue;

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("Users")
                                    .child(friendUid)
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

                                                    user.uid = friendUid;

                                                    users.add(user);

                                                    if (frequentUsers.size() < 5) {

                                                        frequentUsers.add(user);
                                                    }

                                                    adapter.notifyDataSetChanged();

                                                    frequentAdapter.notifyDataSetChanged();
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
}