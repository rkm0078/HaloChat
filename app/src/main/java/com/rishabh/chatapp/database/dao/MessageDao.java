package com.rishabh.chatapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rishabh.chatapp.database.entity.MessageEntity;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MessageEntity> messages);

    @Query("SELECT * FROM messages WHERE (senderId=:user1 AND receiverId=:user2) OR (senderId=:user2 AND receiverId=:user1) ORDER BY timestamp ASC")
    LiveData<List<MessageEntity>> getMessages(String user1, String user2);

    @Query("DELETE FROM messages")
    void deleteAll();

}