package com.rishabh.chatapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.chatapp.R;
import com.rishabh.chatapp.User;
import com.rishabh.chatapp.adapters.ChatAdapter;
import com.rishabh.chatapp.database.entity.ChatEntity;
import com.rishabh.chatapp.database.repository.RecentChatRepository;

import java.util.ArrayList;
import java.util.Collections;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerChats;

    private ChatAdapter adapter;

    private ArrayList<User> chatList;

    private View emptyLayout;
    private EditText searchBar;
    private ArrayList<User> allChats;
    private RecentChatRepository recentChatRepository;

    public ChatsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_chats,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        emptyLayout =
                view.findViewById(R.id.emptyLayout);

        searchBar =
                view.findViewById(R.id.searchBar);

        recyclerChats =
                view.findViewById(R.id.recyclerUsers);

        chatList = new ArrayList<>();
        allChats = new ArrayList<>();

        recentChatRepository =
                new RecentChatRepository(
                        requireContext()
                );

        adapter =
                new ChatAdapter(
                        requireContext(),
                        chatList
                );

        recyclerChats.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerChats.setAdapter(
                adapter
        );

        loadChats();

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                filterChats(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        updateEmptyState();
    }

    private void loadChats() {

        String currentUid =
                FirebaseAuth.getInstance().getUid();

        if (currentUid == null)
            return;

        FirebaseDatabase.getInstance()
                .getReference()
                .child("ChatList")
                .child(currentUid)
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                chatList.clear();

                                for (DataSnapshot chatSnapshot :
                                        snapshot.getChildren()) {

                                    String friendUid =
                                            chatSnapshot.getKey();

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
                                                                @NonNull DataSnapshot userSnapshot) {

                                                            User user =
                                                                    userSnapshot.getValue(
                                                                            User.class
                                                                    );

                                                            if (user == null)
                                                                return;

                                                            user.uid = friendUid;

                                                            user.lastMessage =
                                                                    chatSnapshot
                                                                            .child("lastMessage")
                                                                            .getValue(String.class);

                                                            Long time =
                                                                    chatSnapshot
                                                                            .child("lastMessageTime")
                                                                            .getValue(Long.class);

                                                            user.lastMessageTime =
                                                                    time == null ? 0 : time;

                                                            Long unread =
                                                                    chatSnapshot
                                                                            .child("unreadCount")
                                                                            .getValue(Long.class);

                                                            user.unreadCount =
                                                                    unread == null ? 0 : unread.intValue();

                                                            boolean found = false;

                                                            for (int i = 0; i < chatList.size(); i++) {

                                                                if (chatList.get(i).uid != null &&
                                                                        chatList.get(i).uid.equals(user.uid)) {

                                                                    chatList.set(i, user);
                                                                    found = true;
                                                                    break;
                                                                }
                                                            }

                                                            if (!found) {
                                                                chatList.add(user);
                                                            }

                                                            Collections.sort(
                                                                    chatList,
                                                                    (a, b) ->
                                                                            Long.compare(
                                                                                    b.lastMessageTime,
                                                                                    a.lastMessageTime
                                                                            )
                                                            );

                                                            allChats.clear();
                                                            allChats.addAll(chatList);

                                                            adapter.notifyDataSetChanged();
                                                            updateEmptyState();
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

    private void updateEmptyState() {

        if (chatList.isEmpty()) {

            emptyLayout.setVisibility(View.VISIBLE);

            recyclerChats.setVisibility(View.GONE);

            searchBar.setVisibility(View.GONE);

        } else {

            emptyLayout.setVisibility(View.GONE);

            recyclerChats.setVisibility(View.VISIBLE);

            searchBar.setVisibility(View.VISIBLE);
        }
    }

    private void filterChats(String query) {

        chatList.clear();

        if (query.trim().isEmpty()) {

            chatList.addAll(allChats);

        } else {

            for (User user : allChats) {

                String name = "";

                if (user.getFullName() != null) {
                    name = user.getFullName().toLowerCase();
                }

                if (name.contains(
                        query.toLowerCase())) {

                    chatList.add(user);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void observeRoomChats() {

        recentChatRepository
                .getChats()
                .observe(
                        getViewLifecycleOwner(),
                        chats -> {

                            chatList.clear();

                            for (ChatEntity chat : chats) {

                                User user =
                                        new User();

                                user.uid =
                                        chat.friendUid;

                                user.lastMessage =
                                        chat.lastMessage;

                                user.lastMessageTime =
                                        chat.lastTimestamp;

                                user.unreadCount =
                                        chat.unreadCount;

                                String[] parts =
                                        chat.friendName != null
                                                ? chat.friendName.split(" ", 2)
                                                : new String[]{"User", ""};

                                user.firstName =
                                        parts[0];

                                user.lastName =
                                        parts.length > 1
                                                ? parts[1]
                                                : "";

                                user.profileImage =
                                        chat.profileImage;

                                chatList.add(user);
                            }

                            allChats.clear();
                            allChats.addAll(chatList);

                            adapter.notifyDataSetChanged();

                            updateEmptyState();
                        }
                );
    }
}