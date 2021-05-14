package com.example.bloodpressuremonitor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.TypedArray;
import android.icu.util.Measure;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class DashboardActivity extends AppCompatActivity {
    private static final String LOG_TAG = DashboardActivity.class.getName();

    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<MeasureItem> mItemList;
    private MeasureItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mMeasures;
    private CollectionReference mPatients;
    private Patient mCurrentPatient;

    private NotificationHandler mNotificationHandler;
    private AlarmManager mAlarmManager;
    private JobScheduler mJobscheduler;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            Log.d(LOG_TAG,"Authenticated user!");
        } else {
            Log.d(LOG_TAG,"Unauthenticated user!");
            Toast.makeText(this, "Sikertelen bejelentkezés!",Toast.LENGTH_SHORT).show();
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemList = new ArrayList<>();

        mAdapter = new MeasureItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mMeasures = mFirestore.collection("Measures");
        mPatients = mFirestore.collection("Patients");

        mNotificationHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mJobscheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        getPatient();
        mNotificationHandler.cancel();
        //TODO visszakapcsolni
        //mNotificationHandler.send("Ne felejtsd el megmérni a vérnyomásod!");

        //setAlarmManager();
        setJobScheduler();
    }

    private void getPatient(){
        mPatients
                .whereEqualTo("email",user.getEmail())
                .get()
                .addOnSuccessListener(success ->{
                    mCurrentPatient = success.toObjects(Patient.class).get(0);
                    mCurrentPatient.setId(success.toObjects(Patient.class).get(0)._getId());
                    queryData();
                }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Sikertelen bejelentkezés: " + failure.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void queryData(){
        mItemList.clear();

        mMeasures.whereEqualTo("userId",mCurrentPatient.getIdentifier())
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                        MeasureItem measure = document.toObject(MeasureItem.class);
                        measure.setId(document.getId());
                        mItemList.add(measure);
                    }

            mAdapter.notifyDataSetChanged();
        });
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
                intent.putExtra("userId",mCurrentPatient.getIdentifier());
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

    @Override
    protected void onResume() {
        super.onResume();

        if(mCurrentPatient != null)
            queryData();
    }

    private void setAlarmManager(){
        long repeatInterval = 15 * 1000;    //15mp
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent
        );

        //mAlarmManager.cancel(pendingIntent);      <-- ezzel lehet leállítani
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJobScheduler(){
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        int hardDeadLine = 5 * 1000;

        ComponentName name = new ComponentName(getPackageName(),NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, name)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(true)
                .setOverrideDeadline(hardDeadLine);

        mJobscheduler.schedule(builder.build());
        //mJobscheduler.cancel(0);      <-- ezzel lehet leállítani
    }
}