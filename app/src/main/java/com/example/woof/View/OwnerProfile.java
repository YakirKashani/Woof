package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Adapters.DogProfileInOwnerAdapter;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.example.woof.R;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProfile extends AppCompatActivity {
    private ImageView OP_IV_OwnerPicture;
    private MaterialTextView OP_MTV_TextName;
    private TextView OP_TV_DogsCounter;
    private RecyclerView OP_RV_Dogs;
    private String ownerEmail;
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);
        findViews();
        readDataFromPreviousIntent();
        initViews();
    }

    private void findViews() {
        OP_IV_OwnerPicture = findViewById(R.id.OP_IV_OwnerPicture);
        OP_MTV_TextName = findViewById(R.id.OP_MTV_TextName);
        OP_TV_DogsCounter = findViewById(R.id.OP_TV_DogsCounter);
        OP_RV_Dogs = findViewById(R.id.OP_RV_Dogs);
    }

    private void readDataFromPreviousIntent(){
        Intent intent = getIntent();
        ownerEmail = intent.getStringExtra("OwnerEmail");
    }

    private void initViews(){
        Call<Owner> call = ownerApiService.findOwner(ownerEmail);
        call.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                Owner owner = response.body();
                if(owner.getPhotoURL() == null)
                    OP_IV_OwnerPicture.setImageResource(R.drawable.default_owner_picture);
                else
                    Glide.with(OwnerProfile.this).load(owner.getPhotoURL()).error(R.drawable.default_owner_picture).into(OP_IV_OwnerPicture);
                OP_MTV_TextName.setText(owner.getMail());
                Call<List<Dog>> dogsCall = dogApiService.getAllDogsByOwner(ownerEmail);
                dogsCall.enqueue(new Callback<List<Dog>>() {
                    @Override
                    public void onResponse(Call<List<Dog>> call, Response<List<Dog>> response) {
                        OP_TV_DogsCounter.setText(String.valueOf(response.body().size()));
                        OP_RV_Dogs.setLayoutManager(new GridLayoutManager(OwnerProfile.this,2));
                        DogProfileInOwnerAdapter adapter = new DogProfileInOwnerAdapter(OwnerProfile.this, response.body(), new DogProfileInOwnerAdapter.OnDogProfileInOwnerChooseListener() {
                            @Override
                            public void onDogSelected(Dog selectedDog) {
                                // TODO: switch intent to dog profile
                                Intent intent = new Intent(OwnerProfile.this,DogProfile.class);
                                intent.putExtra("DogName",selectedDog.getName());
                                intent.putExtra("DogOwner",selectedDog.getOwnerEmail());
                                startActivity(intent);
                            }
                        });
                        OP_RV_Dogs.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<Dog>> call, Throwable throwable) {

                    }
                });

            }

            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {

            }
        });
    }
}