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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class login_page extends AppCompatActivity {
    Button login,reset;
    EditText email,password;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        login = (Button)findViewById(R.id.bt_login_login);
        email = (EditText) findViewById(R.id.et_login_email);
        password = (EditText)findViewById(R.id.et_login_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    email.setError("enter email");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.setError("enter password");
                }
                else
                {
                    progressDialog = new ProgressDialog(login_page.this);
                    progressDialog.setTitle("Logging.....");
                    progressDialog.setMessage("Please Wait While Logging !");
                    progressDialog.show();

                    login(email, password);
                }
            }
        });



    }

    private void login(EditText email, EditText password) {

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        // Getting To Firebase Datebase Seeting Or Connectivity
        firebaseAuth = FirebaseAuth.getInstance();


        //fireBase Authentication Checking
        firebaseAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                progressDialog.dismiss();
                                Toast.makeText(login_page.this, "Login Successfuly", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(login_page.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);// for user not going to previous page
                                startActivity(i);
                                finish();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(login_page.this, "User not verified by email link", Toast.LENGTH_SHORT).show();

                            }
                        }
                        else
                        {
                            progressDialog.cancel();
                            Toast.makeText(login_page.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}