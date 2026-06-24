package com.rishabh.chatapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rishabh.chatapp.database.entity.FriendEntity;

import java.util.List;

@Dao
public interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FriendEntity friend);

    @Query("SELECT * FROM friends")
    LiveData<List<FriendEntity>> getFriends();

    @Query("DELETE FROM friends")
    void deleteAll();

    @Delete
    void delete(FriendEntity friend);
}