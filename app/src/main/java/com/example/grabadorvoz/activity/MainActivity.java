package com.example.grabadorvoz.activity;

import static com.example.grabadorvoz.widgets.ShowAlertDialogcustomKt.showAlertDialogcustom;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ade.accessControl.manager.PermissionsManager;
import com.example.grabadorvoz.Service.GrabacionService;
import com.example.grabadorvoz.R;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!new PermissionsManager(this).arePermissionsGranted()){
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        showAlert("ADVERTENCIA",getString(R.string.warningapp));
//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
//        startService(new Intent(this, GrabacionService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isServiceRunning(GrabacionService.class)){
            Intent serviceIntent = new Intent(this, GrabacionService.class);
            Log.e("Servicio","Detenido");
            stopService(serviceIntent);
        }
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(String tittle, String message) {
        showAlertDialogcustom(
                this,
                tittle,
                message,
                (dialog, which) -> {
                    setContentView(R.layout.activity_main);
                },
                (dialog, which) -> {
                    finish();
                },
                "ACEPTAR",
                "CANCELAR"
        );
    }

}

