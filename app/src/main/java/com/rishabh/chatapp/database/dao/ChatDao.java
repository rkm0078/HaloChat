package com.rishabh.chatapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rishabh.chatapp.database.entity.ChatEntity;

import java.util.List;

@Dao
public interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatEntity chat);

    @Query("SELECT * FROM chats ORDER BY lastTimestamp DESC")
    LiveData<List<ChatEntity>> getChats();

    @Query("DELETE FROM chats")
    void deleteAll();

}