package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Adapters.UserDogsAdapter;
import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.Singleton.SharedPreferencesHelper;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseDogActivity extends AppCompatActivity {
    private RecyclerView ACD_RV_UsersDogs;
    private MaterialButton ACD_MB_chooseDog;
    private MaterialTextView ACD_MTV_chooseDog;
    private MaterialButton ACD_MB_AddDog;
    private List<Dog> userDogs = new ArrayList<>();
    private Dog selectedDog = null;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_dog);
        findViews();
        initViews();
    }

    private void findViews() {
        ACD_RV_UsersDogs = findViewById(R.id.ACD_RV_UsersDogs);
        ACD_MB_chooseDog = findViewById(R.id.ACD_MB_chooseDog);
        ACD_MTV_chooseDog = findViewById(R.id.ACD_MTV_chooseDog);
        ACD_MB_AddDog = findViewById(R.id.ACD_MB_AddDog);
    }

    private void AddDogClicked(){
        Intent intent = new Intent(ChooseDogActivity.this,ChooseDogBreedActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        ACD_MB_chooseDog.setEnabled(false);
        ACD_RV_UsersDogs.setLayoutManager(new LinearLayoutManager(this));
        UserDogsAdapter adapter = new UserDogsAdapter(this, userDogs, selectedDog1 -> {
            this.selectedDog = selectedDog1;
            ACD_MB_chooseDog.setEnabled(true);
        });
        ACD_RV_UsersDogs.setAdapter(adapter);
        Call<List<Dog>> call = dogApiService.getAllDogsByOwner(CurrentUserManager.getInstance().getOwner().getMail());
        call.enqueue(new Callback<List<Dog>>() {
            @Override
            public void onResponse(Call<List<Dog>> call, Response<List<Dog>> response) {
                if (!response.isSuccessful() || response.body().isEmpty()) {
                    ACD_MTV_chooseDog.setVisibility(View.VISIBLE);
                    ACD_RV_UsersDogs.setVisibility(View.GONE);
                } else {
                    userDogs.clear();
                    userDogs.addAll(response.body());
                    ACD_RV_UsersDogs.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Dog>> call, Throwable throwable) {

            }
        });

        ACD_MB_AddDog.setOnClickListener(v -> AddDogClicked());
        ACD_MB_chooseDog.setOnClickListener(v -> {
            if (selectedDog != null) {
                CurrentDogManager.getInstance().setDog(selectedDog);
                SharedPreferencesHelper prefs = new SharedPreferencesHelper(ChooseDogActivity.this);
                prefs.saveDog(selectedDog);
                Intent intent = new Intent(ChooseDogActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}