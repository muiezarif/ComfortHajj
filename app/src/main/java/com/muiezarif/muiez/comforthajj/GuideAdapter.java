package com.muiezarif.muiez.comforthajj;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GuideAdapter extends ArrayAdapter<String> {
    ArrayList<String> items;
    private Context context;
    private int resource;
    private LayoutInflater layoutInflater;
    public GuideAdapter(@NonNull Context context, int resource, ArrayList<String> items) {
        super(context, resource);
        this.items=items;
        this.resource=resource;
        this.context=context;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=layoutInflater.inflate(resource,parent,false);
        TextView tv=v.findViewById(R.id.guideItem);
        tv.setText(items.get(position));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),GuideTopic.class);
                intent.putExtra("topic",items.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
