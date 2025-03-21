/*package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Singleton.CurrentUserManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDogWeightAndHeightActivity extends AppCompatActivity {

    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);

    private ImageView WAH_IV_Previous;
    private MaterialButton WAH_MB_Finish;


    private Switch WAH_Switch_WeightSwitch;
    private MaterialTextView WAH_MTV_WeightSeekbarValue;
    private SeekBar WAH_Seekbar_Weight;
    private float WeightValue;

    private Switch WAH_Switch_HeightSwitch;
    private MaterialTextView WAH_MTV_HeightSeekbarValue;
    private SeekBar WAH_Seekbar_Height;
    float HeightValue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dog_weight_and_height);
        findViews();
        initViews();
    }

    private void findViews() {
        WAH_IV_Previous = findViewById(R.id.WAH_IV_Previous);
        WAH_MB_Finish = findViewById(R.id.WAH_MB_Finish);

        WAH_Switch_WeightSwitch = findViewById(R.id.WAH_Switch_WeightSwitch);
        WAH_MTV_WeightSeekbarValue = findViewById(R.id.WAH_MTV_WeightSeekbarValue);
        WAH_Seekbar_Weight = findViewById(R.id.WAH_Seekbar_Weight);

        WAH_Switch_HeightSwitch = findViewById(R.id.WAH_Switch_HeightSwitch);
        WAH_MTV_HeightSeekbarValue = findViewById(R.id.WAH_MTV_HeightSeekbarValue);
        WAH_Seekbar_Height = findViewById(R.id.WAH_Seekbar_Height);
    }

    private void initViews() {
        WAH_Seekbar_Weight.setEnabled(false);
        WAH_Seekbar_Height.setEnabled(false);

        WAH_IV_Previous.setOnClickListener(v -> { // TODO: return to previous page

        });

        updateWeightSeekBarValue(WAH_Seekbar_Weight.getProgress());
        WAH_Switch_WeightSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            WAH_Seekbar_Weight.setEnabled(isChecked);
            WAH_MTV_WeightSeekbarValue.setVisibility(View.VISIBLE);
        }));
        WAH_Seekbar_Weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateWeightSeekBarValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateHeightSeekBarValue(WAH_Seekbar_Height.getProgress());
        WAH_Switch_HeightSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            WAH_MTV_HeightSeekbarValue.setVisibility(View.VISIBLE);
            WAH_Seekbar_Height.setEnabled(isChecked);
        }));
        WAH_Seekbar_Height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateHeightSeekBarValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        WAH_MB_Finish.setOnClickListener(v -> {
            Dog dog = new Dog();
            dog.setBreed(getIntent().getStringExtra("DogBreed").toString());
            dog.setOwnerEmail(CurrentUserManager.getInstance().getOwner().getMail());
            dog.setPhotoURL(getIntent().getStringExtra("DogPhoto").toString());
            dog.setName(getIntent().getStringExtra("DogName").toString());
            dog.setDob(getIntent().getStringExtra("DogDob").toString());
            dog.setGender(getIntent().getStringExtra("DogGender").toString());
            dog.setWeight(WAH_Switch_WeightSwitch.isEnabled() ? WeightValue : -1);
            dog.setHeight(WAH_Switch_HeightSwitch.isEnabled() ? HeightValue : -1);

            Call<Dog> call = dogApiService.createDog(dog);
            call.enqueue(new Callback<Dog>() {
                @Override
                public void onResponse(Call<Dog> call, Response<Dog> response) {
                    Dog createdDog = response.body();
                    if(response.isSuccessful() && createdDog!=null){
                        Toast.makeText(NewDogWeightAndHeightActivity.this,"Dog created successfully!",Toast.LENGTH_LONG).show();
                        Call<Void> addDogToListCall = ownerApiService.addDog(createdDog.getOwnerEmail(),createdDog.getName());
                        addDogToListCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(NewDogWeightAndHeightActivity.this,"Dog added to owner list successfully!",Toast.LENGTH_LONG).show();
                                CurrentDogManager.getInstance().setDog(createdDog);
                                switchIntent();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable throwable) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Dog> call, Throwable throwable) {

                }
            });
        });
    }

    private void updateWeightSeekBarValue(int progress){
        WeightValue = progress/10.0f;
        WAH_MTV_WeightSeekbarValue.setText(String.format("%.1f",WeightValue));

        float percentage = (float) progress / WAH_Seekbar_Weight.getMax();
        int seekBarWidth = WAH_Seekbar_Weight.getWidth() - WAH_Seekbar_Weight.getPaddingLeft() - WAH_Seekbar_Weight.getPaddingRight();
        int thumbX = (int) ((percentage * seekBarWidth + WAH_Seekbar_Weight.getPaddingLeft() - (WAH_MTV_WeightSeekbarValue.getWidth()/2)) + 1);
        WAH_MTV_WeightSeekbarValue.setX(thumbX);
    }

    private void updateHeightSeekBarValue(int progress){
        HeightValue = (float) progress;
        WAH_MTV_HeightSeekbarValue.setText(String.valueOf(progress));

        float percentage = (float) progress / WAH_Seekbar_Height.getMax();
        int seekBarWidth = WAH_Seekbar_Height.getWidth() - WAH_Seekbar_Height.getPaddingLeft() - WAH_Seekbar_Height.getPaddingRight();
        int thumbX = (int) ((percentage * seekBarWidth + WAH_Seekbar_Height.getPaddingLeft() - (WAH_MTV_HeightSeekbarValue.getWidth()/2)) + 1);
        WAH_MTV_HeightSeekbarValue.setX(thumbX);
    }

    private void switchIntent(){
        Intent intent = new Intent(NewDogWeightAndHeightActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}*/