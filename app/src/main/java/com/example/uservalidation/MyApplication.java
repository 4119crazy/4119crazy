package com.example.uservalidation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.adpter.data.JsonFileUtil;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences=getSharedPreferences("uuid",Context.MODE_PRIVATE);
        String mm=preferences.getString("uuid",null);
        if(mm==null){
            String uuid=JsonFileUtil.getJson().get_uuid();
            SharedPreferences preferences1=getSharedPreferences("uuid",Context.MODE_PRIVATE);
            preferences1.edit().putString("uuid",uuid).commit();
        }
        else{
            return;
        }


    }

}
