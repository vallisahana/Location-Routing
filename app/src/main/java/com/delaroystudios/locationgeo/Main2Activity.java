package com.delaroystudios.locationgeo;


import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Main2Activity extends AppCompatActivity {

    EditText editname,editlatitude,editlongitude,editcondition;

    Button btnaddlocation;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        editname=findViewById(R.id.editname);
        editlatitude=findViewById(R.id.editlatitude);
        editlongitude=findViewById(R.id.editlongitude);
        editcondition=findViewById(R.id.editcondition);
        btnaddlocation=findViewById(R.id.buttonadd);

        databaseReference = FirebaseDatabase.getInstance().getReference("loaction");

        btnaddlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveLocation();

                editname.getText().clear();
                editlatitude.getText().clear();
                editlongitude.getText().clear();
                editcondition.getText().clear();

            }
        });

    }

    private void saveLocation()
    {
        String name=editname.getText().toString().trim();
        Double latitude=Double.parseDouble(editlatitude.getText().toString());
        Double longitude=Double.parseDouble(editlongitude.getText().toString());
        String condition=editcondition.getText().toString().trim();


        if(!TextUtils.isEmpty(name)) {


            String id = databaseReference.push().getKey();
            Routing hospital = new Routing(name,latitude,longitude,condition);

            databaseReference.child(id).setValue(hospital);

            Toast.makeText(this, "Location Added", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
        }




}
}
