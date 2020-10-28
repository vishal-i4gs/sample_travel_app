package com.example.sampletravelapp;

import android.app.Application;

import com.example.sampletravelapp.db.AppDatabase;

public class App extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        Repository.getInstance(this, AppDatabase.getInstance(this,mAppExecutors),mAppExecutors);
    }

    public Repository getRepository() {
        return Repository.getInstance(this,
                AppDatabase.getInstance(this,
                        mAppExecutors),mAppExecutors);
    }
}
