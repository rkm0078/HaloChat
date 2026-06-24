package com.rishabh.chatapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rishabh.chatapp.database.dao.CallDao;
import com.rishabh.chatapp.database.dao.ChatDao;
import com.rishabh.chatapp.database.dao.FriendDao;
import com.rishabh.chatapp.database.dao.MessageDao;
import com.rishabh.chatapp.database.dao.UserDao;
import com.rishabh.chatapp.database.entity.CallEntity;
import com.rishabh.chatapp.database.entity.ChatEntity;
import com.rishabh.chatapp.database.entity.FriendEntity;
import com.rishabh.chatapp.database.entity.MessageEntity;
import com.rishabh.chatapp.database.entity.UserEntity;

@Database(
        entities = {
                UserEntity.class,
                MessageEntity.class,
                ChatEntity.class,
                FriendEntity.class,
                CallEntity.class,
        },
        version = 4,
        exportSchema = false
)
public abstract class HaloDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract MessageDao messageDao();

    public abstract ChatDao chatDao();

    public abstract FriendDao friendDao();

    public abstract CallDao callDao();
}