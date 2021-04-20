package com.example.bloodpressuremonitor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.icu.util.Measure;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    private static final String LOG_TAG = DashboardActivity.class.getName();

    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<MeasureItem> mItemList;
    private MeasureItemAdapter mAdapter;

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

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemList = new ArrayList<>();

        mAdapter = new MeasureItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        initalizeData();
    }

    private void initalizeData() {
        String[] date = getResources().getStringArray(R.array.dates);
        String[] timeOfDay = getResources().getStringArray(R.array.times);
        TypedArray systolic = getResources().obtainTypedArray(R.array.sys);
        TypedArray diastolic = getResources().obtainTypedArray(R.array.dia);
        TypedArray pulse = getResources().obtainTypedArray(R.array.pulse);

        mItemList.clear();

        for(int i=0; i<date.length;i++){
            mItemList.add(new MeasureItem(
                    LocalDate.parse(date[i]),
                    timeOfDay[i],
                    systolic.getInt(i,120),
                    diastolic.getInt(i,80),
                    pulse.getInt(i,70)
            ));
        }

        systolic.recycle();
        diastolic.recycle();
        pulse.recycle();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.measure_list_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView  = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Intent intent = new Intent(this, UpdateMeasure.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                Toast.makeText(this, "Még nincsenek beállítások", Toast.LENGTH_SHORT).show();
                //TODO beállítások activity
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Kijelentkezve", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}