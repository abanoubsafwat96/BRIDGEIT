package com.safwat.abanoub.bridgeit;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusActivity extends FragmentActivity implements OnMapReadyCallback {

    LinearLayout busAdminLinear, studentLinear;
    LatLng latLng;
    //Maps
    GoogleMap mMap;
    private Marker marker;
    //Location
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng currentLocation;

    boolean firstOpen = false;
    private String userType;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String busID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        busAdminLinear = findViewById(R.id.busAdminLinear);
        studentLinear = findViewById(R.id.studentLinear);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Define Needed Permissions for android Marshmallow and higher
            // The request code used in ActivityCompat.requestPermissions()
            // and returned in the Activity's onRequestPermissionsResult()
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BusActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        firebaseDatabase=FirebaseDatabase.getInstance();

        if (userType.equals("Bus Admin")) {
            busAdminLinear.setVisibility(View.VISIBLE);
            studentLinear.setVisibility(View.GONE);

        } else {
            busAdminLinear.setVisibility(View.GONE);
            studentLinear.setVisibility(View.VISIBLE);

//            if (userType.equals("Student"))
//                databaseReference = firebaseDatabase.getReference().child("students").child(Utilities.getCurrentUID());
//            else if (userType.equals("Teacher"))
//                databaseReference = firebaseDatabase.getReference().child("teachers").child(Utilities.getCurrentUID());
//            else if (userType.equals("Parent"))
//                databaseReference = firebaseDatabase.getReference().child("parents").child(Utilities.getCurrentUID());
//            else if (userType.equals("Doctor"))
//                databaseReference = firebaseDatabase.getReference().child("doctors").child(Utilities.getCurrentUID());
//            else if (userType.equals("PE"))
//                databaseReference = firebaseDatabase.getReference().child("PEs").child(Utilities.getCurrentUID());
//            else if (userType.equals("Manager"))
//                databaseReference = firebaseDatabase.getReference().child("managers").child(Utilities.getCurrentUID());
//
//            databaseReference.orderByKey().equalTo("busID").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    //iterate through each Msg, ignoring their UID
//                    if (dataSnapshot.getValue() != null) {
//                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                            busID=child.getValue(Teacher.class).busID;
//                        }
//                    }
//                    getBusLocation();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getCurrentLocation();

    }

    private void getBusLocation() {
        if (busID!=null){
            databaseReference=firebaseDatabase.getReference().child("bus").child(busID).child("location");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    latLng=Utilities.getBusLocation(dataSnapshot);
                    showBusLocation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showBusLocation() {
        if (latLng!=null){
            flyWithCameraTo(latLng);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //checking location settings on device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {

            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                if (ActivityCompat.checkSelfPermission(BusActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BusActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // CalendarEvent: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(BusActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Logic to handle location object
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            flyWithCameraTo(currentLocation);
                        }
                    }
                });
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        Toast.makeText(BusActivity.this, "task.onfailure()", Toast.LENGTH_SHORT).show();

                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(BusActivity.this, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void flyWithCameraTo(LatLng target) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(target, 8, 30, 0))
                , 3000, null);
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
        mMap = googleMap;

        showMyLocation();

        if (userType.equals("Bus Admin")) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(30, 30)).title("Student"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(30, 31)).title("Student"));
            flyWithCameraTo(new LatLng(30, 31));

        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(30,31)).title("Bus"));
            flyWithCameraTo(new LatLng(30, 31));
        }
    }

    private void showMyLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // CalendarEvent: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true); //set mark at your location on the map    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstOpen == false)
            firstOpen = true;
        else if (mMap == null || mMap.isMyLocationEnabled() == false) {
            showMyLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
