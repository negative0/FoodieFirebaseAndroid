package com.fireblaze.evento.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fireblaze.evento.R;
import com.fireblaze.evento.SnackBarContainerInterface;
import com.fireblaze.evento.models.MyLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SnackBarContainerInterface {

    private GoogleMap mMap;
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private LatLng location;
    private String name;




    @Override
    public View getContainer() {
        return findViewById(R.id.map);
    }

    public static void navigate(AppCompatActivity activity, MyLocation myLocation, String name){
        Intent i = new Intent(activity,MapsActivity.class);
        i.putExtra("latitude", myLocation.latitude);
        i.putExtra("longitude", myLocation.longitude);
        i.putExtra("name",name);
        activity.startActivity(i);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Snackbar.make(getContainer(),"Need Your MyLocation",Snackbar.LENGTH_INDEFINITE).show();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle b = getIntent().getExtras();
        location = new LatLng(b.getDouble("latitude"),b.getDouble("longitude"));
        name = b.getString("name");
        //Permission to access the user's location

//     if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED){
//         ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_ACCESS_COARSE_LOCATION);
//        }
       // googleApiClient = new GoogleApiClient.Builder(this,this,this).addApi(LocationServices.API).build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        MapStyleOptions style =  MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style);
        googleMap.setMapStyle(style);
        mMap = googleMap;
        // Add a marker to provided location

        Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(name).snippet("Pune"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        marker.showInfoWindow();

    }
}
