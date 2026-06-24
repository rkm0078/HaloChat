package com.rishabh.chatapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rishabh.chatapp.database.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserEntity> users);

    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM users WHERE uid=:uid LIMIT 1")
    LiveData<UserEntity> getUserById(String uid);

    @Query("DELETE FROM users")
    void deleteAll();

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    LiveData<UserEntity> getUser(String uid);

}