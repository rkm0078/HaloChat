package com.rishabh.chatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.chatapp.database.entity.MessageEntity;
import com.rishabh.chatapp.database.repository.ChatRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.rishabh.chatapp.database.entity.ChatEntity;
import com.rishabh.chatapp.database.repository.RecentChatRepository;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImage;

    private ImageView backBtn;

    private LinearLayout sendBtn;

    private EditText messageBox;

    private TextView username;

    private TextView statusText;

    private RecyclerView chatList;

    private ArrayList<Message> messages;

    private MessageAdapter adapter;

    private FirebaseAuth auth;

    private String currentUid;

    private String receiverUid;

    private String senderRoom;

    private String receiverRoom;

    private boolean isChatOpen = false;

    private ChatRepository chatRepository;

    private RecentChatRepository recentChatRepository;


    private final HashSet<String> loadedIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        profileImage = findViewById(R.id.profileImage);

        backBtn = findViewById(R.id.backBtn);

        sendBtn = findViewById(R.id.sendBtn);

        messageBox = findViewById(R.id.messageBox);

        username = findViewById(R.id.username);

        statusText = findViewById(R.id.statusText);

        chatList = findViewById(R.id.chatList);

        chatRepository = new ChatRepository(this);

        recentChatRepository =
                new RecentChatRepository(this);

        auth = FirebaseAuth.getInstance();

        currentUid = auth.getUid();

        if (currentUid == null) {
            finish();
            return;
        }

        DatabaseReference statusRef =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(currentUid)
                        .child("status");

        statusRef.setValue("Online");

        statusRef.onDisconnect().setValue("Offline");

        receiverUid = getIntent().getStringExtra("uid");

        if (receiverUid == null) {
            finish();
            return;
        }

        senderRoom = currentUid + receiverUid;

        receiverRoom = receiverUid + currentUid;

        String userName =
                getIntent().getStringExtra("username");

        username.setText(
                userName == null ? "User" : userName
        );

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(receiverUid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                User user =
                                        snapshot.getValue(User.class);

                                if (user == null)
                                    return;

                                Glide.with(ChatActivity.this)
                                        .load(user.profileImage)
                                        .placeholder(R.drawable.default_profile)
                                        .error(R.drawable.default_profile)
                                        .into(profileImage);
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                            }
                        });

        messages = new ArrayList<>();

        adapter = new MessageAdapter(
                this,
                messages,
                currentUid
        );

        LinearLayoutManager manager =
                new LinearLayoutManager(this);

        manager.setStackFromEnd(true);

        chatList.setLayoutManager(manager);

        chatList.setAdapter(adapter);

        // =========================
        // ROOM OBSERVER
        // =========================

        chatRepository.getMessages(currentUid, receiverUid)
                .observe(this, messageEntities -> {

                    messages.clear();

                    for (MessageEntity entity : messageEntities) {

                        Message message = new Message();

                        message.messageId = entity.messageId;
                        message.message = entity.message;
                        message.senderId = entity.senderId;
                        message.receiverId = entity.receiverId;
                        message.timestamp = entity.timestamp;

                        message.delivered =
                                entity.status.equals("delivered")
                                        || entity.status.equals("seen");

                        message.deliveredTime =
                                entity.deliveredTime;

                        message.seen =
                                entity.status.equals("seen");

                        messages.add(message);
                    }

                    adapter.notifyDataSetChanged();

                    if (!messages.isEmpty()) {

                        chatList.scrollToPosition(
                                messages.size() - 1
                        );
                    }
                });

        // =========================
        // RECEIVER STATUS
        // =========================

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(receiverUid)
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                User user =
                                        snapshot.getValue(User.class);

                                if (user == null)
                                    return;

                                if ("Online".equals(user.status)) {

                                    statusText.setText(
                                            "Active now"
                                    );

                                } else {

                                    statusText.setText(
                                            "Last seen recently"
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                            }
                        });

        // =========================
        // FIREBASE MESSAGE LISTENER
        // =========================

        DatabaseReference senderRef =
                FirebaseDatabase.getInstance()
                        .getReference("Chats")
                        .child(senderRoom);

        senderRef.addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(
                            @NonNull DataSnapshot snapshot,
                            String previousChildName) {

                        Message message =
                                snapshot.getValue(Message.class);

                        if (message == null)
                            return;

                        MessageEntity entity =
                                new MessageEntity();

                        entity.messageId =
                                snapshot.getKey();

                        entity.senderId =
                                message.senderId;

                        entity.receiverId =
                                message.senderId.equals(currentUid)
                                        ? receiverUid
                                        : currentUid;

                        entity.message =
                                message.message;

                        entity.timestamp =
                                message.timestamp;

                        entity.type =
                                message.type == null
                                        ? "text"
                                        : message.type;

                        entity.imageUrl =
                                message.imageUrl == null
                                        ? ""
                                        : message.imageUrl;

                        entity.delivered =
                                message.delivered;

                        entity.deliveredTime =
                                message.deliveredTime;

                        if (message.seen) {

                            entity.status = "seen";

                        } else if (message.delivered) {

                            entity.status = "delivered";

                        } else {

                            entity.status = "sent";
                        }

                        if (loadedIds.add(entity.messageId)) {

                            chatRepository.insertMessage(
                                    entity
                            );

                            ChatEntity chat =
                                    new ChatEntity();

                            chat.chatId =
                                    currentUid + "_" + receiverUid;

                            chat.user1 =
                                    currentUid;

                            chat.user2 =
                                    receiverUid;

                            chat.friendUid =
                                    receiverUid;

                            chat.friendName =
                                    username.getText().toString();

                            chat.profileImage =
                                    "";

                            chat.lastMessage =
                                    message.message;

                            chat.lastTimestamp =
                                    message.timestamp;

                            chat.unreadCount =
                                    0;

                            recentChatRepository.insertChat(chat);
                        }

                        if (isChatOpen &&
                                !message.senderId.equals(currentUid)) {

                            long now =
                                    System.currentTimeMillis();

                            snapshot.getRef()
                                    .child("delivered")
                                    .setValue(true);

                            snapshot.getRef()
                                    .child("deliveredTime")
                                    .setValue(now);

                            snapshot.getRef()
                                    .child("seen")
                                    .setValue(true);

                            snapshot.getRef()
                                    .child("seenTime")
                                    .setValue(now);
                        }
                    }

                    @Override
                    public void onChildChanged(
                            @NonNull DataSnapshot snapshot,
                            String previousChildName) {

                        Message message =
                                snapshot.getValue(Message.class);

                        if (message == null)
                            return;

                        MessageEntity entity =
                                new MessageEntity();

                        entity.messageId =
                                snapshot.getKey();

                        entity.senderId =
                                message.senderId;

                        entity.receiverId =
                                message.senderId.equals(currentUid)
                                        ? receiverUid
                                        : currentUid;

                        entity.message =
                                message.message;

                        entity.timestamp =
                                message.timestamp;

                        entity.type =
                                message.type == null
                                        ? "text"
                                        : message.type;

                        entity.imageUrl =
                                message.imageUrl == null
                                        ? ""
                                        : message.imageUrl;

                        entity.delivered =
                                message.delivered;

                        entity.deliveredTime =
                                message.deliveredTime;

                        if (message.seen) {

                            entity.status = "seen";

                        } else if (message.delivered) {

                            entity.status = "delivered";

                        } else {

                            entity.status = "sent";
                        }

                        chatRepository.insertMessage(
                                entity
                        );
                    }

                    @Override
                    public void onChildRemoved(
                            @NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(
                            @NonNull DataSnapshot snapshot,
                            String previousChildName) {

                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                    }
                });


        // =========================
        // SEND MESSAGE
        // =========================

        sendBtn.setOnClickListener(v -> {

            String text =
                    messageBox.getText()
                            .toString()
                            .trim();

            if (text.isEmpty()) {
                return;
            }

            long now =
                    System.currentTimeMillis();

            Message message =
                    new Message(
                            text,
                            currentUid,
                            receiverUid,
                            now
                    );

            String messageId =
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .push()
                            .getKey();

            if (messageId == null) {
                return;
            }

            message.messageId = messageId;

            // Initial status

            message.delivered = false;
            message.deliveredTime = 0;

            message.seen = false;
            message.seenTime = 0;

            // Save locally

            MessageEntity entity =
                    new MessageEntity();

            entity.messageId = messageId;
            entity.senderId = currentUid;
            entity.receiverId = receiverUid;
            entity.message = text;
            entity.timestamp = now;
            entity.type = "text";
            entity.imageUrl = "";

            entity.status = "sent";
            entity.delivered = false;
            entity.deliveredTime = 0;

            chatRepository.insertMessage(entity);

            ChatEntity chat =
                    new ChatEntity();

            chat.chatId =
                    currentUid + "_" + receiverUid;

            chat.user1 =
                    currentUid;

            chat.user2 =
                    receiverUid;

            chat.friendUid =
                    receiverUid;

            chat.friendName =
                    username.getText().toString();

            chat.profileImage =
                    "";

            chat.lastMessage =
                    text;

            chat.lastTimestamp =
                    now;

            chat.unreadCount =
                    0;

            recentChatRepository.insertChat(chat);

            loadedIds.add(messageId);

            messageBox.setText("");

            // Upload sender copy

            FirebaseDatabase.getInstance()
                    .getReference("Chats")
                    .child(senderRoom)
                    .child(messageId)
                    .setValue(message);

            // Upload receiver copy

            FirebaseDatabase.getInstance()
                    .getReference("Chats")
                    .child(receiverRoom)
                    .child(messageId)
                    .setValue(message);

            // Update last message

            HashMap<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "/Users/" + currentUid + "/lastMessage",
                    text
            );

            updates.put(
                    "/Users/" + currentUid + "/lastMessageTime",
                    now
            );

            updates.put(
                    "/Users/" + receiverUid + "/lastMessage",
                    text
            );

            updates.put(
                    "/Users/" + receiverUid + "/lastMessageTime",
                    now
            );

            updates.put(
                    "/ChatList/" + currentUid + "/" + receiverUid + "/lastMessage",
                    text
            );

            updates.put(
                    "/ChatList/" + currentUid + "/" + receiverUid + "/lastMessageTime",
                    now
            );

            updates.put(
                    "/ChatList/" + receiverUid + "/" + currentUid + "/lastMessage",
                    text
            );

            updates.put(
                    "/ChatList/" + receiverUid + "/" + currentUid + "/lastMessageTime",
                    now
            );

            updates.put(
                    "/ChatList/" + receiverUid + "/" + currentUid + "/unreadCount",
                    1
            );

            FirebaseDatabase.getInstance()
                    .getReference()
                    .updateChildren(updates);

        });

        // =========================
        // BACK BUTTON
        // =========================

        backBtn.setOnClickListener(v -> finish());

    } // onCreate ends here


    // =========================
    // ON RESUME
    // =========================

    @Override
    protected void onResume() {

        super.onResume();

        isChatOpen = true;

        FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(currentUid)
                .child(receiverUid)
                .child("unreadCount")
                .setValue(0);

        if (currentUid != null) {

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .child("status")
                    .setValue("Online");
        }
    }

    // =========================
    // ON PAUSE
    // =========================

    @Override
    protected void onPause() {

        super.onPause();

        isChatOpen = false;

        if (currentUid != null) {

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .child("status")
                    .setValue("Offline");

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUid)
                    .child("lastSeen")
                    .setValue(System.currentTimeMillis());
        }
    }

}