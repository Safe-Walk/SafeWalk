package com.sw.safewalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignIncident extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng latLng;
    private EditText crimeDescription;
    private SeekBar crimeLevel;
    private Spinner crimeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        crimeDescription = findViewById(R.id.crimeDescription);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_incident);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        Spinner dropdown = findViewById(R.id.crimeList);
        String[] items = new String[]{"Roubo", "Furto", "Assalto", "Perseguição", "Assédio", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        saveIncident();
    }

    public void onMapSearch(View view) {
        mMap.clear();
        EditText locationSearch = (EditText) findViewById(R.id.crimeLocation);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(27.746974, 85.301582);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Kathmandu, Nepal"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    // Salva a ocorrência no firebase
    private void saveIncident() {
        final Button saveIncident = findViewById(R.id.saveIncident);
        saveIncident.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                crimeDescription = findViewById(R.id.crimeDescription);
                crimeLevel = findViewById(R.id.crimeLevel);
                crimeList = findViewById(R.id.crimeList);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference incident = database.getReference("incidentes");

                incident.child("registrar").child("descricaoCrime").setValue(crimeDescription.getText().toString());
                incident.child("registrar").child("nivelCrime").setValue(crimeLevel.getProgress());
                incident.child("registrar").child("tipoCrime").setValue(crimeList.getSelectedItem().toString());
                incident.child("registrar").child("localCrime").setValue(latLng);

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
