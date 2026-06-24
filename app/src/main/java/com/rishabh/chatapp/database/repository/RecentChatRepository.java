package com.rishabh.chatapp.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.rishabh.chatapp.database.DatabaseClient;
import com.rishabh.chatapp.database.dao.ChatDao;
import com.rishabh.chatapp.database.entity.ChatEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecentChatRepository {

    private final ChatDao chatDao;

    private final ExecutorService executorService;

    public RecentChatRepository(
            Context context
    ) {

        chatDao =
                DatabaseClient
                        .getInstance(context)
                        .getDatabase()
                        .chatDao();

        executorService =
                Executors.newSingleThreadExecutor();
    }

    public LiveData<List<ChatEntity>> getChats() {

        return chatDao.getChats();
    }

    public void insertChat(
            ChatEntity chat
    ) {

        executorService.execute(() ->
                chatDao.insert(chat)
        );
    }

    public void deleteAllChats() {

        executorService.execute(
                chatDao::deleteAll
        );
    }
}