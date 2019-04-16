package com.nyumba.nyumbakumi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeFragment extends Fragment {


    View view;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    TextView loading, txtTime;
    DatabaseReference mDatabaseReference;
 

    //binds data to the data modeller and the views
    FirebaseRecyclerAdapter<ChatMessage, ItemViewHolder> firebaseRecyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("posts");
        txtTime = view.findViewById(R.id.txtTime);

        loading = view.findViewById(R.id.txtLoading);

        //layout manager for the recyclerview
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);




        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        //initialize adapter with database refernce, viewholder, data model and item row
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatMessage, ItemViewHolder>(
                ChatMessage.class,
                R.layout.item_post,
                ItemViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, final ChatMessage model, int position) {
                loading.setVisibility(View.GONE);
                final String item_key = getRef(position).getKey();
                viewHolder.setTitle(model.getMessageText());
                viewHolder.setTime(model.getMessageTime());

                viewHolder.mView.setTag(item_key);
                final ImageView ivImage = viewHolder.mView.findViewById(R.id.imageView);
                ivImage.buildDrawingCache();
                ivImage.setTag(model.getPhoto());

                // share post button
                ImageView share = viewHolder.mView.findViewById(R.id.ic_share);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


//check if post has a photo
                        if (ivImage.getDrawable() == null) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.putExtra(Intent.EXTRA_TEXT, model.getMessageText());
                            share.setType("text/plain");
                            viewHolder.mView.getContext().startActivity(Intent.createChooser(share, "Share Post"));
                        }
                        //post has photo
                        else {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                            BitmapDrawable drawable = (BitmapDrawable) ivImage.getDrawable();
                            Bitmap icon = drawable.getBitmap();

                            //Bitmap icon =ivImage.getDrawingCache() ;
                            Intent share = new Intent(Intent.ACTION_SEND);

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//create a new folder if not exists
                            File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Nyumba Kumi");
                            // have the object build the directory structure, if needed.
                            dir.mkdirs();
                            //saving the file to the folder
                            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Nyumba Kumi/" + model.getMessageText() + ".jpg");

                            try {
                                f.createNewFile();
                                FileOutputStream fo = new FileOutputStream(f);
                                fo.write(bytes.toByteArray());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("NYUMBA_", e.getMessage(), e);
                            }
                            //share the created file
                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Nyumba Kumi/" + model.getMessageText() + ".jpg"));
                            share.putExtra(Intent.EXTRA_TEXT, model.getMessageText());
                            share.setType("*/*");
                            viewHolder.mView.getContext().startActivity(Intent.createChooser(share, "Share Post"));
                        }
                    }
                });

                final TextView txtCount = viewHolder.mView.findViewById(R.id.txtCount);
//count comments
                DatabaseReference cRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("posts").child(item_key).child("comments");
                cRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        txtCount.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//set username at the toolbar
                final TextView tvUserName = viewHolder.mView.findViewById(R.id.tvUserName);
                DatabaseReference uRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("users").child(model.getMessageUserId());
                uRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        tvUserName.setText(username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if (model.getPhoto() != null && !model.getPhoto().equals("")) {
                    viewHolder.setImage(model.getPhoto());
                }
                //set click listener for entire cardview
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(viewHolder.mView.getContext(), PostDetails.class);
                        intent.putExtra("key", viewHolder.mView.getTag().toString());
                        intent.putExtra("user", tvUserName.getText().toString());
                        intent.putExtra("post", model.getMessageText());
                        viewHolder.mView.getContext().startActivity(intent);
                    }
                });

            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public ItemViewHolder(View v) {
            super(v);
            mView = v;
            final TextView tvUserName = mView.findViewById(R.id.tvUserName);
            final TextView tvTitle = mView.findViewById(R.id.txtCaption);
            final ImageView ivImage = mView.findViewById(R.id.imageView);

            //click listnere for report button
            ImageView report = mView.findViewById(R.id.ic_report);
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatabaseReference rRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("reports").child(mView.getTag().toString());
                    rRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("reportCount")) {
                                long count = (long) dataSnapshot.child("reportCount").getValue();
                                long i = count + 1;
                                rRef.child("reportCount").setValue(i);
                                Toast.makeText(mView.getContext(), "Post reported successfully", Toast.LENGTH_LONG).show();

                            } else {
                                DatabaseReference rRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("reports");
                                rRef.child(mView.getTag().toString()).setValue(
                                        new Report(
                                                tvTitle.getText().toString(),
                                                tvUserName.getText().toString(),
                                                ivImage.getTag().toString(),
                                                1


                                        ));
                                Toast.makeText(mView.getContext(), "Post reported successfully", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });
                }
            });
            //click listener for comment button
            final ImageView comment = mView.findViewById(R.id.ic_comment);
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), PostDetails.class);
                    intent.putExtra("key", mView.getTag().toString());
                    intent.putExtra("user", tvUserName.getText().toString());
                    intent.putExtra("post", tvTitle.getText().toString());
                    mView.getContext().startActivity(intent);
                }
            });

        }

//set post title
        public void setTitle(String title) {
            TextView tvTitle = mView.findViewById(R.id.txtCaption);
            tvTitle.setText(title);

        }
        //set post time

        public void setTime(long time) {
            TextView tvTime = mView.findViewById(R.id.txtTime);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
            calendar.setTimeInMillis(time);

            tvTime.setText(sdf.format(calendar.getTime()));
        }

//set post image
        public void setImage(String image) {
            ImageView ivImage = mView.findViewById(R.id.imageView);
            ivImage.setTag(image);
            if (image != null && !image.equals("")) {
                Picasso.get().load(image).into(ivImage);
            } else {
                //Picasso.with(context).load(R.drawable.noimage).resize(200, 200).into(ivImage);

            }
        }


    }
//craeate a new post
    public void createNewPost() {
        startActivity(new Intent(getContext(), PostActivity.class));
    }
}
