package com.example.grabadorvoz.Service;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.IS_SERVICE;
import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.NOTIFICATION;
import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.WIDGET_TYPE;
import static com.example.pruebaremoto.widgets.toast.ToastKt.showToast;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.grabadorvoz.Service.BroadcastReceiver.StopServiceReceiver;
import com.example.grabadorvoz.data.FileManager;
import com.example.grabadorvoz.manager.managerData;
import com.example.grabadorvoz.widgets.WidgetProvider.updateWidget;
import com.galvancorp.spyapp.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GrabacionService extends Service {

    private updateWidget widget;
    private managerData data;
    private NotificationManager manager;
    private MediaRecorder recorder = null;
    private String fileName = null;
    private static final String CHANNEL_ID = "id_galvan";

    @Override
    public void onCreate() {
        data = new managerData(this);
        File cacheDir = getExternalCacheDir(); // Directorio de caché externo
        if (cacheDir != null && !cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        fileName = new File(cacheDir, "audiorecord_" + getDate() + ".mp3").getAbsolutePath();
        //showToast(this, this.getString(R.string.serviceStart),true);
        data.saveBoolean(IS_SERVICE,true);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        widget = new updateWidget(this);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (data.getBooleanNotidication(NOTIFICATION)) {
            createNotificationChannel();
            startForeground(1, createSilentNotification());
        }else {
            createNotificationChannelSilets();
            startForeground(1, createSilentNotification2());
        }
        startRecording();
        return START_STICKY;
    }

    private Notification createSilentNotification2() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.GROUP_KEY_SILENT)
                .setSilent(true);
        builder.setContentTitle("")
                .setContentText("");

        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //showToast(this, this.getString(R.string.serviceStop),true);
        stopRecording();
        manager.cancel(1);
        data.saveBoolean(IS_SERVICE,false);
        widget.changeLayout(this, GrabacionService.class,false);
    }

    @SuppressLint("RestrictedApi")
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed",e);
        }
        if (!data.getBooleanNotidication(NOTIFICATION)) manager.cancel(1);
        widget.changeLayout(this, GrabacionService.class,true);
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private Notification createSilentNotification() {
        Intent stopIntent = new Intent(this, StopServiceReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_stop_24)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .addAction(R.drawable.baseline_stop_24, "Detener", stopPendingIntent);
        builder.setContentTitle("Grabación en curso")
                .setContentText("Presiona para detener la grabación");

        return builder.build();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Servicio de Notificador", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelSilets(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servicio de Notificador",
                    NotificationManager.IMPORTANCE_MIN
            );
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            manager.createNotificationChannel(channel);
        }
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return dateFormat.format(date);
    }
}


