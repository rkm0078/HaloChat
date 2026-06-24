package com.rishabh.chatapp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.rishabh.chatapp.database.DatabaseClient;
import com.rishabh.chatapp.database.dao.FriendDao;
import com.rishabh.chatapp.database.entity.FriendEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FriendRepository {

    private final FriendDao friendDao;

    private final LiveData<List<FriendEntity>> friends;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public FriendRepository(Application application) {

        friendDao = DatabaseClient
                .getInstance(application)
                .getDatabase()
                .friendDao();

        friends = friendDao.getFriends();
    }

    public LiveData<List<FriendEntity>> getFriends() {
        return friends;
    }

    public void insert(FriendEntity friend) {

        executor.execute(() ->
                friendDao.insert(friend));
    }

    public void delete(FriendEntity friend) {

        executor.execute(() ->
                friendDao.delete(friend));
    }

    public void deleteAll() {

        executor.execute(friendDao::deleteAll);
    }
}