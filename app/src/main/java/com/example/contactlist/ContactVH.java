package com.example.contactlist;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactVH extends RecyclerView.ViewHolder{
    public TextView listName;
    public TextView listNumber;
    public TextView listEmail;

    public EditText listEditName;
    public EditText listEditNumber;
    public EditText listEditEmail;
    public Button deleteButton;
    public Button editButton;
    public Button saveButton;

    public ImageView photoPreview;
    public ContactVH(@NonNull View itemView) {
        super(itemView);
        listName = itemView.findViewById(R.id.listName);
        listNumber = itemView.findViewById(R.id.listNumber);
        listEmail = itemView.findViewById(R.id.listEmail);
        listEditName = itemView.findViewById(R.id.listEditName);
        listEditNumber = itemView.findViewById(R.id.listEditNumber);
        listEditEmail = itemView.findViewById(R.id.listEditEmail);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        editButton = itemView.findViewById(R.id.editContact);
        saveButton = itemView.findViewById(R.id.saveContact);
        photoPreview = itemView.findViewById(R.id.contactPhoto);
    }
}

