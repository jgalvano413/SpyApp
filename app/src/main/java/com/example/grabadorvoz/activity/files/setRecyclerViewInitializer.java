package com.example.grabadorvoz.activity.files;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabadorvoz.data.FileManager;
import com.example.grabadorvoz.widgets.adapter.FileAdapter;
import com.galvancorp.spyapp.R;

public class setRecyclerViewInitializer {

    private FileManager fileManager;
    private Activity a;

    public setRecyclerViewInitializer(Activity d){
        this.a = d;
        fileManager = new FileManager(a.getApplicationContext());
        a.findViewById(R.id.backFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.onBackPressed();
            }
        });
    }

    public void setRecyclerView(){
        RecyclerView recyclerView = a.findViewById(R.id.recyclerFiles);
        FileAdapter adapter = new FileAdapter(fileManager.getCachedAudioFiles(),fileManager,a);
        recyclerView.setLayoutManager(new LinearLayoutManager(a.getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

}