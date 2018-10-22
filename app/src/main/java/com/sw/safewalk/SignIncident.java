package com.sw.safewalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class SignIncident extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private LatLng latLng;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_incident);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        Spinner dropdown = findViewById(R.id.crimeList);
        String[] items = new String[]{"Roubo", "Furto", "Assalto", "Perseguição", "Assédio", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
            markerOptions = new MarkerOptions().position(latLng);
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-15.7797, -47.9297);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Kathmandu, Nepal"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sydney.latitude, sydney.longitude), 15));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                markerOptions = new MarkerOptions().position(latLng).draggable(true);
                mMap.addMarker(markerOptions);
            }
        });


    }

    // Salva a ocorrência no firebase
    private void saveIncident() {
        final Button saveIncident = findViewById(R.id.saveIncident);
        saveIncident.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText crimeDescription;
                SeekBar crimeLevel;
                Spinner crimeList;
                Double latitude, longitude;

                crimeDescription = findViewById(R.id.crimeDescription);
                crimeLevel = findViewById(R.id.crimeLevel);
                crimeList = findViewById(R.id.crimeList);

                String descricao = crimeDescription.getText().toString().trim();
                Integer nivel = crimeLevel.getProgress();
                String crimeSelecionado = crimeList.getSelectedItem().toString().trim();

                if(validate(descricao, nivel, crimeSelecionado)) {
                    latitude = markerOptions.getPosition().latitude;
                    longitude = markerOptions.getPosition().longitude;

                    Incident incidentInfo = new Incident(crimeSelecionado, descricao, nivel, latitude, longitude);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference incident = database.getReference("incidentes");

                    incident.child("listaOcorrencia").push().setValue(incidentInfo);

                    Toast.makeText(SignIncident.this, "Obrigado por nos ajudar com as informações!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignIncident.this, "Favor preencher todos os campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Valida se os campos estiverem preenchidos
    private boolean validate(String description, Integer level, String selected) {
        return description != null && level != null && selected != null && markerOptions != null ? true : false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location){
        Toast.makeText(this, "current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

}
