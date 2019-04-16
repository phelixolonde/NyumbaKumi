package com.nyumba.nyumbakumi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;


public class PostDetails extends AppCompatActivity {

    String key, username, user, post;
    DatabaseReference mRef;
    ListView listView;
    EditText txtComment;
    TextView tvUser, tvPost;
    Button btnComment;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        key = getIntent().getExtras().getString("key");
        user = getIntent().getExtras().getString("user");
        post = getIntent().getExtras().getString("post");


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        mRef = FirebaseDatabase.getInstance().getReference().child("nyumbakumi").child("posts").child(key).child("comments");
        txtComment = findViewById(R.id.txtComment);
        tvUser = findViewById(R.id.postUser);
        tvUser.setText(user);
        tvPost = findViewById(R.id.postTitle);
        tvPost.setText(post);
        listView = findViewById(R.id.list_msg);
        btnComment = findViewById(R.id.btnComment);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = 1 - System.currentTimeMillis() / 1000;
                final String ts = tsLong.toString();
                SharedPreferences sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
                username = sp.getString("username", "user");

                mRef.child(ts).setValue(new Comment(txtComment.getText().toString(), username,System.currentTimeMillis()));
                txtComment.setText("");
            }
        });
        //set ListView adapter first

        adapter = new CommentAdapter(this, Comment.class, R.layout.item_chat_left, mRef);
        listView.setAdapter(adapter);


        showAllComments();


    }



    private void showAllComments() {

        adapter = new CommentAdapter(this, Comment.class, R.layout.item_comment, mRef);
        listView.setAdapter(adapter);

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, ItemViewHolder>(
                Comment.class,
                R.layout.item_comment,
                ItemViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, final Comment model, int position) {

                final String item_key = getRef(position).getKey();
                viewHolder.setTitle(model.getCommentText());
                viewHolder.mView.setTag(item_key);

                viewHolder.setUser(model.getCommentUser());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent adDetails = new Intent(v.getContext(), PostDetails.class);
                        adDetails.putExtra("item_id", item_key);
                        startActivity(adDetails);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public ItemViewHolder(View v) {
            super(v);
            mView = v;

            ImageView report = mView.findViewById(R.id.ic_report);
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            final ImageView comment = mView.findViewById(R.id.ic_comment);
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), PostDetails.class);
                    intent.putExtra("key", mView.getTag().toString());
                    mView.getContext().startActivity(intent);
                }
            });
        }


        public void setTitle(String title) {
            TextView tvTitle = mView.findViewById(R.id.comment);
            tvTitle.setText(title);

        }


        public void setUser(String user) {

            TextView tvTitle = mView.findViewById(R.id.user);
            tvTitle.setText(user);
        }


    }
*/

}
