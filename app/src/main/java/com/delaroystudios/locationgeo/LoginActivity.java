package com.delaroystudios.locationgeo;


import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.delaroystudios.locationgeo.provider.PlaceDetails;
import com.delaroystudios.locationgeo.provider.SqlDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button btnlogin;
    private TextView txtregister,txtcondition;
    private CheckBox checkBox;
    private SqlDatabase sqlDatabase;

    LocationManager locationManager;
    private static int req_location=100;


    private FirebaseAnalytics mFirebaseAnalytics;


    private final static int EXIT_CODE = 100;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sqlDatabase = new SqlDatabase(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        email=findViewById( R.id.editemail );
        password=findViewById( R.id.editpassword );
        btnlogin=findViewById( R.id.buttonlogin );
        txtregister=findViewById( R.id.textlogin );
        checkBox=findViewById(R.id.checklogin);
        txtcondition=findViewById(R.id.textViewcondition);


        mAuth = FirebaseAuth.getInstance();
        progressBar=findViewById( R.id.progressbar );



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        txtregister.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );

                startActivity( intent );
            }
        } );
        btnlogin.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {


                String Email= email.getText().toString().trim();
                String Pass= password.getText().toString().trim();

                if(TextUtils.isEmpty(Email)){
                    email.setError( "Email is Required" );
                    email.requestFocus() ;
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(Email ).matches()){
                    email.setError( "please enter the vaild email" );
                    email.requestFocus(  );
                    return;
                }
                if(TextUtils.isEmpty(Pass)){
                    password.setError( "password is Required" );
                    password.requestFocus() ;
                    return;
                }

                if(Pass.length()<6){
                    password.setError( "minimum length of password should be 6" );
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility( View.VISIBLE );

                mAuth.signInWithEmailAndPassword( Email,Pass ).addOnCompleteListener( new OnCompleteListener<AuthResult>( ) {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        progressBar.setVisibility( View.GONE );

                        if (task.isSuccessful()) {

                            Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                            startActivity( intent );
                            finish();
                        }else{


                            Toast.makeText( LoginActivity.this, "Error !" + task.getException( ).getMessage( ), Toast.LENGTH_SHORT ).show( );

                        }
                    }
                } );

            }
        } );
    }

    protected boolean checkLocationExists() {
        List<PlaceDetails> allPlace= sqlDatabase.allPlace();
        if (allPlace.size() > 0){
            return true;
        }else{
            return false;
        }
    }

}
