package com.example.bloodpressuremonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    private EditText emailET;
    private EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if(email == null || password == null || email.equals("") || password.equals(""))
            return;

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"Sikeres bejelentkezés!");
                    Toast.makeText(MainActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                    startDashboard();
                } else {
                    Log.d(LOG_TAG,"Sikertelen bejelentkezés: " + task.getException().getMessage());
                    Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

        Log.i(LOG_TAG,"Bejelentkezett: " + email + ", jelszó: " + password);
    }

    private void startDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("email", emailET.getText().toString());
        editor.apply();
    }
}