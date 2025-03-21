package com.example.woof.Singleton;

import com.example.woof.Model.Dog;

public class CurrentDogManager {
    private static CurrentDogManager instance;
    private Dog dog;
    public final String defaultDogPicture = "https://res.cloudinary.com/dhefmhtya/image/upload/v1742553178/axepyphrnvbfvaqzcaco.png";

    private CurrentDogManager(){

    }

    public static CurrentDogManager getInstance(){
        if(instance==null)
            instance = new CurrentDogManager();
        return instance;
    }

    public Dog getDog(){
        return dog;
    }

    public void setDog(Dog dog){
        this.dog = dog;
    }
}
