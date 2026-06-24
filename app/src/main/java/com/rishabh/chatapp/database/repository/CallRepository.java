package com.rishabh.chatapp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.rishabh.chatapp.database.DatabaseClient;
import com.rishabh.chatapp.database.dao.CallDao;
import com.rishabh.chatapp.database.entity.CallEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallRepository {

    private final CallDao callDao;
    private final LiveData<List<CallEntity>> calls;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public CallRepository(Application application) {

        callDao = DatabaseClient
                .getInstance(application)
                .getDatabase()
                .callDao();

        calls = callDao.getCalls();
    }

    public LiveData<List<CallEntity>> getCalls() {
        return calls;
    }

    public void insert(CallEntity call) {

        executor.execute(() ->
                callDao.insert(call));
    }

    public void insertAll(List<CallEntity> callList) {

        executor.execute(() ->
                callDao.insertAll(callList));
    }

    public void clearCalls() {

        executor.execute(callDao::clearCalls);
    }
}