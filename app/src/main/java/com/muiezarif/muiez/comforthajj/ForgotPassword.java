package com.muiezarif.muiez.comforthajj;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class ForgotPassword extends AppCompatActivity {
    Button resetPassword;
    EditText resetEmail;
    FirebaseAuth auth;
    String sEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        inIt();
        auth=FirebaseAuth.getInstance();
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail=resetEmail.getText().toString();
                if(sEmail.isEmpty()){
                    resetEmail.setError("Please enter your email.");
                }else {
                    auth.sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toasty.success(ForgotPassword.this, "Password Reset Email has been sent to your email address", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPassword.this,SignIn.class));
                            }else{
                                Toasty.error(ForgotPassword.this, "Email address not found in database.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void inIt(){
        resetEmail=findViewById(R.id.reset_email);
        resetPassword=findViewById(R.id.btn_reset_password);
    }
}
