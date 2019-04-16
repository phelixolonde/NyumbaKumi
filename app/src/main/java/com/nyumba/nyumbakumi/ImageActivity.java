package com.nyumba.nyumbakumi;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    ImageView img;
    Uri uri;
    Button btnSend;
    DatabaseReference mRef;
    String groupId;
    EditText txtCaption;

    private StorageReference mStorage;
    StorageReference filepath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        uri = Uri.parse(getIntent().getExtras().getString("imageUri"));
        groupId = getIntent().getExtras().getString("groupId");
        mRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("chatrooms").child(groupId).child("chats");
        mStorage = FirebaseStorage.getInstance().getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));

        img = findViewById(R.id.img);
        btnSend = findViewById(R.id.btnSend);
        txtCaption = findViewById(R.id.txtCaption);
        //preveiew selected image
        img.setImageURI(uri);

        //send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Toast.makeText(ImageActivity.this, "Imag Uri is "+uri, Toast.LENGTH_SHORT).show();

                //upload photo first
                filepath = mStorage.child("Photos").child(uri.getLastPathSegment());


                Uri mUri = Uri.fromFile(new File(uri.toString()));

                //add file on Firebase and got Download Link
                filepath.putFile(mUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downUri = task.getResult();
                            //  Log.d(TAG, "onComplete: Url: "+ downUri.toString());

                            //save message to db
                            mRef.push()
                                    .setValue(new ChatMessage(txtCaption.getText().toString(),
                                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            downUri.toString())
                                    );
                            finish();
                        }
                    }
                });




            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
