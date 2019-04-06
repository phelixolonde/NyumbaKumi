package com.nyumba.nyumbakumi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

public class Login extends AppCompatActivity {

    EditText txtUsername, txtPassword;
    Button btnLogin;
    TextView tvRegister;
    AVLoadingIndicatorView avi;
    FirebaseAuth mAuth;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("users");

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        avi = findViewById(R.id.avi);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, RegisterActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    avi.setVisibility(View.VISIBLE);
                    avi.show();
                    login(txtUsername.getText().toString(), txtPassword.getText().toString());
                }
            }
        });
    }

    private void login(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    getUsername(email,mAuth.getCurrentUser().getUid());

                } else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    avi.hide();
                }
            }
        });
    }

    private void getUsername(final String email, String uid) {

        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child("username").getValue().toString();
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                avi.hide();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean validate() {
        boolean isValid;
        if (txtUsername.getText().toString().equals("")) {
            txtUsername.setError("Username is required");
            isValid = false;
        } else if (txtPassword.getText().toString().equals("")) {
            txtPassword.setError("Password is required");
            isValid = false;
        } else {
            isValid = true;
        }
        return isValid;
    }
}
