package com.example.grabadorvoz.activity;


import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ade.accessControl.manager.PermissionsManager;
import com.galvancorp.spyapp.R;

public class SplashActivity extends AppCompatActivity {
    private PermissionsManager permissionsManager;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        permissionsManager = new PermissionsManager(this);
        message = findViewById(R.id.messageSplash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        permissionsManager.checkAndRequestPermissions();
        permissionsManager.getAlert().observe(this, it -> {
            if (it) {
                int[] seconds = {8};
                Thread thread = new Thread(() -> {
                    while (true) {
                        seconds[0]--;
                        runOnUiThread(() -> {
                            if (message != null) {
                                message.setText(getString(R.string.denied_permiss) + " " + seconds[0]);
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (seconds[0] <= 1) {
                            finish();
                            break;
                        }
                    }
                });
                thread.start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.handlePermissionsResult(requestCode, permissions, grantResults);
    }
}