package com.nyumba.nyumbakumi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRoomAdapter extends ArrayAdapter<ChatRoom> {

    public ChatRoomAdapter(Context context, ArrayList<ChatRoom> chatRooms) {

        super(context, 0, chatRooms);

    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        ChatRoom chatRooms = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_room, parent, false);

        }

        // Lookup view for data population

        TextView tvName = convertView.findViewById(R.id.tvName);

        TextView tvID = convertView.findViewById(R.id.tvId);

        // Populate the data into the template view using the data object

        tvName.setText(chatRooms.getGroupName());

        tvID.setText(chatRooms.getGroupId());

        // Return the completed view to render on screen

        return convertView;

    }

}