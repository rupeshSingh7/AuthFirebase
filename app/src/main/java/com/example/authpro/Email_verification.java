package com.example.authpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Email_verification extends AppCompatActivity {

    Button verifyemail;
    String Name ;
    String Email;
    String Password;
   static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        FirebaseAuth.getInstance().signOut();
        verifyemail = (Button)findViewById(R.id.verifyemail);

        //setting Toolbar
//        toolbar = findViewById(R.id.email_verify_toolbar);
//        getSupportActionBar();
//        getSupportActionBar().setTitle("Email Verification");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseUser.isEmailVerified())
                {
                    Intent i = new Intent(Email_verification.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // fOR User Not Going To Previous Page
                    startActivity(i);
                    finish();
                }




            }
        };

        verifyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    startActivity(new Intent(getBaseContext(), login_page.class));
                    finish();
                }


        });

    }
public void sendmobileNumberToFirebase(String uid, String number){
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(uid);
    databaseReference.child("Number").setValue(number);
    databaseReference.child("online").setValue("false");
}
    public void  sendDataToFirebase(String uid, String name, String email, String password, Boolean isEmailVerified, FirebaseUser user) {
        //firebaseUser = user;
        //First Get Connection With FireBase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(uid);

        //Writing User Profile Data To Firebase
        databaseReference.child("name").setValue(name);
        databaseReference.child("email").setValue(email);
        databaseReference.child("password").setValue(password);
        databaseReference.child("isemailverified").setValue(isEmailVerified);
        databaseReference.child("online").setValue("false");

    }
}

