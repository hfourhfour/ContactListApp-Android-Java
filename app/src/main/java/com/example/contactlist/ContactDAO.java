package com.example.contactlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {
    @Insert
    void insert(Contact... contact);

    @Update
    void update(Contact... contact);

    @Delete
    void delete(Contact... contact);

    @Query("SELECT * FROM Contacts")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM Contacts WHERE contact_name = :contactName")
    List<Contact> getContactsByName(String contactName);

    @Query("SELECT * FROM Contacts WHERE contact_number = :contactNo")
    Contact getContactbyNo(int contactNo);
}



