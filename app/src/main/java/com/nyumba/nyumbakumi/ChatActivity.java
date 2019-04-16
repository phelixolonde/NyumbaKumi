package com.nyumba.nyumbakumi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private static final int RequestCode = 100;
    private ListView listView;
    private View btnSend;
    private EditText editText;
    private MessageAdapter adapter;
    String groupName, groupId;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    ImageView imgCam;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        groupName = getIntent().getExtras().getString("groupName");
        groupId = getIntent().getExtras().getString("groupId");
        mRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("chatrooms").child(groupId).child("chats");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(groupName);
        }
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, Login.class));
        }

        listView = findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        editText = findViewById(R.id.msg_type);
        imgCam = findViewById(R.id.imgCamera);

        //satrt image sleection
        imgCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(ChatActivity.this, Options.init().setRequestCode(100));

            }
        });


        //set ListView adapter first

        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_chat_left, mRef);
        listView.setAdapter(adapter);


        // User is already signed in, show list of messages

        showAllOldMessages();


        //event for button SEND
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    mRef
                            .push()
                            .setValue(new ChatMessage(editText.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),"")

                            );
                    editText.setText("");
                }
            }
        });
    }

    private String loggedInUserName = "";

    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);


        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_chat_left,
                mRef);
        listView.setAdapter(adapter);

    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

           Intent intent=new Intent(ChatActivity.this,ImageActivity.class);
           intent.putExtra("imageUri",returnValue.get(0));
           intent.putExtra("groupId",groupId);
           startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ChatActivity.this, Options.init().setRequestCode(100));
                } else {
                    Toast.makeText(ChatActivity.this, "Approve permissions to select picture", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}

