package com.codepath.apps.chirrup.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.activities.ProfileActivity;
import com.codepath.apps.chirrup.models.User;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


/**
 * Created by santoshag on 8/5/16.
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    // Store a member variable for the tweets
    private List<User> mUsers;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public FollowAdapter(Context context, List<User> users) {
        mUsers = users;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        //inflate the custom view
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_follow, parent, false);

        //return the viewholder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);


        return viewHolder;

    }

    //bind data to the viewholder
    @Override
    public void onBindViewHolder(FollowAdapter.ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);

        TextView tvUserName = viewHolder.tvUserName;
        TextView tvScreenName = viewHolder.tvScreenName;

        TextView tvDescription = viewHolder.tvDescription;
        ImageView ivProfileImg = viewHolder.ivProfileImg;

        tvUserName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
        tvDescription.setText(user.getDescription());
        ivProfileImg.setImageResource(android.R.color.transparent);

        ivProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", Parcels.wrap(user));
                getContext().startActivity(intent);
            }
        });
        Picasso.with(getContext()).load(user.getProfileImageUrl()).transform(new RoundedCornersTransformation(15, 0)).into(ivProfileImg);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvUserName, tvDescription, tvScreenName;
        public ImageView ivProfileImg;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivProfileImg = (ImageView) itemView.findViewById(R.id.ivProfileImg);
        }
    }


}

