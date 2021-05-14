package com.example.bloodpressuremonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();

    EditText nameET;
    EditText identifierET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;
    CheckBox dataCB;
    Spinner genderSpinner;

    //TODO Firestore-ral felvenni a Patient-et

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private CollectionReference mPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameET = findViewById(R.id.editTextNameReg);
        identifierET = findViewById(R.id.editTextIdReg);
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

        mAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();
        mPatients = mFirestore.collection("Patients");
    }

    public void register(View view) {
        String name = nameET.getText().toString();
        String id = identifierET.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();
        boolean confirmData = dataCB.isChecked();

        if(name.equals("") || name == null){
            Toast.makeText(this, "A név megadása kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(id.equals("") || id == null){
            Toast.makeText(this, "A TAJ szám megadása kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        int intId = Integer.parseInt(id);

        if(email.equals("") || email == null){
            Toast.makeText(this, "Az e-mail cím megadása kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

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

        addUser(name, intId, gender, email, password);

        Log.i(LOG_TAG,"Regisztrált: " + name + ", email: " + email + ", jelszó: " + password);
    }

    public void cancel(View view) {
        finish();
    }

    private void addUser(String name, int id, String gender, String email, String password){
        mPatients.add(new Patient(
                id,
                name,
                gender,
                email
        )).addOnSuccessListener(success -> {
            createAuth(email,password);
        }).addOnFailureListener(failure -> {
            Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void createAuth(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"User created successfully!");
                    Toast.makeText(RegisterActivity.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                    startDashboard();
                } else {
                    Log.d(LOG_TAG,"User creation failed: " + task.getException().getMessage());
                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void startDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}