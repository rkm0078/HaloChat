package com.rishabh.chatapp.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.rishabh.chatapp.database.DatabaseClient;
import com.rishabh.chatapp.database.dao.MessageDao;
import com.rishabh.chatapp.database.entity.MessageEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {

    private final MessageDao messageDao;
    private final ExecutorService executorService;

    public ChatRepository(Context context) {

        messageDao = DatabaseClient
                .getInstance(context)
                .getDatabase()
                .messageDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    // Get conversation
    public LiveData<List<MessageEntity>> getMessages(String user1, String user2) {
        return messageDao.getMessages(user1, user2);
    }

    // Insert one message
    public void insertMessage(MessageEntity message) {
        executorService.execute(() -> messageDao.insert(message));
    }

    // Insert multiple messages
    public void insertMessages(List<MessageEntity> messages) {
        executorService.execute(() -> {
            for (MessageEntity message : messages) {
                messageDao.insert(message);
            }
        });
    }

    // Delete all cached messages
    public void deleteAllMessages() {
        executorService.execute(messageDao::deleteAll);
    }

}