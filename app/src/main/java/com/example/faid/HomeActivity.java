package com.example.faid;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Toast;

import com.example.faid.models.CameraActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    View view;
    TextInputEditText et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermissions();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            String data = bundle.getString("state", "");
            if (data.equals("signup")) {
                Snackbar.make(findViewById(android.R.id.content), "Sign Up Successful", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Log In Successful", Snackbar.LENGTH_SHORT).show();
            }
            mAuth = FirebaseAuth.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            String email = mAuth.getCurrentUser().getEmail().toString();
            TextView profilenametv = findViewById(R.id.profilename);
            profilenametv.setText(email);
            et = findViewById(R.id.token);
            String token = et.getEditableText().toString();
        } catch (Exception e){
            mAuth = FirebaseAuth.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            String email = mAuth.getCurrentUser().getEmail().toString();
            TextView profilenametv = findViewById(R.id.profilename);
            profilenametv.setText(email);
            et = findViewById(R.id.token);
            String token = et.getEditableText().toString();
        }
    }


        private void checkPermissions() {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                       1001);
            }
            else {
                System.out.println("Permission is granted.");
            }
        }

        public void selectImage(View view) {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                // When an Image is picked
                if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    ImageView imgView = (ImageView) findViewById(R.id.profilepic);
                    // Set the Image in ImageView after decoding the String
                    imgView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));

                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }

        }

        public void goToLearn(View view) {
        startActivity(new Intent(HomeActivity.this,LearnActivity.class));
        }

        public void goToSymptom(View view) {
        startActivity(new Intent(HomeActivity.this, SymptomActivity.class));
            Intent intent = new Intent(this, SymptomActivity.class);
            String token= et.getText().toString();
            System.out.println(token);
            Bundle bun = new Bundle();
            if (token=="") {
                Toast.makeText(this, "No authorisation token received.", Toast.LENGTH_SHORT).show();
            }
            else {
                bun.putString("token", token);
                intent.putExtras(bun);
                startActivity(intent);
            }
    }

    public void goToDetect(View view) {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));
    }

    public void goToNearby(View view) {
        startActivity(new Intent(HomeActivity.this, MapsActivity.class));
    }
}



