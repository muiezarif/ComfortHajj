package com.muiezarif.muiez.comforthajj;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> messagesList;
    private FirebaseUser user;
    DatabaseReference reference;
    public static final int MSG_TYPE_RIGHT=1;
    public static final int MSG_TYPE_LEFT=0;


    public MessageAdapter(List<Messages> messagesList){
        this.messagesList=messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
            return new MessageViewHolder(v);


    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView receiveMessageText;
        public TextView sendMessageText;


        public MessageViewHolder(View itemView) {
            super(itemView);
            receiveMessageText=itemView.findViewById(R.id.receiver_message_text);
            sendMessageText=itemView.findViewById(R.id.sender_message_textt);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        Messages c=messagesList.get(position);
        String fromUser=c.getFrom();


        reference= FirebaseDatabase.getInstance().getReference().child("users").child(fromUser);

        if (fromUser.equals(user.getUid())){

            holder.receiveMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
            holder.receiveMessageText.setTextColor(Color.BLACK);
            holder.receiveMessageText.setText(c.getMessage());
            holder.sendMessageText.setBackground(null);
            holder.sendMessageText.setText(null);


//            holder.receiveMessageText.setText(c.getMessage());
        }else if(!fromUser.equals(user.getUid())){
            holder.sendMessageText.setBackgroundResource(R.drawable.sender_message_layout);
            holder.sendMessageText.setTextColor(Color.BLACK);
            holder.sendMessageText.setText(c.getMessage());
            holder.receiveMessageText.setBackground(null);
            holder.receiveMessageText.setText(null);


//            holder.sendMessageText.setText(c.getMessage());
        }
//          holder.receiveMessageText.setText(c.getMessage());
//        holder.sendMessageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


}
