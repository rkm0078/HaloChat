package com.rishabh.chatapp.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.rishabh.chatapp.database.DatabaseClient;
import com.rishabh.chatapp.database.dao.UserDao;
import com.rishabh.chatapp.database.entity.UserEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {

        userDao = DatabaseClient
                .getInstance(context)
                .getDatabase()
                .userDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<UserEntity>> getUsers() {
        return userDao.getAllUsers();
    }

    public void insertUser(UserEntity user) {

        executorService.execute(() -> {
            userDao.insert(user);
        });

    }

    public void insertUsers(List<UserEntity> users) {

        executorService.execute(() -> {

            for (UserEntity user : users) {
                userDao.insert(user);
            }

        });
    }

    public LiveData<UserEntity> getUser(String uid) {
        return userDao.getUser(uid);
    }

    public void deleteAllUsers() {
        executorService.execute(() -> userDao.deleteAll());
    }

}