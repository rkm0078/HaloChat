package com.rishabh.chatapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class FriendRequestsActivity
        extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<User> requests;
    FriendRequestAdapter adapter;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        recyclerView = findViewById(R.id.requestsRecycler);

        requests = new ArrayList<>();

        adapter = new FriendRequestAdapter(
                this,
                requests
        );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        String currentUid =
                FirebaseAuth.getInstance().getUid();

        if (currentUid == null) {
            finish();
            return;
        }

        db = FirebaseDatabase.getInstance()
                .getReference("FriendRequests")
                .child(currentUid);

        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(
                    @NonNull DataSnapshot snapshot) {

                requests.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String senderUid = data.getKey();

                    if (senderUid == null)
                        continue;

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(senderUid)
                            .addListenerForSingleValueEvent(
                                    new ValueEventListener() {

                                        @Override
                                        public void onDataChange(
                                                @NonNull DataSnapshot snapshot) {

                                            User user =
                                                    snapshot.getValue(User.class);

                                            if (user != null) {

                                                requests.add(user);

                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(
                                                @NonNull DatabaseError error) {

                                        }
                                    });
                }
            }

            @Override
            public void onCancelled(
                    @NonNull DatabaseError error) {

                Toast.makeText(
                        FriendRequestsActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}