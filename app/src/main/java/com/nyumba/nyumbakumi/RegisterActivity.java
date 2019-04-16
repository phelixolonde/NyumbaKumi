package com.nyumba.nyumbakumi;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText txtUsername, txtEmail, txtPassword;
    Button btnRegister;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtEmail = findViewById(R.id.txtEmail);
        btnRegister = findViewById(R.id.btnRegister);
        avi=findViewById(R.id.avi);
        mAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    register(txtEmail.getText().toString(), txtPassword.getText().toString(), txtUsername.getText().toString());
                }
            }
        });
    }

    private void register(final String email, String password, final String username) {

        avi.show();
        avi.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (!task.isSuccessful()) {
                        //Log.e("REGISTER_EX",task.getException().getMessage());
                        avi.hide();

                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        createNewUser(email, username,mAuth.getCurrentUser().getUid());
                    }
                } catch (Exception e) {
                    avi.hide();
                    Log.e("REGISTER_EX", e.getMessage(), e);
                }
                //hideProgressDialog();
            }
        });

    }

    private void createNewUser(String email, final String username, String uid) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("users").child(uid);

        mDatabase.child("email").setValue(email);
        mDatabase.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                avi.hide();
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validate() {
        boolean isValid;
        if (txtUsername.getText().toString().equals("")) {
            txtUsername.setError("Username is required");
            isValid = false;
        } else if (txtEmail.getText().toString().equals("")) {
            txtEmail.setError("Email is required");
            isValid = false;


        } else if (!isEmailValid(txtEmail.getText().toString())) {
            txtEmail.setError("Enter a valid email");
            isValid = false;
        } else if (txtPassword.getText().toString().equals("")) {
            txtPassword.setError("Password is required");
            isValid = false;
        }else if (txtPassword.getText().toString().length()<6){
            txtPassword.setError("Password must not be less than 6 characters");
            isValid=false;
        }
        else {
            isValid = true;
        }
        return isValid;
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
