package com.example.grabadorvoz.activity.files;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.galvancorp.spyapp.R;


public class ShowFilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files);
        findViewById(R.id.backFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Init();
    }

    private void Init(){
        new setRecyclerViewInitializer(this).setRecyclerView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
