package com.example.meetu.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meetu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {

    EditText post_title, post_description;
    Button upload_post;
    ImageView post_image;

    Uri image_uri = null;
    private static final int GALLERY_IMAGE_CODE = 100;
    private static final int CAMERA_IMAGE_CODE = 200;

    ProgressDialog pd;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        permission();
        post_title = findViewById(R.id.post_title);
        post_description = findViewById(R.id.post_description);
        upload_post = findViewById(R.id.upload_post);
        post_image = findViewById(R.id.post_image);
        pd = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_add_post);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_post:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(AddPostActivity.this, HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(AddPostActivity.this, ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(AddPostActivity.this, WelcomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        upload_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String title = post_title.getText().toString();
               String description = post_description.getText().toString();

               if (TextUtils.isEmpty(title)) {
                   post_title.setError("Title is required!");

               } else if (TextUtils.isEmpty(description)) {
                   post_description.setError("Description is required!");
               } else {
                   uploadData(title, description);
               }
            }
        });

    }

    private void uploadData(String title, String description) {

        pd.setMessage("Publishing new post");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePath = "Posts/post_" + timeStamp;

        if(post_image.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) post_image.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePath);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();

                                HashMap<String, Object> hashMap = new HashMap<>();

                                hashMap.put("uid", user.getUid());
                                hashMap.put("userEmail", user.getEmail());
                                hashMap.put("postId", timeStamp);
                                hashMap.put("postTitle", title);
                                hashMap.put("postImage", downloadUri);
                                hashMap.put("postDescription", description);
                                hashMap.put("postTime", timeStamp);

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                                databaseReference.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Post published with success!", Toast.LENGTH_SHORT).show();
                                                post_title.setText("");
                                                post_description.setText("");
                                                post_image.setImageURI(null);
                                                image_uri = null;

                                                startActivity(new Intent(AddPostActivity.this, HomeActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();;
                                                Toast.makeText(AddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }

    private void imagePickDialog() {
        
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose upload method");
        
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    cameraPic();
                }
                if(which == 1) {
                    galleryPic();
                }
            }
        });

        builder.create().show();
    }

    private void cameraPic() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp desc");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, CAMERA_IMAGE_CODE);
    }

    private void galleryPic() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_CODE);
    }

    private void permission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == GALLERY_IMAGE_CODE) {
                image_uri = data.getData();
                post_image.setImageURI(image_uri);
            }
            if(requestCode == CAMERA_IMAGE_CODE) {
                post_image.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}