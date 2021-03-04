package com.thf.journal;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

public class DatabaseClient { //creates singleton pattern for database

    private static DatabaseClient instance;

    private AppDatabase appDatabase;

    private DatabaseClient(Context context){

        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "journaldatabase.db").createFromAsset("database/sample.db").build();
    }

    public static synchronized DatabaseClient getInstance(Context context){
        if(instance == null){
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getDatabase(){
        return appDatabase;
    }
}
