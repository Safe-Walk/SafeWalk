package com.sw.safewalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener{
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private GoogleMap mMap;
    private ArrayList<LatLng> crimeLocations = new ArrayList<>();
    private ArrayList<Marker> markerArray;
    Route routeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        markerArray = new ArrayList<>();


        // Caso tenha a permissão do usuário, seta para pegar a localização e adiciona o botão de mudar para a localização.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            //Caso não, pede ao usuário a permissão.
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                Marker auxMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                markerArray.add(auxMarker);
            }
        });

        // Botão para redirecionar para a activity de registrar ocorrência
        final FloatingActionButton btnIncident = (FloatingActionButton) findViewById(R.id.btnIncident);
        btnIncident.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIncident.class);
                startActivity(intent);
            }
        });
        final Button btnGetRoute =  findViewById(R.id.btnGetRoute);
        btnGetRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                routeManager = new Route(mMap);
                routeManager.sendRequest(markerArray, crimeLocations);
            }
        });

        final FloatingActionButton btnClearScreen =  findViewById(R.id.btnClearScreen);
        btnClearScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(Marker marker:  markerArray){
                    marker.remove();
                }
                markerArray = new ArrayList<Marker>();
                routeManager.clearLine();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("incidentes/listaOcorrencia");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()) {
                    Incident in = snap.getValue(Incident.class);
                    LatLng location = new LatLng(parseDouble(in.latitude.toString()), parseDouble(in.longitude.toString()));
                    crimeLocations.add(location);
                }

                for(int i = 0; i < crimeLocations.size(); i++) {
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center((LatLng) crimeLocations.get(i))
                            .radius(70)
                            .strokeColor(Color.RED)
                            .fillColor(Color.RED));
                   // mMap.addMarker(new MarkerOptions().position((LatLng) crimeLocations.get(i)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    
    @Override
    public void onBackPressed(){
        Intent exit = new Intent(Intent.ACTION_MAIN);
        exit.addCategory(Intent.CATEGORY_HOME);
        exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(exit);
    }
}