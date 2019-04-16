package com.nyumba.nyumbakumi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    String username = "";
    FirebaseAuth mAuth;
    FloatingActionButton fab;

    private static final String FRAGMENT_CHAT = "chat";
    private static final String FRAGMENT_HOME = "home";
    private static final String FRAGMENT_REPORT = "report";

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


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        fab = findViewById(R.id.fabAdd);

        //switching functionality of the floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                String tag = currentFragment.getTag();
                switch (tag) {
                    case FRAGMENT_CHAT:
                        ((ChatFragment) currentFragment).createNewGroup();
                        break;
                    case FRAGMENT_HOME:
                        ((HomeFragment) currentFragment).createNewPost();
                        break;
                    case FRAGMENT_REPORT:

                        break;
                    default:

                        break;
                }
            }
        });
        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.navigation_home); // change to whichever id should be default
        }
    }

    //switching fragments
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fab.show();
                    fragment = new HomeFragment();
                    ft.replace(R.id.content_frame, fragment, FRAGMENT_HOME);
                    ft.commit();
                    return true;
                case R.id.navigation_chat:
                    fab.show();
                    fragment = new ChatFragment();
                    ft.replace(R.id.content_frame, fragment, FRAGMENT_CHAT);
                    ft.commit();
                    return true;
                case R.id.navigation_report:
                    fab.hide();
                    fragment = new ReportFragment();
                    ft.replace(R.id.content_frame, fragment, FRAGMENT_REPORT);
                    ft.commit();
                    return true;
                default:
                    fab.show();
                    fragment = new HomeFragment();
                    ft.replace(R.id.content_frame, fragment, FRAGMENT_HOME);
                    ft.commit();
                    return true;
            }


        }
    };

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
            //logout action
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
