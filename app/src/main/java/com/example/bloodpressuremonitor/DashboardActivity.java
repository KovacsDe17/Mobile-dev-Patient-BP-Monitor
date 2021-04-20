package com.example.bloodpressuremonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private static final String LOG_TAG = DashboardActivity.class.getName();

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            Log.d(LOG_TAG,"Authenticated user!");
        } else {
            Log.d(LOG_TAG,"Unauthenticated user!");
            finish();
        }
    }
}