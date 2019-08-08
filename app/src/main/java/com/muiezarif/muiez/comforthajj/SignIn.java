package com.muiezarif.muiez.comforthajj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.muiezarif.muiez.comforthajj.usersignedin.UserSignedIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import es.dmoral.toasty.Toasty;

public class SignIn extends AppCompatActivity  {
    Button signin,register,forgotPassword;
    EditText email,password;
    FirebaseAuth auth;
    DatabaseReference reference;
    String sEmail,sPassword;
    ProgressDialog dialog;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        inIt();
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        dialog=new ProgressDialog(this);
        if (user!=null){
            finish();
            startActivity(new Intent(SignIn.this,UserSignedIn.class));
        }else{

        }
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail=email.getText().toString().replace(" ","");
                sPassword=password.getText().toString();
                if (sEmail.isEmpty() || sPassword.isEmpty()){
                        Toasty.warning(SignIn.this, "Please fill the required fields.", Toast.LENGTH_SHORT).show();

                }else{
                    dialog.setMessage("Verifying...");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    validate(sEmail,sPassword);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this,ForgotPassword.class));
            }
        });
    }

    public void inIt() {
        signin=findViewById(R.id.btn_signin);
        register=findViewById(R.id.register_activity);
        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        forgotPassword=findViewById(R.id.btn_forgot_password);
    }
    public void validate(final String email, String pass){
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    checkEmailVerification();
                }else{
//                    Toasty.error(SignIn.this, "Login Failed! ", Toast.LENGTH_SHORT).show();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toasty.error(SignIn.this, "Invalid email ", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toasty.error(SignIn.this, "Invalid password ", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        Toasty.error(SignIn.this, "No network ", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toasty.error(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }
        });
    }
    private void checkEmailVerification(){
        FirebaseUser user = auth.getCurrentUser();
        Boolean emailFlag=user.isEmailVerified();
        if (emailFlag){
            String device_token= FirebaseInstanceId.getInstance().getToken();
            reference.child(user.getUid()).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                    startActivity(new Intent(SignIn.this,UserSignedIn.class));
                }
            });
        }else{
            Toasty.warning(this, "Please Verify Your Email First :)", Toast.LENGTH_SHORT).show();
            auth.signOut();
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(user!=null) {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
