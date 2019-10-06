package com.example.gpstrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.gpstrackerapp.fragments.HomeProfileFragment;
import com.example.gpstrackerapp.fragments.JoinCircleFragment;
import com.example.gpstrackerapp.fragments.MapFragment;
import com.example.gpstrackerapp.fragments.MyCircleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class BottomNavigation extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(navListener);
        navView.setSelectedItemId(R.id.navigation_profile);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.navigation_map:
                            selectedFragment = new MapFragment();
                            break;
                        case R.id.navigation_joincircle:
                            selectedFragment = new JoinCircleFragment();
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new HomeProfileFragment();
                            break;
                        case R.id.navigation_mycircle:
                            selectedFragment = new MyCircleFragment();
                            break;



                         case R.id.navigation_signout:
                             signOut();
                            return false;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };




    public void signOut() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            auth.signOut();
            finish();
            Intent myIntent = new Intent(BottomNavigation.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
}
