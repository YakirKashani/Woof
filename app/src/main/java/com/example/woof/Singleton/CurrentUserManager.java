package com.example.woof.Singleton;

import android.content.Context;

import com.example.woof.Model.Owner;

public class CurrentUserManager {
    private static CurrentUserManager instance;
    private Owner owner;


    private CurrentUserManager() {

    }

    public static CurrentUserManager getInstance() {
        if (instance == null)
            instance = new CurrentUserManager();
        return instance;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void logout(Context context) {
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(context);
        prefs.clear();
        instance = null;
        owner = null;
    }

}
