package com.example.gymrats.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gymrats.models.Post;
import com.example.gymrats.adapters.ProfileAdapter;
import com.example.gymrats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    protected TextView tvBio;
    protected List<Post> allPosts;
    protected RecyclerView rvPosts;
    protected ProfileAdapter adapter;
    protected ImageView ivProfileImage;
    protected TextView tvProfileUsername;
    protected SwipeRefreshLayout swipeContainer;
    public static final String TAG = "FeedActivity";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allPosts = new ArrayList<>();

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvBio = view.findViewById(R.id.tvBio);

        adapter = new ProfileAdapter(getContext(),allPosts);
        rvPosts = view.findViewById(R.id.rvGrid);

//       tvProfileUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvProfileUsername.setText(ParseUser.getCurrentUser().getUsername());
        Glide.with(getContext())
                .load(ParseUser.getCurrentUser().getParseFile("profileImage").getUrl())
                .circleCrop()
                .into(ivProfileImage);

        // set the apter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // query posts from Parstagram
        queryPosts();

    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();

            }
        });
    }
}