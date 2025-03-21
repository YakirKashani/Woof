package com.example.woof.Singleton;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    private static CloudinaryManager instance;

    private CloudinaryManager(Context context){
        Map<String,String> config = new HashMap<>();
        config.put("cloud_name","dhefmhtya");
        config.put("api_key","626938945753787");
        config.put("api_secret","wfh-ZH8Tlgiv_9ZnDkt8LoRAyFc");

        MediaManager.init(context,config);
    }

    public static synchronized CloudinaryManager getInstance(Context context){
        if(instance == null){
            instance = new CloudinaryManager(context.getApplicationContext());
        }
        return instance;
    }

    public void uploadImage(Uri imageUri, UploadCallback callback){
        MediaManager.get().upload(imageUri).callback(callback).dispatch();
    }
}
