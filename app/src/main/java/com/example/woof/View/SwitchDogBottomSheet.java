package com.example.woof.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Adapters.SwitchDogAdapter;
import com.example.woof.Model.Dog;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.ui.home.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class SwitchDogBottomSheet extends BottomSheetDialogFragment {
    private List<Dog> dogs;

    public SwitchDogBottomSheet(List<Dog> dogs) {
        this.dogs = dogs;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dog_list, container, false);
        RecyclerView BSDL_RV_dogsRecyclerView = view.findViewById(R.id.BSDL_RV_dogsRecyclerView);
        BSDL_RV_dogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        BSDL_RV_dogsRecyclerView.setAdapter(new SwitchDogAdapter(dogs, dog -> {
            CurrentDogManager.getInstance().setDog(dog);
            dismiss();
            if(getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.replaceFragment(new HomeFragment());
                mainActivity.setSelectedNavItem(R.id.navigation_home);
            }
        }));
        return view;
    }
}
