package com.example.instadam.components;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instadam.R;
import com.example.instadam.auth.LoginActivity;
import com.example.instadam.auth.RegisterActivity;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * The NavigationBarFragment is used to navigate between the different activities.
 * It shows the navigation bar at the bottom of the screen.
 */
public class NavigationBar extends Fragment {

    public NavigationBar() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_bar, container, false);
        BottomNavigationView navbar = view.findViewById(R.id.bottom_navigation);

        if (getActivity() instanceof FeedActivity) {
            navbar.setSelectedItemId(R.id.home);
        } else if (getActivity() instanceof LoginActivity) {
            navbar.setSelectedItemId(R.id.add);
        } else if (getActivity() instanceof RegisterActivity) {
            navbar.setSelectedItemId(R.id.profile);
        }

        navbar.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startFeedActivity();
                    break;
                case R.id.add:
                    startAddActivity();
                    break;
                case R.id.profile:
                    startProfileActivity();
                    break;
            }
            return true;
        });
        return view;
    }

    public void startFeedActivity() {
        if (getActivity() instanceof FeedActivity) {
            return;
        }
        Intent intent = new Intent(getActivity(), FeedActivity.class);
        startActivity(intent);
    }

    public void startAddActivity() {
        /**if (getActivity() instanceof LoginActivity) {
            return;
        }
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);*/
        return;
    }

    public void startProfileActivity() {
        if (getActivity() instanceof ProfileActivity) {
            return;
        }
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }
}