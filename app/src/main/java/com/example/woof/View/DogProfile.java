package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.example.woof.R;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogProfile extends AppCompatActivity {
    private ImageView ADP_IV_OwnerPicture;
    private MaterialTextView ADP_MTV_textName;
    private TextView ADP_TV_textOwner;
    private TextView ADP_TV_textPosts;
    private TextView ADP_TV_textFollowers;
    private TextView ADP_TV_textFollowing;
    private ImageView ADP_IV_DogPicture;
    private RecyclerView ADP_RV_Posts;
    private MaterialButton ADP_MB_FollowMB;
    private String ownerEmail;
    private String dogName;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);
        findViews();
        readDataFromPreviousIntent();
        initViews();
    }

    private void findViews() {
        ADP_IV_OwnerPicture = findViewById(R.id.ADP_IV_OwnerPicture);
        ADP_MTV_textName = findViewById(R.id.ADP_MTV_textName);
        ADP_TV_textOwner = findViewById(R.id.ADP_TV_textOwner);
        ADP_TV_textPosts = findViewById(R.id.ADP_TV_textPosts);
        ADP_TV_textFollowers = findViewById(R.id.ADP_TV_textFollowers);
        ADP_TV_textFollowing = findViewById(R.id.ADP_TV_textFollowing);
        ADP_IV_DogPicture = findViewById(R.id.ADP_IV_DogPicture);
        ADP_RV_Posts = findViewById(R.id.ADP_RV_Posts);
        ADP_MB_FollowMB = findViewById(R.id.ADP_MB_FollowMB);
    }

    private void readDataFromPreviousIntent(){
        Intent intent = getIntent();
        ownerEmail = intent.getStringExtra("DogOwner");
        dogName = intent.getStringExtra("DogName");
    }

    private void initViews(){
        ADP_MTV_textName.setText(dogName);
        ADP_TV_textOwner.setText(ownerEmail);

        Call<Dog> DogCall = dogApiService.findDog(ownerEmail,dogName);
        DogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                Dog dog = response.body();
                if(dog.getPhotoURL() == null)
                    ADP_IV_DogPicture.setImageResource(R.drawable.default_dog_picture);
                else
                    Glide.with(DogProfile.this).load(dog.getPhotoURL()).error(R.drawable.default_dog_picture).into(ADP_IV_DogPicture);
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });

        Call<Owner> OwnerCall = ownerApiService.findOwner(ownerEmail);
        OwnerCall.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                Owner owner = response.body();
                if(owner.getPhotoURL() == null)
                    ADP_IV_OwnerPicture.setImageResource(R.drawable.default_owner_picture);
                else
                    Glide.with(DogProfile.this).load(owner.getPhotoURL()).error(R.drawable.default_owner_picture).into(ADP_IV_OwnerPicture);
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable throwable) {

            }
        });



        




    }

}