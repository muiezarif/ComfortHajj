package com.muiezarif.muiez.comforthajj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.muiezarif.muiez.comforthajj.usersignedin.FragmentProfile;
import com.muiezarif.muiez.comforthajj.usersignedin.UserSignedIn;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class ChangeStatusActivity extends AppCompatActivity {
    EditText status;
    Button change;
    DatabaseReference reference;
    DatabaseReference presenceReference;
    FirebaseUser user;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Change Status");
        inIt();

        user=FirebaseAuth.getInstance().getCurrentUser();
        presenceReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=new ProgressDialog(ChangeStatusActivity.this);
                dialog.setTitle("Saving status");
                dialog.setMessage("Please wait while we update your status");
                dialog.show();
                String sStaus=status.getText().toString();
                reference.child("profile_status").setValue(sStaus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            finish();

                        }else{
                            Toasty.error(ChangeStatusActivity.this, "There was error updating your changes. Please try again. :)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void inIt(){
        status=findViewById(R.id.status_change);
        change=findViewById(R.id.btn_status_update);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(user!=null) {
            presenceReference.child("online").setValue("true");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(user!=null) {
            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            this.finish();
        }
        return true;
    }
}
