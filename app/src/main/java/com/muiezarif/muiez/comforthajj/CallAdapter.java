package com.muiezarif.muiez.comforthajj;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.muiezarif.muiez.comforthajj.usersignedin.CallFriendsListFragment;
import com.muiezarif.muiez.comforthajj.usersignedin.UserSignedIn;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallAdapter extends ArrayAdapter<String> {
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> imgs=new ArrayList<>();
    ArrayList<String> key=new ArrayList<>();
    private Context context;
    private int resource;
    private LayoutInflater layoutInflater;

    public CallAdapter(@NonNull Context context, int resource, ArrayList<String> name, ArrayList<String> status, ArrayList<String> imgs,ArrayList<String> key) {
        super(context, resource);
        this.imgs=imgs;
        this.name=name;
        this.status=status;
        this.key=key;
        this.context=context;
        this.resource=resource;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v= layoutInflater.inflate(resource,parent,false);
        CircleImageView circleImageView=v.findViewById(R.id.call_friend_image);
        if(imgs!=null) {
            Picasso.get().load(imgs.get(position)).placeholder(R.drawable.user).into(circleImageView);
        }else{
            Picasso.get().load(R.drawable.user).into(circleImageView);
        }
        TextView tv1=v.findViewById(R.id.call_friend_name);
        ImageView iv1=v.findViewById(R.id.call_click_btn_image);
        tv1.setText(name.get(position));
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getContext(), CallActivity.class);
                intent.putExtra("key",key.get(position));
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
