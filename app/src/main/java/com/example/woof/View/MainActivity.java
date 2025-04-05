package com.example.woof.View;

import android.os.Bundle;

import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.ui.MedicalFragment;
import com.example.woof.ui.dashboard.DashboardFragment;
import com.example.woof.ui.home.HomeFragment;
import com.example.woof.ui.notifications.NotificationsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.woof.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FloatingActionButton FAB_DogPicture;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);

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
            } else if (itemId == R.id.navigation_medical) {
                replaceFragment(new MedicalFragment());
            } else if (itemId == R.id.navigation_notifications) {
                replaceFragment(new NotificationsFragment());
            }
            return true;
        });

        FAB_DogPicture = binding.getRoot().findViewById(R.id.FAB_DogPicture);
        FAB_DogPicture.setOnClickListener(v -> {
            Call<List<Dog>> call = dogApiService.getAllDogsByOwner(CurrentUserManager.getInstance().getOwner().getMail());
            call.enqueue(new Callback<List<Dog>>() {
                @Override
                public void onResponse(Call<List<Dog>> call, Response<List<Dog>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        SwitchDogBottomSheet switchDogBottomSheet = new SwitchDogBottomSheet(response.body());
                        switchDogBottomSheet.show(getSupportFragmentManager(),"switchDogBottomSheet");
                    }
                }

                @Override
                public void onFailure(Call<List<Dog>> call, Throwable throwable) {

                }
            });

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

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment);
        fragmentTransaction.commit();
    }

    public void setSelectedNavItem(int itemId){
        binding.navView.setSelectedItemId(R.id.navigation_home);

    }

}