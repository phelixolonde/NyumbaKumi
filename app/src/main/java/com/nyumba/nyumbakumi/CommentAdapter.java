package com.nyumba.nyumbakumi;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends FirebaseListAdapter<Comment> {

    private PostDetails activity;
    TextView txtTime;

    public CommentAdapter(PostDetails activity, Class<Comment> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
    }

    @Override
    protected void populateView(View v, final Comment model, int position) {
        TextView messageText = v.findViewById(R.id.comment);
        final TextView messageUser = v.findViewById(R.id.user);
        TextView txtTime = v.findViewById(R.id.txtTime);

        messageText.setText(model.getCommentText());
        messageUser.setText(model.getCommentUser());
       // txtTime.setTime(model.getTime());

        TextView tvTime=v.findViewById(R.id.txtTime);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());

        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        long time = model.getTime();
        calendar.setTimeInMillis(time);

        tvTime.setText(sdf.format(calendar.getTime()));


        // Format the date before showing it
       txtTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view==null){
            LayoutInflater lInflater = (LayoutInflater)activity.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            view = lInflater.inflate(R.layout.activity_post_details, null);
            return view;

        }

        Comment comment = getItem(position);


            view = activity.getLayoutInflater().inflate(R.layout.item_comment, viewGroup, false);

        //generating view
        populateView(view, comment, position);



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

