package com.muiezarif.muiez.comforthajj;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatAdapter extends ArrayAdapter<String> {
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> key=new ArrayList<>();
    private Context context;
    private int resource;
    private LayoutInflater layoutInflater;
    DatabaseReference onCallReference;

    public ChatAdapter(@NonNull Context context, int resource, ArrayList<String> name, ArrayList<String> status, ArrayList<String> imgs, ArrayList<String> key) {
        super(context, resource);
        this.imgs=imgs;
        this.name=name;
        this.status=status;
        this.key=key;
        this.resource=resource;
        this.context=context;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=layoutInflater.inflate(resource,parent,false);
        CircleImageView view=v.findViewById(R.id.user_single_image);
        if(imgs!=null) {
            Picasso.get().load(imgs.get(position)).placeholder(R.drawable.user).into(view);
        }else{
            Picasso.get().load(R.drawable.user).into(view);
        }
        TextView tv1=v.findViewById(R.id.user_single_name);
        TextView tv2=v.findViewById(R.id.user_single_status);
        tv1.setText(name.get(position));
        tv2.setText(status.get(position));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getContext(),ChatActivity.class);
                intent.putExtra("key",key.get(position));
                intent.putExtra("name",name.get(position));
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
