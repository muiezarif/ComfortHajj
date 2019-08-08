package com.muiezarif.muiez.comforthajj.usersignedin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.muiezarif.muiez.comforthajj.R;
import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTasbeehCounter extends Fragment implements View.OnKeyListener {
    private int mCounter=0;
    Button btn_count,btn_clear;
    TextView counter;
    FirebaseUser user;
    DatabaseReference presenceReference;


    public FragmentTasbeehCounter() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_tasbeeh_counter, container, false);
        btn_count=v.findViewById(R.id.btn_counter);
        btn_clear=v.findViewById(R.id.btn_clearcounter);
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        counter=v.findViewById(R.id.tasbeehcounter);
        counter.setOnKeyListener(this);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCounter=0;
                counter.setText(Integer.toString(mCounter));
            }
        });
        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCounter++;
                counter.setText(Integer.toString(mCounter));
            }
        });

        return v;
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        if(user!=null) {
//            presenceReference.child("online").setValue("true");
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
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

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(user!=null) {
//            presenceReference.child("online").setValue(ServerValue.TIMESTAMP);
//        }
//    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            mCounter++;
            counter.setText(Integer.toString(mCounter));
            return true;
        }else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            mCounter++;
            counter.setText(Integer.toString(mCounter));
            return true;
        }
        return true;
    }

}
