package com.nyumba.nyumbakumi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    String username = "";
    FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_chat:
                    mTextMessage.setText(R.string.title_chat);
                    return true;
                case R.id.navigation_report:
                    mTextMessage.setText(R.string.title_report);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
        username = getIntent().getExtras().getString("username");

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.username);
        item.setTitle(username);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.username) {
            View userView = findViewById(R.id.username);
            final PopupMenu popup = new PopupMenu(MainActivity.this, userView);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.account, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.signout) {
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    }

                    return true;
                }

            });
        }
        return super.onOptionsItemSelected(item);
    }
}
