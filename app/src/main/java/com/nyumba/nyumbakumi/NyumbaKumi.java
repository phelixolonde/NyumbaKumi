package com.nyumba.nyumbakumi;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class NyumbaKumi extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //offline capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
