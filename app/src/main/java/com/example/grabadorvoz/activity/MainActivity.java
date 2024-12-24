package com.example.grabadorvoz.activity;

import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.KEY_MESSAGE;
import static com.example.grabadorvoz.widgets.dialog.ShowAlertDialogcustomKt.showAlertDialogcustom;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ade.accessControl.manager.PermissionsManager;
import com.example.grabadorvoz.Service.GrabacionService;
import com.example.grabadorvoz.activity.files.ShowFilesActivity;
import com.example.grabadorvoz.manager.HardwareManager;
import com.example.grabadorvoz.manager.managerData;
import com.galvancorp.spyapp.R;

public class MainActivity extends AppCompatActivity {

    private View mainView,audioView,camareView;
    private managerData data;
    private Button files,audio,record;
    private HardwareManager hardwareManager;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (isServiceRunning(GrabacionService.class)){
            setAudioView();
        } else {
            data = new managerData(this);
            if (!data.getBoolean(KEY_MESSAGE)) {
                showAlert("ADVERTENCIA", getString(R.string.warningapp));
            } else {
                Init();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isServiceRunning(GrabacionService.class)) {
                setAudioView();
            } else {
                setMemory();
                goneVisibilit(R.id.audioLayout);
                viewVisibility(R.id.mainLayout);
                accionButton();
            }
        } catch (Exception e) {
            Log.e("MainActivity","Error",e);
        }
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
                    data.saveBoolean(KEY_MESSAGE,true);
                    Init();
                },
                (dialog, which) -> {
                    finish();
                },
                "ACEPTAR",
                "CANCELAR"
        );
    }

    private void Init(){
        if (!new PermissionsManager(this).arePermissionsGranted()){
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        hardwareManager = new HardwareManager(this);
        setMemory();
        accionButton();
    }

    private void setMemory(){
        TextView RamView = findViewById(R.id.ramView);
        RamView.setText("RAM disponible"+"\n"+ hardwareManager.getMemoryAvailable()[0] + "GB /" + hardwareManager.getMemoryAvailable()[1] + "GB");
        TextView StoregeView = findViewById(R.id.storageView);
        StoregeView.setText("Almacenamineto disponible"+"\n"+ hardwareManager.getStorageInfo()[0] + "GB /" + hardwareManager.getStorageInfo()[1] + "GB");
    }

    private void accionButton(){
        findViewById(R.id.btnAudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), GrabacionService.class));
                onBackPressed();
                setAudioView();
            }
        });
        findViewById(R.id.btnVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.btnData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowFilesActivity.class));
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private void setAudioView() {
        setMemory();
        setContentView(R.layout.activity_main);
        goneVisibilit(R.id.mainLayout);
        viewVisibility(R.id.audioLayout);
        findViewById(R.id.btnAudioStop).setOnClickListener(view -> {
            Intent serviceIntent = new Intent(this, GrabacionService.class);
            stopService(serviceIntent);
            goneVisibilit(R.id.audioLayout);
            viewVisibility(R.id.mainLayout);
            accionButton();
        });
    }


    private void goneVisibilit(int layout){
        findViewById(layout).setVisibility(View.GONE);
    }

    private void viewVisibility(int layout){
        findViewById(layout).setVisibility(View.VISIBLE);
    }



}

