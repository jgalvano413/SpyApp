package com.example.grabadorvoz.widgets.windows;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraDevice;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.grabadorvoz.Service.photoService;

public class FloatingCamera {

    private Context context;
    private SurfaceView surfaceView;
    private WindowManager windowManager;
    private Surface surface;

    public FloatingCamera(Context context){
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        windowManager.addView(surfaceView, params);
        surface = surfaceView.getHolder().getSurface();
    }

    public void showWindowsCamamra(CameraDevice cameraDevice){




    }


}
