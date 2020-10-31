package com.example.mapstrack;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    LatLng latLng, latLng_new;
    GoogleMap map;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView tvMain;
    EditText lat_find, lon_find;
    Button findBtn;
    String p1;
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tvMain = findViewById(R.id.tv_main);
        tvMain.setMovementMethod(new ScrollingMovementMethod());
        lat_find = findViewById(R.id.lat_find);
        lon_find = findViewById(R.id.lon_find);
        findBtn = findViewById(R.id.findBtn);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        else {
            getCurrentLoacation();
        }

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat_find.getText().toString().isEmpty()||lat_find.getText().toString().isEmpty()){
                    Toast.makeText(MapsActivity.this, "provide Latitude and Longitude first", Toast.LENGTH_SHORT).show();
                }
                else {
                    float newLat = Float.parseFloat(lat_find.getText().toString());
                    float newLon = Float.parseFloat(lon_find.getText().toString());

//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(latLng_new);
//                    markerOptions.title(latLng_new.latitude+" : "+ latLng_new.longitude);


                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(newLat,
                                newLon, 1);
                        latLng_new = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng_new).title(addresses.get(0).getAddressLine(0)));
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng_new));
                        map.animateCamera(CameraUpdateFactory.zoomIn());
                        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        Toast.makeText(MapsActivity.this, ""+latLng_new, Toast.LENGTH_SHORT).show();

                        p1 = p1+"\n\nLocation "+count+":\n----------------------------" +
                                "\nCountry: "+addresses.get(0).getCountryName()+
                                "\nState: "+addresses.get(0).getAdminArea()+
                                "\nCity: "+addresses.get(0).getLocality()+
                                "\nPincode: "+addresses.get(0).getPostalCode()+
                                "\nAddress: "+addresses.get(0).getAddressLine(0)+
                                "\nLatitude: "+addresses.get(0).getLatitude()+
                                "\nLongitude: "+ addresses.get(0).getLongitude();
                        count = count+1;
                        tvMain.setText(p1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLoacation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        mapFragment.getMapAsync(MapsActivity.this);
                        p1 = "Current Location:\n----------------------------" +
                                "\nCountry: "+addresses.get(0).getCountryName()+
                                "\nState: "+addresses.get(0).getAdminArea()+
                                "\nCity: "+addresses.get(0).getLocality()+
                                "\nPincode: "+addresses.get(0).getPostalCode()+
                                "\nAddress: "+addresses.get(0).getAddressLine(0)+
                                "\nLatitude: "+addresses.get(0).getLatitude()+
                                "\nLongitude: "+ addresses.get(0).getLongitude();
                        tvMain.setText(p1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Add a marker in Sydney and move the camera
        if(latLng!=null){
            map.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

    }


}