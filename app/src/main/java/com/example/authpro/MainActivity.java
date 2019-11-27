package com.example.authpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    EditText name,email,password;
    Button signup,login,sendOtp;
    Toolbar tb;
    TextView logout,deleteUser,textView;
    ProgressDialog progressDialog;
    private EditText editTextMobile;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Getting Data Into  Java Objects For Further Use
        name = (EditText)findViewById(R.id.et_signup_name);
        email = (EditText)findViewById(R.id.et_signup_email);
        password = (EditText)findViewById(R.id.et_signup_password);
        textView = (TextView) findViewById(R.id.textView);
        signup = (Button)findViewById(R.id.bt_signup_signup);
        login = (Button)findViewById(R.id.bt_signup_login);
        logout = findViewById(R.id.logout);
        deleteUser = findViewById(R.id.delete_user);
        editTextMobile = findViewById(R.id.editTextMobile);
        sendOtp = (Button)findViewById(R.id.buttonContinue);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(),"Succesfully LogOut User",Toast.LENGTH_SHORT).show();
                    visibleLogin(false);
                }

            }
        });
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.getCurrentUser().delete();
                    Toast.makeText(getApplicationContext(),"Succesfully Delete User",Toast.LENGTH_SHORT).show();
                    visibleLogin(false);
                }
            }
        });

        visibleLogin(mAuth.getCurrentUser() != null);


       sendOtp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String mobile = editTextMobile.getText().toString().trim();

        if(mobile.isEmpty() || mobile.length() < 10){
            editTextMobile.setError("Enter a valid mobile");
            editTextMobile.requestFocus();
            return;
        }

        Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
    }
});


        // When SignUp Button Clicked
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Setting Progress Bar


                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    email.setError("enter email");
                }
                else if(TextUtils.isEmpty(name.getText().toString()))
                {
                    name.setError("Enter name");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.setError("enter password");
                }
                else {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Registering User.....");
                    progressDialog.setMessage("Please Wait While We Creating Your Account");
                    progressDialog.show();
                    Registration(name, email, password);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,login_page.class);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.textView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Crashlytics.getInstance().crash(); for crashlytics crash manuly
               Boolean verifyCrash = mAuth.getCurrentUser().isEmailVerified();
            }
        });

    }

    public void visibleLogin(Boolean isVisible) {
       // final Boolean isVisible = mAuth.getCurrentUser() != null;
        logout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        deleteUser.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        textView.setVisibility(isVisible ? View.INVISIBLE: View.VISIBLE);
    }

    public void Registration(final EditText name, final EditText email, final EditText password) {
        final   String Name = name.getText().toString();
        final   String Email = email.getText().toString();
        final   String Password = password.getText().toString();
        final   String Uid;
        final   String image;

        //Showing Progressing Dialog To Look And Feel Good To User :-)
        // We Have To Check validation Here But We Will Do It Further

        // Getting FireBase Authorization Access

        //sending data to firebase authentication data storage
        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Email_verification email_verification  = new Email_verification();
                                                email_verification.sendDataToFirebase(user.getUid(),Name,Email,Password,false, user);

                                                Intent i = new Intent(MainActivity.this,Email_verification.class);
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checking User Is Logging In Or Not;
        final FirebaseUser  user= mAuth.getCurrentUser();
        if(user==null )
        {
           return;
        }
        else if(user.isEmailVerified())
        {
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            databaseReference.child("isemailverified").setValue(true);
            databaseReference.child("online").setValue("true");
        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser  user= mAuth.getCurrentUser();
        if(user!=null) {
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            databaseReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser  user= mAuth.getCurrentUser();
        if(user!=null)
        {
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            databaseReference.child("online").setValue("true");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser  user= mAuth.getCurrentUser();
        if(user!=null)
        {
            //First Get Connection With FireBase
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            databaseReference.child("online").setValue("false");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser  user= mAuth.getCurrentUser();
        if(user!=null)
        {
            //First Get Connection With FireBase
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

}
