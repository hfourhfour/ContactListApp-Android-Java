package com.example.contactlist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ContactList listFrag = new ContactList();
    File cameraPhoto;
    ImageView photoView;

    EditText contactName;
    EditText contactEmail;
    EditText contactNumber;

    int contactID;

    private static final int REQUEST_READ_CONTACT_PERMISSION = 3;
    ActivityResultLauncher<Intent> photoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Bitmap photo = BitmapFactory.decodeFile(cameraPhoto.toString());
                    photoView.setImageBitmap(photo);
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContactDAO contactDAO = ContactDBInstance.getDatabase(getApplicationContext()).contactDAO();
        findViewById(R.id.topText).setVisibility(View.VISIBLE);
        findViewById(R.id.contactName).setVisibility(View.VISIBLE);
        findViewById(R.id.contactEmail).setVisibility(View.VISIBLE);
        findViewById(R.id.contactNo).setVisibility(View.VISIBLE);
        findViewById(R.id.cameraButton).setVisibility(View.VISIBLE);
        findViewById(R.id.addContact).setVisibility(View.VISIBLE);
        findViewById(R.id.toList).setVisibility(View.VISIBLE);

        EditText contactName = findViewById(R.id.contactName);
        EditText contactEmail = findViewById(R.id.contactEmail);
        EditText contactNumber = findViewById(R.id.contactNo);
        Button cameraButton = findViewById(R.id.cameraButton);
        Button addButton = findViewById(R.id.addContact);
        Button listButton = findViewById(R.id.toList);
        Button importButton = findViewById(R.id.importContact);
        contactName.setText("");
        contactEmail.setText("");
        contactNumber.setText("");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberCheck = contactNumber.getText().toString();
                if (!phoneNumberCheck.isEmpty())
                {
                    try {
                        long number = Long.parseLong(phoneNumberCheck); // check number for valid format, otherwise exception thrown

                        // check number for duplicate in list
                        Contact existingNumber = contactDAO.getContactbyNo(Integer.parseInt(phoneNumberCheck));
                        if (existingNumber != null) {
                            Toast toast = Toast.makeText(MainActivity.this,
                                    "This number already exists in your contact list.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        // else continue with adding contact
                        else {
                            String name = contactName.getText().toString();
                            String email = contactEmail.getText().toString();
                            Contact newContact = new Contact();
                            newContact.setName(name);
                            newContact.setEmail(email);
                            newContact.setPhoneNumber(number);

                            if (cameraPhoto != null) {
                                newContact.setPhotoPath(cameraPhoto.getAbsolutePath());
                            }

                            contactDAO.insert(newContact);
                            Toast toast = Toast.makeText(MainActivity.this,
                                    "A contact has been added!", Toast.LENGTH_SHORT);
                            toast.show();

                            // clear after adding
                            contactName.setText("");
                            contactEmail.setText("");
                            contactNumber.setText("");
                            if (photoView != null) {
                                photoView.setImageResource(R.drawable.gallery);
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        Toast toast = Toast.makeText(MainActivity.this,
                                "Your phone number is in an invalid format, try again.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "Your contact must have a phone number, try again.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContactList();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoView = findViewById(R.id.photoView);
                String fileName = "photo" + System.currentTimeMillis() + ".jpg";
                cameraPhoto = new File(getFilesDir(),fileName);
                Uri cameraUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() +".fileprovider", cameraPhoto);
                Intent photoIntent = new Intent();
                photoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);

                PackageManager pm = getPackageManager();
                for(ResolveInfo a : pm.queryIntentActivities(
                        photoIntent, PackageManager.MATCH_DEFAULT_ONLY)) {

                    grantUriPermission(a.activityInfo.packageName, cameraUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                    photoLauncher.launch(photoIntent);
                }
            });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_READ_CONTACT_PERMISSION);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(ContactsContract.Contacts.CONTENT_URI);
                    pickContactLauncher.launch(intent);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT_PERMISSION);
                }
            }
        });

    }

    public void loadContactList() {
        // remember to use intent/new intent get activity to move from fragment to main
        int screenOrientation = getResources().getConfiguration().orientation;
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.listContainer);

        findViewById(R.id.topText).setVisibility(View.GONE);
        findViewById(R.id.contactName).setVisibility(View.GONE);
        findViewById(R.id.contactEmail).setVisibility(View.GONE);
        findViewById(R.id.contactNo).setVisibility(View.GONE);
        findViewById(R.id.cameraButton).setVisibility(View.GONE);
        findViewById(R.id.addContact).setVisibility(View.GONE);
        findViewById(R.id.toList).setVisibility(View.GONE);
        findViewById(R.id.photoView).setVisibility(View.GONE);
        findViewById(R.id.importContact).setVisibility(View.GONE);

        if(frag==null){
            fm.beginTransaction().add(R.id.listContainer,listFrag).commit();
        }
        else{
            fm.beginTransaction().replace(R.id.listContainer,listFrag).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_READ_CONTACT_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Contacts GRANTED",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                pickContactLauncher.launch(intent);
            }
            else {
                // Permission denied, inform the user.
                Toast.makeText(MainActivity.this, "Contacts permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ActivityResultLauncher<Intent> pickContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Uri contactUri = data.getData();

                    // the fields/info i want
                    String[] queryFields = {
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                    };

                    Cursor c = getContentResolver().query(
                            contactUri, queryFields, null, null, null);

                    try {
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                            this.contactID = c.getInt(0);
                            contactName = findViewById(R.id.contactName);
                            contactName.setText(c.getString(1)); // name first
                        }
                    }
                    finally {
                        c.close();
                    }

                    Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                    Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] emailField = new String[] {
                            ContactsContract.CommonDataKinds.Email.ADDRESS,

                    };
                    String[] phoneField = new String[] {
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    };

                    String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
                    String [] whereValues = new String[]{
                            String.valueOf(this.contactID)
                    };

                    Cursor emailCursor = getContentResolver().query(
                            emailUri, emailField, whereClause,whereValues, null);
                    Cursor phoneCursor = getContentResolver().query(
                            phoneUri, phoneField, whereClause,whereValues, null);
                    try{
                        emailCursor.moveToFirst();
                        phoneCursor.moveToFirst();
                        do{
                            String emailAddress = emailCursor.getString(0);
                            String phoneNumber = phoneCursor.getString(0);
                            contactEmail = findViewById(R.id.contactEmail);
                            contactEmail.setText(emailAddress);
                            contactNumber = findViewById(R.id.contactNo);
                            contactNumber.setText(phoneNumber);
                        }
                        while (emailCursor.moveToNext() && phoneCursor.moveToNext());
                    }
                    finally {
                        emailCursor.close();
                        phoneCursor.close();
                    }
                }
            });
}



