package com.rishabh.chatapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.chatapp.database.entity.MessageEntity;
import com.rishabh.chatapp.database.repository.ChatRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import com.rishabh.chatapp.database.entity.ChatEntity;
import com.rishabh.chatapp.database.repository.RecentChatRepository;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private ImageView menuBtn;
    private ImageView backBtn;
    private EditText messageBox;
    private ImageView sendIcon;
    private FrameLayout sendBtn;
    private ImageView emojiBtn;
    private ImageView attachBtn;
    private ImageView cameraBtn;


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
    private ImageView videoCallBtn;
    private ImageView callBtn;

    private boolean isChatOpen = false;

    private ChatRepository chatRepository;

    private RecentChatRepository recentChatRepository;


    private final HashSet<String> loadedIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        profileImage = findViewById(R.id.profileImage);
        emojiBtn = findViewById(R.id.emojiBtn);
        attachBtn = findViewById(R.id.attachBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        backBtn = findViewById(R.id.backBtn);
        menuBtn = findViewById(R.id.menuBtn);
        username = findViewById(R.id.nameText);

        chatList = findViewById(R.id.chatRecycler);

        statusText = findViewById(R.id.statusText);
        videoCallBtn = findViewById(R.id.videoCallBtn);
        callBtn = findViewById(R.id.callBtn);

        messageBox = findViewById(R.id.messageBox);
        sendIcon = findViewById(R.id.sendIcon);
        sendBtn = findViewById(R.id.sendBtn);

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
                messages
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

                    String lastDate = "";

                    for (MessageEntity entity : messageEntities) {

                        Message message = new Message();

                        message.messageId = entity.messageId;
                        message.message = entity.message;
                        message.senderId = entity.senderId;
                        message.receiverId = entity.receiverId;
                        message.timestamp = entity.timestamp;
                        message.status = entity.status;

                        String currentDate =
                                DATE_FORMAT.format(
                                        new Date(message.timestamp)
                                );

                        if (!currentDate.equals(lastDate)) {

                            Message dateChip = new Message();

                            dateChip.isDateChip = true;

                            dateChip.dateText = currentDate;

                            messages.add(dateChip);

                            lastDate = currentDate;
                        }

                        message.deliveredTime =
                                entity.deliveredTime;

                        message.delivered =
                                "delivered".equals(entity.status)
                                        || "seen".equals(entity.status);

                        message.seen =
                                "seen".equals(entity.status);

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

                                    long lastSeen = user.lastSeen;

                                    long diff =
                                            System.currentTimeMillis() - lastSeen;

                                    long minutes =
                                            diff / (1000 * 60);

                                    if (minutes < 1) {

                                        statusText.setText("Last seen just now");

                                    } else if (minutes < 60) {

                                        statusText.setText(
                                                "Last seen " + minutes + " min ago"
                                        );

                                    } else {

                                        long hours = minutes / 60;

                                        statusText.setText(
                                                "Last seen " + hours + " hr ago"
                                        );
                                    }
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

                        if (message.status != null) {

                            entity.status = message.status;

                        } else if (message.seen) {

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

                            String messageId = snapshot.getKey();

                            if (messageId == null) {
                                return;
                            }

                            DatabaseReference root =
                                    FirebaseDatabase.getInstance()
                                            .getReference("Chats");

// Receiver copy
                            root.child(senderRoom)
                                    .child(messageId)
                                    .child("delivered")
                                    .setValue(true);

                            root.child(senderRoom)
                                    .child(messageId)
                                    .child("deliveredTime")
                                    .setValue(now);

// Sender copy
                            root.child(receiverRoom)
                                    .child(messageId)
                                    .child("delivered")
                                    .setValue(true);

                            root.child(receiverRoom)
                                    .child(messageId)
                                    .child("deliveredTime")
                                    .setValue(now);

                            // Receiver copy
                            root.child(senderRoom)
                                    .child(messageId)
                                    .child("seen")
                                    .setValue(true);

                            root.child(senderRoom)
                                    .child(messageId)
                                    .child("seenTime")
                                    .setValue(now);

// Sender copy
                            root.child(receiverRoom)
                                    .child(messageId)
                                    .child("seen")
                                    .setValue(true);

                            root.child(receiverRoom)
                                    .child(messageId)
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

                        if (message.status != null) {

                            entity.status = message.status;

                        } else if (message.seen) {

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

        profileImage.setOnClickListener(v ->
                showComingSoonDialog(
                        "User Profile",
                        "User profile screen is coming soon.",
                        "OK",
                        null
                ));

        username.setOnClickListener(v ->
                showComingSoonDialog(
                        "User Profile",
                        "User profile screen is coming soon.",
                        "OK",
                        null
                ));

        menuBtn.setOnClickListener(v ->
                showComingSoonDialog(
                        "Chat Options",
                        "More chat options are coming soon.",
                        "OK",
                        null
                ));

        videoCallBtn.setOnClickListener(v ->
                showComingSoonDialog(
                        "Video Calls",
                        "Video calling will be available in a future HaloChat update.",
                        "OK",
                        null
                ));

        callBtn.setOnClickListener(v ->
                showComingSoonDialog(
                        "Voice Calls",
                        "Voice calling will be available in a future HaloChat update.",
                        "OK",
                        null
                ));


        messageBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                if (s.toString().trim().isEmpty()) {

                    sendIcon.setImageResource(
                            R.drawable.mic_icon
                    );

                } else {

                    sendIcon.setImageResource(
                            R.drawable.send_icon
                    );
                }
            }

            @Override
            public void afterTextChanged(
                    Editable s
            ) {}
        });

        emojiBtn.setOnClickListener(v ->
                showComingSoonDialog("Coming Soon",
                        "Emoji Picker section will be available in a future HaloChat update.",
                            "OK",
                        null));

        attachBtn.setOnClickListener(v ->
                showComingSoonDialog("Coming Soon",
                        "Attachement section will be available in a future HaloChat update.",
                        "OK",
                        null));

        cameraBtn.setOnClickListener(v ->
                showComingSoonDialog("Coming Soon",
                        "Camera section will be available in a future HaloChat update.",
                        "OK",
                        null));

        // =========================
        // SEND MESSAGE
        // =========================

        sendBtn.setOnClickListener(v -> {

            String text =
                    messageBox.getText()
                            .toString()
                            .trim();

            if (text.isEmpty()) {

                showComingSoonDialog(
                        "Coming Soon",
                        "Voice messages are coming soon.",
                        "OK",
                        null
                );

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
            message.status = "sending";

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

            entity.status = "sending";
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

            DatabaseReference root =
                    FirebaseDatabase.getInstance()
                            .getReference("Chats");

            Task<Void> senderTask =
                    root.child(senderRoom)
                            .child(messageId)
                            .setValue(message);

            Task<Void> receiverTask =
                    root.child(receiverRoom)
                            .child(messageId)
                            .setValue(message);

            Tasks.whenAll(senderTask, receiverTask)

                    .addOnSuccessListener(unused -> {

                        entity.status = "sent";

                        chatRepository.insertMessage(entity);

                    })

                    .addOnFailureListener(e -> {

                        entity.status = "failed";

                        chatRepository.insertMessage(entity);

                    });

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

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
            );

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
                    .setValue(ServerValue.TIMESTAMP);

        }
    }

    private void showComingSoonDialog(
            String title,
            String message,
            String buttonText,
            Runnable action
    ) {

        View dialogView =
                getLayoutInflater().inflate(
                        R.layout.dialog_coming_soon,
                        null
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
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

        MaterialButton okBtn =
                dialogView.findViewById(
                        R.id.okBtn
                );

        titleText.setText(title);

        messageText.setText(message);

        okBtn.setText(buttonText);

        okBtn.setOnClickListener(v -> {

            dialog.dismiss();

            if (action != null) {

                action.run();
            }
        });

        dialog.show();
    }

}