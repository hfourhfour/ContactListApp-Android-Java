package com.example.contactlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactVH> {

    List<Contact> contactList;
    ContactDAO contactDAO;
    public ContactAdapter(List<Contact> contactList, ContactDAO contactDAO){
        this.contactList = contactList;
        this.contactDAO = contactDAO;
    }

    @NonNull
    @Override
    public ContactVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_layout,parent,false);
        ContactVH contactVHolder = new ContactVH(view);
        return contactVHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactVH holder, int position) {

        Contact contact = contactList.get(position);
        holder.listName.setText(contact.getName());
        holder.listEmail.setText(contact.getEmail());
        holder.listNumber.setText(Long.toString(contact.getPhoneNumber()));
        holder.listEditName.setVisibility(View.GONE);
        holder.listEditNumber.setVisibility(View.GONE);
        holder.listEditEmail.setVisibility(View.GONE);

        holder.photoPreview.setImageResource(R.drawable.gallery);
        String photoFilePath = contact.getPhotoPath();
        if (photoFilePath != null) {
            File contactPhoto = new File(photoFilePath);
            if (contactPhoto.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(contactPhoto.getAbsolutePath());
                holder.photoPreview.setImageBitmap(myBitmap);
            }
        }

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // swap TextViews for EditTexts to get user alterations
                holder.listEditName.setText(contact.getName());
                holder.listEditEmail.setText(contact.getEmail());
                holder.listEditNumber.setText(Long.toString(contact.getPhoneNumber()));
                holder.listEditName.setVisibility(View.VISIBLE);
                holder.listEditNumber.setVisibility(View.VISIBLE);
                holder.listEditEmail.setVisibility(View.VISIBLE);
                holder.listName.setVisibility(View.GONE);
                holder.listEmail.setVisibility(View.GONE);
                holder.listNumber.setVisibility(View.GONE);

                // swap buttons
                holder.editButton.setVisibility(View.GONE);
                holder.saveButton.setVisibility(View.VISIBLE);
                }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save altered information fields for saving/updating
                String updatedName = holder.listEditName.getText().toString();
                String updatedEmail = holder.listEditEmail.getText().toString();
                long updatedNumber = Long.parseLong(holder.listEditNumber.getText().toString());
                holder.listEditName.setVisibility(View.GONE);
                holder.listEditNumber.setVisibility(View.GONE);
                holder.listEditEmail.setVisibility(View.GONE);

                // update contact in database with altered information
                contact.setName(updatedName);
                contact.setEmail(updatedEmail);
                contact.setPhoneNumber(updatedNumber);
                contactDAO.update(contact);


                // update list text for screen
                holder.listName.setText(updatedName);
                holder.listEmail.setText(updatedEmail);
                holder.listNumber.setText(holder.listEditNumber.getText());
                holder.listName.setVisibility(View.VISIBLE);
                holder.listEmail.setVisibility(View.VISIBLE);
                holder.listNumber.setVisibility(View.VISIBLE);

                // swap buttons
                holder.editButton.setVisibility(View.VISIBLE);
                holder.saveButton.setVisibility(View.GONE);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDAO.delete(contact);
                contactList.remove(contact);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
