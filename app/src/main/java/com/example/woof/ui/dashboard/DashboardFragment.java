package com.example.woof.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Adapters.SearchDogsAdapter;
import com.example.woof.Adapters.SearchOwnersAdapter;
import com.example.woof.Adapters.UserDogsAdapter;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.example.woof.R;
import com.example.woof.View.DogProfile;
import com.example.woof.View.MainActivity;
import com.example.woof.View.OwnerProfile;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.WoofBackend.OwnerApi;
import com.example.woof.databinding.FragmentDashboardBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SearchView Dashboard_SV_Search;
    private TabLayout Dashboard_TL_TabLayout;
    private RecyclerView Dashboard_RV;
    private List<Dog> dogs;
    private List<Owner> owners;
    private SearchDogsAdapter dogsAdapter;
    private SearchOwnersAdapter ownersAdapter;
    private boolean isDogs;
    private String searchText;

    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    OwnerApi ownerApiService = ApiController.getRetrofitInstance().create(OwnerApi.class);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews();
        initViews();
        return root;
    }

    private void findViews(){
        Dashboard_SV_Search = binding.getRoot().findViewById(R.id.Dashboard_SV_Search);
        Dashboard_TL_TabLayout = binding.getRoot().findViewById(R.id.Dashboard_TL_TabLayout);
        Dashboard_RV = binding.getRoot().findViewById(R.id.Dashboard_RV);
    }

    private void initViews(){
        dogs = new ArrayList<>();
        owners = new ArrayList<>();
        Dashboard_TL_TabLayout.getTabAt(0).select();
        isDogs = true;

        Dashboard_RV.setLayoutManager(new LinearLayoutManager(getContext()));
        dogsAdapter = new SearchDogsAdapter(dogs,selectedDog -> {
            //TODO: open dog profile intent
            Intent intent = new Intent(getActivity(),DogProfile.class);
            intent.putExtra("DogName",selectedDog.getName());
            intent.putExtra("DogOwner",selectedDog.getOwnerEmail());
            startActivity(intent);
        });
        ownersAdapter = new SearchOwnersAdapter(owners, selectedOwner -> {
            //TODO: open owner profile intent
            Intent intent = new Intent(getActivity(), OwnerProfile.class);
            intent.putExtra("OwnerEmail",selectedOwner.getMail());
            startActivity(intent);
        });

        Dashboard_RV.setAdapter(dogsAdapter);


        Dashboard_SV_Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Objects.equals(newText, "")) {
                    //Clean lists
                    searchText = "";
                    owners.clear();
                    dogs.clear();
                    dogsAdapter.notifyDataSetChanged();
                    ownersAdapter.notifyDataSetChanged();

                } else {
                    searchText = newText;
                    if (isDogs) {
                        fetchDogs(searchText);
                        Dashboard_RV.getAdapter().notifyDataSetChanged();
                    } else {
                        fetchOwners(searchText);
                        Dashboard_RV.getAdapter().notifyDataSetChanged();
                    }
                }
                return false;
            }
        });

        Dashboard_TL_TabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    //dogs
                    isDogs = true;
                    fetchDogs(searchText);
                    Dashboard_RV.setAdapter(dogsAdapter);
                }
                else{
                    //Owners
                    isDogs = false;
                    fetchOwners(searchText);
                    Dashboard_RV.setAdapter(ownersAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void fetchDogs(String dogName){
        if(dogName.equals("")){
            dogs.clear();
            dogsAdapter.notifyDataSetChanged();
            return;
        }
        Call<List<Dog>> call = dogApiService.searchDogs(dogName);
        call.enqueue(new Callback<List<Dog>>() {
            @Override
            public void onResponse(Call<List<Dog>> call, Response<List<Dog>> response) {
                dogs.clear();
                dogs.addAll(response.body());
                dogsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Dog>> call, Throwable throwable) {

            }
        });
    }

    private void fetchOwners(String ownerName){
        if(ownerName == ""){
            owners.clear();
            ownersAdapter.notifyDataSetChanged();;
            return;
        }
        Call<List<Owner>> call = ownerApiService.searchOwner(ownerName);
        call.enqueue(new Callback<List<Owner>>() {
            @Override
            public void onResponse(Call<List<Owner>> call, Response<List<Owner>> response) {
                owners.clear();
                owners.addAll(response.body());
                ownersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Owner>> call, Throwable throwable) {

            }
        });

    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}