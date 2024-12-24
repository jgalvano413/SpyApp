package com.example.grabadorvoz.activity.files;

import android.app.Activity;

import com.example.grabadorvoz.data.FileManager;

public class setRecyclerViewInitializer {

    private FileManager fileManager;
    private Activity a;

    public setRecyclerViewInitializer(Activity d){
        this.a = d;
        fileManager = new FileManager(a.getApplicationContext());
    }

    public void setRecyclerView(){

    }

}