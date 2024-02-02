package com.example.contactlist;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 3) // if room cannot verify data integrity change this version
public abstract class ContactDataBase extends RoomDatabase {
    public abstract ContactDAO contactDAO();
}

