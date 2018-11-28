package com.sw.safewalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class SortAndFilterActivity extends AppCompatActivity implements LocationListener {
    private RecyclerView mRecyclerView;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;

    private LocationManager locationManager;
    private String provider;
    private Location location;
    private int distance;
    private boolean recent, danger;
    private String typeOfCrime;
    private Long time;
    private ArrayList<Incident> sortArray = new ArrayList();
    private ArrayList<Incident> sortedArray = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_and_filter);

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, SortAndFilterActivity.FINE_LOCATION_PERMISSION_REQUEST);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        location = locationManager.getLastKnownLocation(provider);

        if (location != null){
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        LinearLayoutManager lln = new LinearLayoutManager(this);
        lln.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lln);

        danger = Boolean.parseBoolean(getIntent().getSerializableExtra("SortData").toString().split(" ")[0]);
        distance = parseInt(getIntent().getSerializableExtra("SortData").toString().split(" ")[1]);
        recent = Boolean.parseBoolean(getIntent().getSerializableExtra("SortData").toString().split(" ")[2]);
        typeOfCrime = getIntent().getSerializableExtra("SortData").toString().split(" ")[3];
        time = parseLong(getIntent().getSerializableExtra("SortData").toString().split(" ")[4]);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("incidentes/listaOcorrencia");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Incident in = snap.getValue(Incident.class);
                    sortArray.add(in);
                }
                Log.d("aqui", sortArray.toString());
                sortArray();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public ArrayList<Incident> filterByType(ArrayList<Incident> newArray) {

        ArrayList<Incident> anotherArray = new ArrayList<>();

        for(int i = 0; i < newArray.size(); i++) {
            if(typeOfCrime.equals(newArray.get(i).getCrimeSelecionado().toString())) {
                anotherArray.add(newArray.get(i));
            }
        }

        return anotherArray;
    }

    public ArrayList<Incident> filterByDate(ArrayList<Incident> newArray) {

        ArrayList<Incident> anotherArray = new ArrayList<>();

        if(time != 0) {
            long now = Calendar.getInstance().getTime().getTime();

            for(int i = 0; i < newArray.size(); i++) {
                long actual = newArray.get(i).getHorario();
                Log.d("AAAAAAAAAAAAAAA", Long.toString(Math.abs(now - actual) / (1000*60*60*24)));
                Log.d("BBBBB", Long.toString(time));
                if((Math.abs(now - actual) / (1000*60*60*24)) <= time) {
                    anotherArray.add(newArray.get(i));
                }
            }

            return anotherArray;
        } else {
            return newArray;
        }
    }

    public Double getDistanceFromLatLng(Double lat1, Double lat2, Double lon1, Double lon2) {

        Double R = 6371.0;
        Double dLat = deg2rad(lat2 - lat1);
        Double dLon = deg2rad(lon2 - lon1);

        Double a = Math.sin(dLat/2.0) * Math.sin(dLat/2.0) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon/2.0) * Math.sin(dLon/2.0);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = R * c;

        return d;
    }

    public Double deg2rad(Double deg) {
        return deg * (Math.PI/180);
    }

    ArrayList<Incident> filterByDistance(ArrayList<Incident> newArray){
        ArrayList<Incident> anotherArray = new ArrayList<Incident>();

        for(Incident inc: newArray){
            Log.d("Distancia", Double.valueOf(getDistanceFromLatLng(inc.getLatitude(), location.getLatitude(), inc.getLongitude(), location.getLongitude())).toString());
            if(getDistanceFromLatLng(inc.getLatitude(), location.getLatitude(), inc.getLongitude(), location.getLongitude()) <= distance){
                anotherArray.add(inc);
            }
        }
        return anotherArray;
    }

    public void sortArray() {

        ArrayList<Incident> filteredArray = sortArray;

        filteredArray = filterByDate(filteredArray);
        filteredArray = filterByType(filteredArray);
        filteredArray = filterByDistance(filteredArray);
        Log.d("ERREI", filteredArray.toString());

        if(danger) {
            CountingSort s = new CountingSort();
            s.sort(filteredArray);
            sortedArray = s.getSortedArray();
        } else if(recent) {
            QuickSort s = new QuickSort();
            s.sort(filteredArray, 0, filteredArray.size() - 1);
            sortedArray = s.getSortedArray();
        }

        MyAdapter adapter = new MyAdapter(this, sortedArray);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Diasbled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
}
