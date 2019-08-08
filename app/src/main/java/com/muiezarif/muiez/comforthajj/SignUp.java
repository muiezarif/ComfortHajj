package com.muiezarif.muiez.comforthajj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {
    Button signup,signin;
    EditText email,password,cpassword,username;
    CheckBox terms;
    DatabaseReference reference;
    FirebaseAuth auth;
    String sCpassword,sPassword,sUsername,sEmail;
    String deviceID=null;
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inIt();
        auth=FirebaseAuth.getInstance();
        deviceID= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        reference=FirebaseDatabase.getInstance().getReference().child("users");
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(SignUp.this,SignIn.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail=email.getText().toString().replace(" ","");
                sUsername=username.getText().toString();
                sPassword=password.getText().toString();
                sCpassword=cpassword.getText().toString();
                if(Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
                        if (sPassword.equals(sCpassword)){
                            if(terms.isChecked()){
                                if (sUsername.isEmpty()){
                                    username.setError("Username Empty");
                                }else if(sUsername.contains(" ")){
                                    username.setError("No spaces allowed in username");
                                }else{
                                    CreateNewUser();
                                }
                            }else{
                                Toasty.warning(SignUp.this, "Please Check the terms of service box first :)", Toast.LENGTH_SHORT).show();
                            }
                        }else if (sPassword.contains(" ")||sCpassword.contains(" ")){
                            cpassword.setError("Password Should Not Contain Space");
                        }else{
                            cpassword.setError("Password does not match");
                        }
                    } else{
                    email.setError("Email Invalid");
                }

            }
        });
    }

    public void inIt(){
        signin=findViewById(R.id.activity_login);
        signup=findViewById(R.id.btn_sign_up);
        email=findViewById(R.id.register_email);
        password=findViewById(R.id.register_password);
        cpassword=findViewById(R.id.register_confirm_password);
        username=findViewById(R.id.register_username);
        terms=findViewById(R.id.terms_of_service);

    }
    private void CreateNewUser(){
        auth.createUserWithEmailAndPassword(sEmail,sPassword).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailVerification();
                }else {
//                    Toasty.error(SignUp.this, "Error! Check email or password(Password should not be less than 6 characters). :)", Toast.LENGTH_SHORT).show();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toasty.error(SignUp.this, "Invalid email ", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toasty.error(SignUp.this, "Invalid password ", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        Toasty.error(SignUp.this, "No network ", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toasty.error(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void sendEmailVerification(){
        FirebaseUser user=auth.getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserData();
                        Toasty.success(SignUp.this, "Successfully Registered,Verification email has been sent to the provided email address.", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        finish();
                        startActivity(new Intent(SignUp.this, SignIn.class));
                    }else {
                        Toasty.error(SignUp.this, "Error! please try again maybe there is some error in the server. :)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void sendUserData(){
        String device_token= FirebaseInstanceId.getInstance().getToken();
        HashMap<String,String> userdata=new HashMap<String, String>();
        userdata.put("name",sUsername);
        userdata.put("email",sEmail);
        userdata.put("status","");
        userdata.put("image","default");
        userdata.put("thumb_image","default");
        userdata.put("profile_status","Hey There! Im using ComfortHajj");
        userdata.put("device_token",device_token);
        reference.child(auth.getUid()).setValue(userdata);
    }

}
