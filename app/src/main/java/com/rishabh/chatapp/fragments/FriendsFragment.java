package com.rishabh.chatapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rishabh.chatapp.AddFriendsActivity;
import com.rishabh.chatapp.ChatActivity;
import com.rishabh.chatapp.R;
import com.rishabh.chatapp.User;
import com.rishabh.chatapp.adapters.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerFriends;

    private View contentLayout;
    private View emptyLayout;

    private Button btnAddFriends;

    private FriendsAdapter friendAdapter;

    private final ArrayList<User> friendList =
            new ArrayList<>();

    public FriendsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_friends,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerFriends =
                view.findViewById(R.id.recyclerFriends);

        contentLayout =
                view.findViewById(R.id.contentLayout);

        emptyLayout =
                view.findViewById(R.id.emptyLayout);

        btnAddFriends =
                view.findViewById(R.id.btnAddFriends);

        recyclerFriends.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        friendAdapter =
                new FriendsAdapter(
                        requireContext(),
                        friendList,
                        new FriendsAdapter.OnFriendClickListener() {

                            @Override
                            public void onChatClick(User user) {

                                Intent intent =
                                        new Intent(
                                                requireContext(),
                                                ChatActivity.class
                                        );

                                intent.putExtra("uid", user.uid);
                                intent.putExtra("username", user.username);
                                intent.putExtra("profileImage", user.profileImage);

                                startActivity(intent);
                            }

                            @Override
                            public void onVideoCallClick(User user) {

                                showComingSoonDialog(
                                        "Coming Soon",
                                        "Video calling will be available in a future update."
                                );
                            }

                            @Override
                            public void onVoiceCallClick(User user) {

                                showComingSoonDialog(
                                        "Coming Soon",
                                        "Voice calling will be available in a future update."
                                );
                            }
                        }
                );
        recyclerFriends.setAdapter(friendAdapter);

        updateEmptyState();

        loadFriends();


        btnAddFriends.setOnClickListener(v ->

                startActivity(
                        new Intent(
                                requireContext(),
                                AddFriendsActivity.class
                        )
                )
        );
    }

    private void updateEmptyState() {

        if (contentLayout == null ||
                emptyLayout == null) {
            return;
        }

        if (friendList.isEmpty()) {

            emptyLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);

        } else {

            emptyLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadFriends() {

        String currentUid = FirebaseAuth.getInstance().getUid();

        if (currentUid == null) {
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(currentUid)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        friendList.clear();
                        friendAdapter.notifyDataSetChanged();

                        if (!snapshot.exists()) {

                            if (friendAdapter != null) {
                                friendAdapter.notifyDataSetChanged();
                            }

                            updateEmptyState();
                            return;
                        }

                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {

                            String friendUid = friendSnapshot.getKey();

                            boolean exists = false;

                            for (User u : friendList) {
                                if (u.uid != null && u.uid.equals(friendUid)) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (exists) {
                                continue;
                            }

                            if (friendUid == null) {
                                continue;
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("Users")
                                    .child(friendUid)
                                    .addListenerForSingleValueEvent(
                                            new ValueEventListener() {

                                                @Override
                                                public void onDataChange(
                                                        @NonNull DataSnapshot snapshot) {

                                                    User user =
                                                            snapshot.getValue(User.class);

                                                    if (user != null) {

                                                        user.uid = friendUid;

                                                        boolean exists = false;

                                                        for (User u : friendList) {
                                                            if (u.uid != null && u.uid.equals(user.uid)) {
                                                                exists = true;
                                                                break;
                                                            }
                                                        }

                                                        if (!exists) {
                                                            friendList.add(user);
                                                            friendAdapter.notifyDataSetChanged();
                                                        }

                                                        updateEmptyState();
                                                        android.util.Log.d("Friends", "Friend key = " + friendSnapshot.getKey());
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
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        recyclerFriends = null;
        contentLayout = null;
        emptyLayout = null;
        btnAddFriends = null;

        friendAdapter = null;

        friendList.clear();
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
                        requireContext()
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