package com.example.bloodpressuremonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class UpdateMeasure extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = UpdateMeasure.class.getName();

    private FirebaseFirestore mFirestore;
    private CollectionReference mMeasures;
    private MeasureItem currentMeasure;

    private int userId;
    private EditText dateET;
    private EditText sysET;
    private EditText diaET;
    private EditText pulseET;
    private Spinner timeOfDaySpinner;

    private TextView title;
    private Button updateButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_measure);

        mFirestore = FirebaseFirestore.getInstance();
        mMeasures = mFirestore.collection("Measures");

        String id = getIntent().getStringExtra("id");
        userId = getIntent().getIntExtra("userId",0);

        dateET = findViewById(R.id.editTextDate);
        sysET = findViewById(R.id.editTextSys);
        diaET = findViewById(R.id.editTextDia);
        pulseET = findViewById(R.id.editTextPulse);
        timeOfDaySpinner = findViewById(R.id.timeOfDaySpinner);

        timeOfDaySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.timesOfDay, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeOfDaySpinner.setAdapter(adapter);

        findViewById(R.id.cancelMeasureButton).setOnClickListener(view -> finish());

        if(id == null || id.equals("")){
            title = findViewById(R.id.updateTextView);
            updateButton = findViewById(R.id.updateMeasureButton);
            deleteButton = findViewById(R.id.deleteMeasureButton);

            title.setText(R.string.newMeasure);
            updateButton.setText(R.string.record);
            deleteButton.setVisibility(View.INVISIBLE);

            timeOfDaySpinner.setSelection(MeasureItem.currentTimeOfDay());
            dateET.setText(MeasureItem.currentDate());

            updateButton.setOnClickListener(view -> create(view));
        } else {
            Log.d(LOG_TAG,"id: " + id);
            DocumentReference ref = mMeasures.document(id);

            ref.get().addOnSuccessListener(doc -> {

                currentMeasure = doc.toObject(MeasureItem.class);

                if(currentMeasure != null){
                    currentMeasure.setId(id);

                    dateET.setText(currentMeasure.getDate());
                    sysET.setText(Integer.toString(currentMeasure.getSystolic()));
                    diaET.setText(Integer.toString(currentMeasure.getDiastolic()));
                    pulseET.setText(Integer.toString(currentMeasure.getPulse()));

                    String[] times = getResources().getStringArray(R.array.timesOfDay);
                    int index = Arrays.asList(times).indexOf(currentMeasure.getTimeOfDay());
                    timeOfDaySpinner.setSelection(index);
                }
            })
            .addOnFailureListener(failure -> {
                Toast.makeText(this, "Nem található a mérés: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void create(View view){
        currentMeasure = new MeasureItem(
                userId,
                dateET.getText().toString(),
                timeOfDaySpinner.getSelectedItem().toString(),
                Integer.parseInt(sysET.getText().toString()),
                Integer.parseInt(diaET.getText().toString()),
                Integer.parseInt(pulseET.getText().toString())
        );

        mMeasures.add(currentMeasure).addOnSuccessListener(success -> {
            Toast.makeText(this, "Sikeres felvétel!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Sikertelen felvétel: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
        });

        finish();
    }

    public void update(View view){
        DocumentReference ref = mMeasures.document(currentMeasure._getId());

        ref.update(
                "date", dateET.getText().toString(),
                "timeOfDay", timeOfDaySpinner.getSelectedItem().toString(),
                "systolic", Integer.parseInt(sysET.getText().toString()),
                "diastolic", Integer.parseInt(diaET.getText().toString()),
                "pulse", Integer.parseInt(pulseET.getText().toString())
        ).addOnSuccessListener(success -> {
            Toast.makeText(this, "Sikeres módosítás!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Sikertelen módosítás: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
        });

        finish();
    }

    public void delete(View view){
        DocumentReference ref = mMeasures.document(currentMeasure._getId());

        ref.delete().addOnSuccessListener(success -> {
            Toast.makeText(this, "Sikeres törlés!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Sikertelen törlés: " + failure.getMessage(), Toast.LENGTH_SHORT).show();
        });

        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}