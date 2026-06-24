package com.rishabh.chatapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rishabh.chatapp.database.entity.CallEntity;

import java.util.List;

@Dao
public interface CallDao {

    @Query("SELECT * FROM calls ORDER BY timestamp DESC")
    LiveData<List<CallEntity>> getCalls();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CallEntity call);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CallEntity> calls);

    @Query("DELETE FROM calls")
    void clearCalls();
}