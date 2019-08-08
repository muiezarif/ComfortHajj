package com.muiezarif.muiez.comforthajj.usersignedin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.muiezarif.muiez.comforthajj.HajjGuide;
import com.muiezarif.muiez.comforthajj.R;
import com.firebase.client.ServerValue;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHajjGuide extends Fragment {
    AdView adView;
    InterstitialAd interstitialAd;
    Button englishHajjGuide,urduHajjGuide;
    String ulang="urdu";
    String elang="english";
    FirebaseUser user;
    DatabaseReference presenceReference;
    public FragmentHajjGuide() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_hajj_guide, container, false);
        adView=v.findViewById(R.id.adViewhajjguide);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);
        interstitialAd=new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-7701765309854052/7867968342");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        try {
            interstitialAd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        englishHajjGuide=v.findViewById(R.id.englishGuide);
        urduHajjGuide=v.findViewById(R.id.urduGuide);
        user= FirebaseAuth.getInstance().getCurrentUser();
        presenceReference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        englishHajjGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), HajjGuide.class);
                intent.putExtra("Guide",elang);
                startActivity(intent);
            }
        });
        urduHajjGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),HajjGuide.class);
                intent.putExtra("Guide",ulang);
                startActivity(intent);
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

}
