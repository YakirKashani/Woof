package com.example.woof.WoofBackend;

import com.example.woof.Model.Dog;
import com.example.woof.Model.NewDog;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DogApi {

    @POST("dog")
    Call<Dog> createDog(@Body NewDog newDog);

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

    @PUT("dog/follow/{followedOwnerEmail}/{followedDogName}/{followingOwnerEmail}/{followingDogName}")
    Call<Boolean> followDog(
            @Path("followedOwnerEmail")  String followedOwnerEmail,
            @Path("followedDogName") String followedDogName,
            @Path("followingOwnerEmail") String followingOwnerEmail,
            @Path("followingDogName") String followingDogName
    );

    @PUT("dog/unfollow/{followedOwnerEmail}/{followedDogName}/{followingOwnerEmail}/{followingDogName}")
    Call<Boolean> unfollowDog(
            @Path("followedOwnerEmail")  String followedOwnerEmail,
            @Path("followedDogName") String followedDogName,
            @Path("followingOwnerEmail") String followingOwnerEmail,
            @Path("followingDogName") String followingDogName
    );
}
