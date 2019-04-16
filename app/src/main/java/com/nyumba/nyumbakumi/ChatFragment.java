package com.nyumba.nyumbakumi;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listChats;
    View v;
    ArrayList<ChatRoom> arrayList;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("chatrooms");
    ChatRoom chatRoom;
    EditText txtSearch;
    int textlength;
    ArrayList array_sort=new<String> ArrayList();

    public ChatFragment() {
        // Required empty public constructor
    }

    ChatRoomAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_chat, container, false);
        listChats = v.findViewById(R.id.listChats);
        arrayList = new ArrayList<>();

//adapter for group list
        adapter = new ChatRoomAdapter(getContext(), arrayList);

        //get all group names
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();

                for (DataSnapshot chidSnap : dataSnapshot.getChildren()) {


                    chatRoom = new ChatRoom();
                    chatRoom.GroupId = chidSnap.getKey();
                    chatRoom.GroupName = (String) chidSnap.child("groupName").getValue();
                    arrayList.add(chatRoom);
                }

                try {
                    adapter = new ChatRoomAdapter(getContext(), arrayList);

                    listChats.setAdapter(adapter);

                    listChats.setOnItemClickListener(ChatFragment.this);
                } catch (Exception ex) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txtSearch= v.findViewById(R.id.txtSearch);
        //search functionality
        txtSearch.addTextChangedListener(new TextWatcher() {


            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textlength = txtSearch.getText().length();
                array_sort.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (textlength <= arrayList.get(i).getGroupName().length()) {
                        Log.d("ertyyy",arrayList.get(i).getGroupName().toLowerCase().trim());
                        if (arrayList.get(i).getGroupName().toLowerCase().trim().contains(
                                txtSearch.getText().toString().toLowerCase().trim())) {
                            array_sort.add(arrayList.get(i));
                        }
                    }
                }
                adapter = new ChatRoomAdapter(getActivity(), array_sort);
                listChats.setAdapter(adapter);

            }
        });


        return v;
    }

    //open up the selected chatroom
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String groupName = arrayList.get(position).getGroupName();
        String groupID = arrayList.get(position).getGroupId();

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("groupName", groupName);
        intent.putExtra("groupId", groupID);
        startActivity(intent);
    }

    //create new group
    public void createNewGroup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final EditText edittext = new EditText(getContext());
        alert.setMessage("Enter group name");
        alert.setTitle("Create New Group");

        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//get the time in milliseconds
                String groupName = edittext.getText().toString();
                Long time = System.currentTimeMillis() / 1000;

                mRef.child(time + "-" + groupName).child("groupName").setValue(groupName);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.

            }
        });

        alert.show();
    }
}
