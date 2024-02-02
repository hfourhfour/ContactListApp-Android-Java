package com.example.contactlist;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

public class ContactDBInstance{
    private static ContactDataBase database;

    public static ContactDataBase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context,
                            ContactDataBase.class, "contact_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // or addMigrations()? fall back may result in data loss if there are existing items in database
                    .build();
        }
        return database;
    }
}


