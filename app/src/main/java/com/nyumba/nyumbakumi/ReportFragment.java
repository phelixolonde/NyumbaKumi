package com.nyumba.nyumbakumi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ReportFragment extends Fragment {


    View view;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    TextView loading, txtTime;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerAdapter<Report, ItemViewHolder> firebaseRecyclerAdapter;
    DatabaseReference nRef;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("reports");
        txtTime = view.findViewById(R.id.txtTime);

        loading = view.findViewById(R.id.txtLoading);

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);



        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Report, ItemViewHolder>(
                Report.class,
                R.layout.item_report,
                ItemViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, final Report model, int position) {
                loading.setVisibility(View.GONE);
                final String item_key = getRef(position).getKey();
                viewHolder.setTitle(model.getMessageText());
                viewHolder.setCount(model.getReportCount());

                viewHolder.mView.setTag(item_key);

                final TextView tvUserName = viewHolder.mView.findViewById(R.id.tvUserName);
                tvUserName.setText(model.getMessageUser());

                if (model.getPhoto() != null && !model.getPhoto().equals("")) {
                    viewHolder.setImage(model.getPhoto());
                }
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


        public void setTitle(String title) {
            TextView tvTitle = mView.findViewById(R.id.txtCaption);
            tvTitle.setText(title);

        }

        public void setCount(int count) {
            TextView tvCount = mView.findViewById(R.id.txtCount);

            tvCount.setText("Reports: "+count);
        }


        public void setImage(String image) {
            ImageView ivImage = mView.findViewById(R.id.imageView);
            if (image != null && !image.equals("")) {
                Picasso.get().load(image).into(ivImage);
            }  //Picasso.with(context).load(R.drawable.noimage).resize(200, 200).into(ivImage);

        }


    }

}
