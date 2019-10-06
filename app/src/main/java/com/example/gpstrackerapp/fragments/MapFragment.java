package com.example.gpstrackerapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gpstrackerapp.CreateUser;
import com.example.gpstrackerapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE =101;
    LocationManager locationManager;
    double lat;
    double lon;
    FirebaseAuth auth;
    FirebaseUser user;
    CreateUser createUser;
    ArrayList<CreateUser> nameList;
    DatabaseReference reference,userReference;
    String circleMemberId;
    GoogleMap googleMap;
    SearchView searchView;

    public MapFragment() {
        // Required empty public constructor

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        nameList = new ArrayList<>();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameList.clear();

                if(dataSnapshot.exists()){
                    for(DataSnapshot dss: dataSnapshot.getChildren()){
                        circleMemberId = dss.child("circleMemberId").getValue(String.class);

                        userReference.child(circleMemberId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        createUser = dataSnapshot.getValue(CreateUser.class);
                                        nameList.add(createUser);
                                        //addMembers();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getActivity().getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        searchView = (SearchView)rootView.findViewById(R.id.sv_location);
        getLocations();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                Double avgLat = lat;
                Double avgLon = lon;
                int numMarker = 1;

                for(int i=0; i<nameList.size();i++){
                    CreateUser cu = nameList.get(i);
                    if (location != null || !location.equals("")) {


                        if (location.equals(cu.getUsername())) {

                            if (cu.isSharing.equals("true")) {
                                double latitude = Double.parseDouble(cu.getLat());
                                double longitude = Double.parseDouble(cu.getLng());
                                avgLat = avgLat + (latitude + 0.012);
                                avgLon = avgLon + (longitude + 0.012);
                                LatLng latitudeLng = new LatLng(latitude + 0.012, longitude + 0.012);
                                MarkerOptions memberMarker = new MarkerOptions().position(latitudeLng).title(cu.getName());
                                memberMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                numMarker++;
                                googleMap.addMarker(memberMarker);
                                centerZoom(avgLat, avgLon, numMarker);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "User is offline", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "User is not a circle member", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return rootView;
    }

    public void getLocations(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager=(LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            OnGPS();
        }
        else {
            fetchLastLocation();

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    googleMap.clear(); //clear old markers

                    LatLng latLng = new LatLng(lat, lon);

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I AM HERE");

                    googleMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).bearing(0).tilt(0).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    googleMap.addMarker(markerOptions);

                }
            });

        }
    }

    protected void addMembers(){
        System.out.println("Size of namelist : "+nameList.size());
        Double avgLat = lat;
        Double avgLon = lon;
        int numMarker = 1;

        for(int i=0; i<nameList.size();i++){
            CreateUser cu = nameList.get(i);
            if(cu.isSharing.equals("true")){
                double latitude = Double.parseDouble(cu.getLat());
                double longitude = Double.parseDouble(cu.getLng());
                avgLat = avgLat+(latitude+0.012);
                avgLon = avgLon+(longitude+0.012);
                LatLng latitudeLng = new LatLng(latitude+0.012, longitude+0.012);
                MarkerOptions memberMarker = new MarkerOptions().position(latitudeLng).title(cu.getName());
                memberMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                numMarker++;
                googleMap.addMarker(memberMarker);
            }
        }

        centerZoom(avgLat,avgLon,numMarker);

    }

    public void centerZoom(Double aLat, Double aLon, int numMarkers){

        Double zoomLat = aLat/numMarkers;
        Double zoomLon = aLon/numMarkers;

        LatLng zoomlatLng = new LatLng(zoomLat,zoomLon);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(zoomlatLng).zoom(14).bearing(0).tilt(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    private void fetchLastLocation() {

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        else{

            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(locationGPS!=null){
                lat = locationGPS.getLatitude();
                lon = locationGPS.getLongitude();

                updateLatLng(lat,lon);

            }

        }
    }


    public void updateLatLng(double latitude, double longitude) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        databaseReference.child("lat").setValue(Double.toString(latitude));
        databaseReference.child("lng").setValue(Double.toString(longitude));

    }

}
