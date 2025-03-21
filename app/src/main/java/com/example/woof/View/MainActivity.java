package com.example.woof.View;

import android.os.Bundle;

import com.example.woof.R;
import com.example.woof.ui.CommunityFragment;
import com.example.woof.ui.dashboard.DashboardFragment;
import com.example.woof.ui.home.HomeFragment;
import com.example.woof.ui.notifications.NotificationsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.woof.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.navView.setBackground(null);

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.navigation_home){
                replaceFragment(new HomeFragment());
            }
            else if (itemId == R.id.navigation_dashboard) {
                replaceFragment(new DashboardFragment());
            } else if (itemId == R.id.navigation_community) {
                replaceFragment(new CommunityFragment());
            } else if (itemId == R.id.navigation_notifications) {
                replaceFragment(new NotificationsFragment());
            }
            return true;
        });

        /*
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_community)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

         */
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment);
        fragmentTransaction.commit();
    }

}