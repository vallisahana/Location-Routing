package com.delaroystudios.locationgeo;


import android.Manifest;
import android.R.layout;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import com.delaroystudios.locationgeo.Interface.IFirebaseLoaddone;
import com.delaroystudios.locationgeo.provider.PlaceDbHelper;
import com.delaroystudios.locationgeo.provider.PlaceDetails;
import com.delaroystudios.locationgeo.provider.SqlDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.layout.simple_list_item_1;

public class NavigateActivity extends AppCompatActivity implements OnMapReadyCallback,IFirebaseLoaddone {

    GoogleMap mMap;
    DatabaseReference locationRef;
    IFirebaseLoaddone iFirebaseLoaddone;
    List<Routing> routings;
    SearchableSpinner searchableSpinner;
    Button btndirection,btndelete;
    String selected;

    private TextView textViewdistance;

    private ProgressDialog LoadingBar;
    List<String> namelist = new ArrayList<>();

    PlaceDbHelper placeDbHelper=new PlaceDbHelper(this);
    PlaceListAdapter adapter;
    SQLiteDatabase sqLiteDatabase;

    SqlDatabase sqlDatabase=new SqlDatabase(this);
    List<PlaceDetails> places = new ArrayList<PlaceDetails>();



    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private static DecimalFormat df2 = new DecimalFormat("#.#");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        LoadingBar = new ProgressDialog(this);



        searchableSpinner = findViewById(R.id.searchviewid);
        btndirection=findViewById(R.id.buttongo);
        textViewdistance=findViewById(R.id.textviewdistance);
        btndelete=findViewById(R.id.buttondelete);





        //init Db
        locationRef = FirebaseDatabase.getInstance().getReference("loaction");
        //init interface
        iFirebaseLoaddone = this;

        //child event
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Routing> routing = new ArrayList<>();
                for (DataSnapshot routingsnapshot : dataSnapshot.getChildren()) {
                    routing.add(routingsnapshot.getValue(Routing.class));
                   // locationsnapshot.getRef().child("Critical Condition").setValue(3);

                }

                //loading data from firebase
                iFirebaseLoaddone.onFirebaseLoadSuccess(routing);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                iFirebaseLoaddone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });

        loadspinnerdata();

        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                selected = parent.getItemAtPosition(position).toString();
                Log.e("clicked", " " + selected);




                locationRef.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        setMarker(dataSnapshot);
                        calculatedistance(dataSnapshot);
                       // RandomForest(dataSnapshot);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        setMarker(dataSnapshot);
                       calculatedistance(dataSnapshot);
                        //RandomForest(dataSnapshot);

                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void RandomForest(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


        btndirection.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

       Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + selected);
       Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
       // Intent intent=new Intent(NavigateActivity.this,Main2Activity.class);
        //startActivity(intent);
    }
});

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           deletelocation();

                Toast.makeText(NavigateActivity.this, "you have reached the place", Toast.LENGTH_SHORT).show();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.zoomin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        findViewById(R.id.zoomout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    @Override
    public void onFirebaseLoadSuccess(List<Routing> routingList) {

       routings = routingList;

        //get all data
        List<String> id = new ArrayList<>();
      //  List<String> name_list = new ArrayList<>();
        List<Double> latitude = new ArrayList<>();
        List<Double> longitude = new ArrayList<>();
        for (Routing location : routingList)
            //  id.add(hospital.getId());
            namelist.add(location.getName());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, simple_list_item_1, namelist);
        searchableSpinner.setAdapter(adapter);

    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }

    public void loadspinnerdata(){

        sqlDatabase=new SqlDatabase(this);
        List<PlaceDetails> places = sqlDatabase.allPlace();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, simple_list_item_1, places);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchableSpinner.setAdapter(adapter);

    }
    @Override
    public void onBackPressed() {

            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage("Do you want to exit app?")
                    .setNegativeButton("NO", null)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavigateActivity.super.onBackPressed();
                        }
                    }).create().show();
            // super.onBackPressed();

    }

    public double calculatedistance(DataSnapshot dataSnapshot) {


        HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();

        double lati = Double.parseDouble(values.get("latitude").toString());
        double longi = Double.parseDouble(values.get("longitude").toString());

        double xSquare = Math.pow(12.7032012 - lati,2);
        double ySquare = Math.pow(76.3050752 - longi, 2);
        double distance = Math.sqrt(xSquare + ySquare);



        textViewdistance.setText(String.valueOf("" + distance + "" + "km"));
        textViewdistance.setText(df2.format(distance) + "km");


        LoadingBar.setMessage("Please Wait........" +
                "Finding Distance");
        LoadingBar.show();
        LoadingBar.setCancelable(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingBar.dismiss();
            }
        }, 4000);
        return distance;
    }

   public void deletelocation() {


       locationRef = FirebaseDatabase.getInstance().getReference("loaction");

       //child event
       locationRef.addListenerForSingleValueEvent(new ValueEventListener() {

           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               //   List<Routing> routing = new ArrayList<>();
               for (DataSnapshot routingsnapshot : dataSnapshot.getChildren()) {
                   if (routingsnapshot.child("name").getValue().toString().equals(searchableSpinner.getSelectedItem().toString())) {

                       routingsnapshot.getRef().removeValue();
                   }
               }

           }


           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
   }

       public void setMarker (DataSnapshot dataSnapshot){

           String key = dataSnapshot.getKey();
           HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

           double latitude = Double.parseDouble(value.get("latitude").toString());
           double longitude = Double.parseDouble(value.get("longitude").toString());

           LatLng location = new LatLng(latitude, longitude);

           if (!mMarkers.containsKey(key)) {
               mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
           } else {
               mMarkers.get(key).setPosition(location);

           }
           LatLngBounds.Builder builder = new LatLngBounds.Builder();
           for (Marker marker : mMarkers.values()) {
               builder.include(marker.getPosition());
           }
           mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));


       }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMaxZoomPreference(15.5f);
        mMap.setMinZoomPreference(3.0f);

    }
}

