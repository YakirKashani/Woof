package com.example.woof.Singleton;

import com.example.woof.Model.Owner;

public class CurrentUserManager {
    private static CurrentUserManager instance;
    private Owner owner;
    public final String defaultOwnerPicture = "https://res.cloudinary.com/dhefmhtya/image/upload/v1742553178/dppaivmuxdyxw6tp2v0n.png";


    private CurrentUserManager(){

    }

    public static CurrentUserManager getInstance(){
        if(instance == null)
            instance = new CurrentUserManager();
        return instance;
    }

    public Owner getOwner(){
        return owner;
    }

    public void setOwner(Owner owner){
        this.owner = owner;
    }
}
