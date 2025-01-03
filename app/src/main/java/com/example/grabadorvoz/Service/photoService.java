package com.example.grabadorvoz.Service;

import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.IS_SERVICE;
import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.NOTIFICATION;
import static com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.TAKE_PHOTO;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.grabadorvoz.Service.BroadcastReceiver.StopServiceReceiver;
import com.example.grabadorvoz.manager.managerData;
import com.galvancorp.spyapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class photoService extends Service{

    private BroadcastReceiver volumeButtonReceiver;
    private Boolean widgetPress = false;
    private managerData data;
    private NotificationManager manager;
    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private File fileName = null;
    private static final String CHANNEL_ID = "id_galvan";

    @SuppressLint("Override")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (data.getBooleanNotidication(NOTIFICATION)) {
            createNotificationChannel();
            startForeground(1, createSilentNotification());
        }else {
            createNotificationChannelSilets();
            startForeground(1, createSilentNotification2());
        }
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        data = new managerData(this);
        getidCamara();
        File cacheDir = getExternalCacheDir();
        if (cacheDir != null && !cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        fileName = new File(cacheDir, "IMG_" + getDate() + ".jpeg");
        data.saveBoolean(IS_SERVICE,true);
        //data.saveBoolean(TAKE_PHOTO,true);
        if (data.getBoolean(TAKE_PHOTO)) {
            widgetPress = true;
            takePhoto();
            Log.w("CameraService", "widget");
        } else {
            registerVolumeButtonReceiver();
            setupMediaSession();
            Log.w("CameraService", "Activado");
        }
    }

    private void getidCamara(){
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                String[] cameraList = cameraManager.getCameraIdList();
                for (String id : cameraList) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = id;
                        Log.w("CameraService", "idCamara: " + cameraId);
                        openCamera();
                        break;
                    }
                }
            } catch (CameraAccessException e) {
                Log.e("CameraService", "camaraid",e);
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.cancel(1);
    }

    private void setupMediaSession() {
        MediaSessionCompat mediaSession = new MediaSessionCompat(this, "PhotoService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP ||
                            keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        takePhoto();
                        Log.w("ServicePhoto","EventoResgistrado");
                        stopSelf();
                        return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });
        mediaSession.setMediaButtonReceiver(null);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setActive(true);
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            if (cameraId != null) {
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {

                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        cameraDevice = camera;
                        Log.w("CameraActivity", "Cámara abierta correctamente");
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        Log.e("CameraActivity", "Cámara desconectada");
                        cameraDevice.close();
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        Log.e("CameraActivity", "Error al abrir la cámara: " + error);
                    }
                }, null);
            }
        } catch (Exception e) {
            Log.e("CameraActivity", "Error al abrir la cámara", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void takePhoto() {
        if (cameraId != null && cameraDevice != null) {
            try {
                ImageReader imageReader = ImageReader.newInstance(
                        1920, 1080, ImageFormat.JPEG, 1);

                cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                            captureRequestBuilder.addTarget(imageReader.getSurface());
                            CaptureRequest captureRequest = captureRequestBuilder.build();

                            session.capture(captureRequest, new CameraCaptureSession.CaptureCallback() {
                                @Override
                                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                    super.onCaptureCompleted(session, request, result);
                                    Log.d("CameraService", "Foto tomada");
                                    data.saveBoolean(TAKE_PHOTO,false);
                                    saveImage(imageReader);
                                }
                            }, null);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.e("CameraService", "Configuración fallida para la sesión de captura");
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            data.saveBoolean(TAKE_PHOTO,false);
            Log.e("CameraService", "camaraid null");
        }
    }



    private void saveImage(ImageReader imageReader){
        try {
            Image image = imageReader.acquireLatestImage();
            FileOutputStream outputStream = new FileOutputStream(fileName);
            ByteBuffer imageBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[imageBuffer.remaining()];
            imageBuffer.get(bytes);
            outputStream.write(bytes);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(fileName);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        builder.setContentTitle("Camara en curso")
                .setContentText("Presiona algun boton de volumen para tomar la fotografia");
        return builder.build();
    }

    private Notification createNotification(String title, String content) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    private Notification createSilentNotification2() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setSilent(true);

        return builder.build();
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Servicio de Notificador", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private void registerVolumeButtonReceiver() {
        volumeButtonReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.w("CameraService", "Evento recibido");
                if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                    KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (keyEvent != null) {
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            takePhoto();
                            Log.w("CameraService", "Botón presionado");
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(volumeButtonReceiver, filter,RECEIVER_NOT_EXPORTED);
    }
}
