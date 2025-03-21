package com.example.woof.WoofBackend;

import com.example.woof.Model.Dog;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DogApi {

    @POST("dog")
    Call<Dog> createDog(@Body Dog newDog);

    @GET("dog/{ownerEmail}/{dogName}")
    Call<Dog> findDog(
            @Path("ownerEmail") String ownerEmail,
            @Path("dogName") String dogName
    );

    @GET("dog/getOwnerDogs/{ownerEmail}")
    Call<List<Dog>>getAllDogsByOwner(
            @Path("ownerEmail") String ownerEmail
    );

    @GET("dog/search/{dogName}")
    Call<List<Dog>>searchDogs(
            @Path("dogName") String dogName
    );
}
