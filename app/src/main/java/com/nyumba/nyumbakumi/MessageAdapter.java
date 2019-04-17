package com.nyumba.nyumbakumi;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;

public class MessageAdapter extends FirebaseListAdapter<ChatMessage> {

    private ChatActivity activity;

    public MessageAdapter(ChatActivity activity, Class<ChatMessage> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
    }

    @Override
    protected void populateView(View v, final ChatMessage model, int position) {
        TextView messageText = v.findViewById(R.id.message_text);
        final TextView messageUser = v.findViewById(R.id.message_user);
        TextView messageTime = v.findViewById(R.id.message_time);
        ImageView photo=v.findViewById(R.id.photo);

        messageText.setText(model.getMessageText());

//sets photo to message
        try {
            if (model.getPhoto() != null) {

                Picasso.get().load(model.getPhoto()).into(photo);
            }
        }catch (Exception ignored){

        }
        DatabaseReference nref=FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("users").child(
                model.getMessageUserId());
        nref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageUser.setText(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view==null){
            LayoutInflater lInflater = (LayoutInflater)activity.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            view = lInflater.inflate(R.layout.activity_chat, null);
            return view;

        }

            ChatMessage chatMessage = getItem(position);


 if (chatMessage.getMessageUserId().equals(activity.getLoggedInUserName()))
                view = activity.getLayoutInflater().inflate(R.layout.item_chat_right, viewGroup, false);
            else
                view = activity.getLayoutInflater().inflate(R.layout.item_chat_left, viewGroup, false);

            //generating view
            populateView(view, chatMessage, position);



        return view;

    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }
}

