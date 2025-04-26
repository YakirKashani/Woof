package com.example.woof.Singleton;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.woof.Model.Dog;
import com.example.woof.Model.Owner;
import com.google.gson.Gson;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "woof_prefs";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_DOG = "dog";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SharedPreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveOwner(Owner owner){
        sharedPreferences.edit().putString(KEY_OWNER, gson.toJson(owner)).apply();
    }

    public Owner getOwner(){
        String json = sharedPreferences.getString(KEY_OWNER, null);
        return json != null ? gson.fromJson(json, Owner.class) : null;
    }

    public void saveDog(Dog dog){
        sharedPreferences.edit().putString(KEY_DOG, gson.toJson(dog)).apply();
    }

    public Dog getDog(){
        String json = sharedPreferences.getString(KEY_DOG, null);
        return json != null ? gson.fromJson(json, Dog.class) : null;
    }

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }
}
