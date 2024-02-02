package com.example.contactlist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private long contactID;

    @ColumnInfo(name = "contact_number")
    private long phoneNumber;

    @ColumnInfo(name = "contact_name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    private String photoPath;
    public long getContactID() { return contactID; }

    public void setContactID(long pID) { this.contactID = pID; }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long pNum) {
        this.phoneNumber = pNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email;}

    public void setEmail(String pEmail) { this.email = pEmail;}

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String path) {
        this.photoPath = path;
    }
}