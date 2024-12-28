package com.example.grabadorvoz.Service;

import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.IS_SERVICE;
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
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.grabadorvoz.Service.BroadcastReceiver.StopServiceReceiver;
import com.example.grabadorvoz.manager.managerData;
import com.galvancorp.spyapp.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class videoRecording extends Service {

    private managerData data;
    private NotificationManager manager;
    private Camera camera;
    private MediaRecorder recorder = null;
    private Surface surfaceHolder;
    private String fileName = null;
    private static final String CHANNEL_ID = "id_galvan";

    @Override
    public void onCreate() {
        data = new managerData(this);
        File cacheDir = getExternalCacheDir(); // Directorio de caché externo
        if (cacheDir != null && !cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        fileName = new File(cacheDir, "videorecord_" + getDate() + ".mp4").getAbsolutePath();
        showToast(this, this.getString(R.string.serviceStart),true);
        data.saveBoolean(IS_SERVICE,true);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(1, createSilentNotification());

        // Obtén el SurfaceHolder de la vista en la que quieres mostrar la vista previa del video
        surfaceHolder = (Surface) intent.getExtras().get("surface");
        startRecording();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast(this, this.getString(R.string.serviceStop),true);
        stopRecording();
        manager.cancel(1);
        data.saveBoolean(IS_SERVICE,false);
    }

    @SuppressLint("RestrictedApi")
    private void startRecording() {
        camera = Camera.open();
        recorder = new MediaRecorder();
        camera.unlock();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Fuente de audio
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // Fuente de video
        // Establecer formato de salida
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // Establecer el archivo de salida
        recorder.setOutputFile(fileName);

        // Configurar la vista previa del video
        recorder.setPreviewDisplay(surfaceHolder);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e("GrabacionService", "prepare() failed", e);
        }
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                camera.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VideoRecording","errorStop",e);
        }
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
                .setSmallIcon(R.drawable.baseline_camera_alt_24)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .addAction(R.drawable.baseline_camera_alt_24, "Detener", stopPendingIntent);
        builder.setContentTitle("Grabación en curso")
                .setContentText("Presiona para detener la grabación");

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Servicio de Notificador", NotificationManager.IMPORTANCE_HIGH);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return dateFormat.format(date);
    }
}
