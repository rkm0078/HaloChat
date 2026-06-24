package com.rishabh.chatapp.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient instance;
    private final HaloDatabase database;

    private DatabaseClient(Context context) {

        database = Room.databaseBuilder(
                        context.getApplicationContext(),
                        HaloDatabase.class,
                        "halochat_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseClient(context);
        }

        return instance;
    }

    public HaloDatabase getDatabase() {
        return database;
    }
}