package com.example.woof.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogbreedslib.BreedAndUrlModel;
import com.example.dogbreedslib.DogData;
import com.example.woof.Adapters.DogBreedsAdapter;
import com.example.woof.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseDogBreedActivity extends AppCompatActivity {

    private RecyclerView ACD_RV_ChooseBreed;
    private MaterialButton ACD_MB_next;
    List<BreedAndUrlModel> breedAndUrlModels = new ArrayList<>();
    private BreedAndUrlModel selectedBreed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_dog_breed);
        findViews();
        initViews();
    }

    private void findViews(){
        ACD_RV_ChooseBreed = findViewById(R.id.ACD_RV_ChooseBreed);
        ACD_MB_next = findViewById(R.id.ACD_MB_next);
    }

    private void initViews(){
        ACD_MB_next.setEnabled(false);
        ACD_RV_ChooseBreed.setLayoutManager(new GridLayoutManager(this,2));
        DogBreedsAdapter adapter = new DogBreedsAdapter(breedAndUrlModels, selectedBreed -> {
            this.selectedBreed = selectedBreed;
            ACD_MB_next.setEnabled(true);
        });
        ACD_RV_ChooseBreed.setAdapter(adapter);
        DogData.getAllDogsAndUrls(this, new DogData.Callback_Data<List<BreedAndUrlModel>>() {
            @Override
            public void data(List<BreedAndUrlModel> value) {
                breedAndUrlModels.clear();
                breedAndUrlModels.addAll(value);
                ACD_RV_ChooseBreed.getAdapter().notifyDataSetChanged();
            }
        });


        ACD_MB_next.setOnClickListener(v -> {
            if(selectedBreed!=null){
                Intent intent = new Intent(ChooseDogBreedActivity.this,DogNameAndDobActivity.class);
                intent.putExtra("selected breed",selectedBreed.getBreed_name());
                startActivity(intent);
            }
        });
    }
}