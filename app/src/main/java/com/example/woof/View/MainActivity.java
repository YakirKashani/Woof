package com.example.woof.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.Utils.NotificationWorker;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.ui.MedicalFragment;
import com.example.woof.ui.dashboard.DashboardFragment;
import com.example.woof.ui.home.HomeFragment;
import com.example.woof.ui.notifications.NotificationsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import com.example.woof.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FloatingActionButton FAB_DogPicture;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    private static final int REQUEST_CODE_NOTIFICATIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.navView.setBackground(null);

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.navigation_dashboard) {
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
                    if (response.isSuccessful() && response.body() != null) {
                        SwitchDogBottomSheet switchDogBottomSheet = new SwitchDogBottomSheet(response.body());
                        switchDogBottomSheet.show(getSupportFragmentManager(), "switchDogBottomSheet");
                    }
                }

                @Override
                public void onFailure(Call<List<Dog>> call, Throwable throwable) {

                }
            });

        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATIONS);
        }
        scheduleNotificationsWorker();
    }

    private void scheduleNotificationsWorker() {
        PeriodicWorkRequest notificationsWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DogNotificationWork",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationsWorkRequest
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_NOTIFICATIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                scheduleNotificationsWorker();
            }
        }
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