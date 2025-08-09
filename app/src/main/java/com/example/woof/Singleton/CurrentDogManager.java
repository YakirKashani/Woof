package com.example.woof.Singleton;

import android.content.Context;

import com.example.woof.Model.Dog;
import com.example.woof.Model.NewDog;
import com.example.woof.View.ChooseDogActivity;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentDogManager {
    private static CurrentDogManager instance;
    private Dog dog;

    private CurrentDogManager(){
    }

    public static synchronized CurrentDogManager getInstance(){
        if(instance==null)
            instance = new CurrentDogManager();
        return instance;
    }

    public Dog getDog(){
        return dog;
    }

    public void setDog(Dog dog, Context context){
        this.dog = dog;
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(context);
        prefs.saveDog(dog);
    }

    public void logout(Context context){
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(context);
        prefs.clear();
        instance = null;
        dog = null;
    }


}
