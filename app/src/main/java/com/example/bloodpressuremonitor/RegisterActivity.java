package com.example.bloodpressuremonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 42069;

    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;
    CheckBox dataCB;
    Spinner genderSpinner;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secretKey = getIntent().getIntExtra("SECRET_KEY",0);

        if(secretKey != 42069)
            finish();

        usernameET = findViewById(R.id.editTextNameReg);
        emailET = findViewById(R.id.editTextEmailReg);
        passwordET = findViewById(R.id.editTextPasswordReg);
        passwordAgainET = findViewById(R.id.editTextPasswordAgainReg);
        dataCB = findViewById(R.id.dataCheckBox);
        genderSpinner = findViewById(R.id.genderSpinner);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String email = preferences.getString("email","");
        emailET.setText(email);

        genderSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

    }

    public void register(View view) {
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();
        boolean confirmData = dataCB.isChecked();
        String gender = genderSpinner.getSelectedItem().toString();

        if(password.equals("") || password == null){
            Toast.makeText(this, "A jelszó megadása kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(passwordAgain)){
            Toast.makeText(this, "A két jelszó nem egyezik!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!confirmData){
            Toast.makeText(this, "Beleegyezés nélkül nem lehet regisztrálni!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(LOG_TAG,"Regisztrált: " + username + ", email: " + email + ", jelszó: " + password);

        //TODO regisztrációs cuclit megírni
        startDashboard();
    }

    public void cancel(View view) {
        finish();
    }

    private void startDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}