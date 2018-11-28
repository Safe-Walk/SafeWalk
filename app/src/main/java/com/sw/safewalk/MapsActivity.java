package com.sw.safewalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, LocationListener {
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private GoogleMap mMap;
    private ArrayList<LatLng> crimeLocations = new ArrayList<>();
    private ArrayList<Integer> crimeWeight   = new ArrayList<>();
    private ArrayList<Marker> markerArray;
    private ArrayList<Long> crimeTime = new ArrayList<>();
    private LocationManager locationManager;
    private String provider;
    private Location location;

    Route routeManager;
//    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapsActivity.FINE_LOCATION_PERMISSION_REQUEST);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        markerArray = new ArrayList<>();
        routeManager = new Route(mMap, this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getLocation();


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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker auxMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                markerArray.add(auxMarker);
            }
        });


        final FloatingActionButton btnGetRoute = (FloatingActionButton) findViewById(R.id.btnGetRoute);
        btnGetRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(markerArray.size() > 0)routeManager.sendRequest(markerArray, crimeLocations, crimeTime, crimeWeight);
            }
        });

        drawDangerousPoints();
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker mAux;
                    mAux = mMap.addMarker(new MarkerOptions().position(me).title("Estou Aqui!"));
                    markerArray.add(mAux);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 17));
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };


            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        }
    }

    public void drawDangerousPoints() {
        final FloatingActionButton btnClearScreen =  findViewById(R.id.btnClearScreen);
        btnClearScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Marker first = markerArray.get(0);
                int i = 0;
                for(Marker marker:  markerArray){
                    if(i != 0 )marker.remove();
                    i++;
                }
                markerArray = new ArrayList<Marker>();
                markerArray.add(first);
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
                    LatLng location = new LatLng(in.latitude, in.longitude);
                    Log.d("vei", Long.toString(in.horario));
                    crimeLocations.add(location);
                    crimeWeight.add(in.nivel);
                    crimeTime.add(in.horario);
                }

                for(int i = 0; i < crimeLocations.size(); i++) {
                   Timestamp aux = new Timestamp(crimeTime.get(i));
                   Long days = Math.abs(Calendar.getInstance().getTime().getTime() - aux.getTime()) / (1000 * 60 * 60 * 24);

                   if (days <= 14) {
                        Circle circle = mMap.addCircle(new CircleOptions()
                                .center((LatLng) crimeLocations.get(i))
                                .radius(crimeWeight.get(i) * 10)
                                .strokeColor(Color.RED)
                                .strokeWidth(0)
                                .fillColor(0x55ff0000));
                        // mMap.addMarker(new MarkerOptions().position((LatLng) crimeLocations.get(i)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location){
        Toast.makeText(this, "Localização atual.\n", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Redirecionando para localização atual.", Toast.LENGTH_SHORT).show();
        if(markerArray.isEmpty()){
            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
            Marker mAux;
            mAux = mMap.addMarker(new MarkerOptions().position(me).title("Estou Aqui!"));
            markerArray.add(mAux);
        }
        return false;
    }
    
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
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