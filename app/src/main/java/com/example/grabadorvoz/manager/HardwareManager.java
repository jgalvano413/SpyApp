package com.example.grabadorvoz.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class HardwareManager {

    private Context context;

    public HardwareManager(Context context){
        this.context = context;
    }

    public String[] getMemoryAvailable() {
        String[] memory = new String[2];
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        long totalMemory;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            totalMemory = memoryInfo.totalMem; // Total RAM en bytes
        } else {
            totalMemory = -1; // Valor por defecto si no está disponible
        }

        long availableMemory = memoryInfo.availMem; // RAM disponible

        // Convertir de bytes a GB
        float availableMemoryGB = availableMemory / (1024f * 1024f * 1024f);
        float totalMemoryGB = totalMemory / (1024f * 1024f * 1024f);

        memory[0] = String.format("%.2f", availableMemoryGB); // Formato con 2 decimales
        memory[1] = String.format("%.2f", totalMemoryGB);

        Log.d("MemoryUtil", "Total RAM: " + totalMemoryGB + " GB");
        Log.d("MemoryUtil", "RAM Disponible: " + availableMemoryGB + " GB");
        Log.d("MemoryUtil", "¿Bajo Memoria?: " + memoryInfo.lowMemory);

        return memory;
    }

    public String[] getStorageInfo() {
        String[] storage = new String[2];

        // Obtener la ruta del almacenamiento externo
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        // Obtener tamaño del bloque y cantidad de bloques
        long blockSize, totalBlocks, availableBlocks;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();        // Tamaño de bloque en bytes
            totalBlocks = statFs.getBlockCountLong();     // Cantidad total de bloques
            availableBlocks = statFs.getAvailableBlocksLong(); // Bloques disponibles
        } else {
            blockSize = statFs.getBlockSize();
            totalBlocks = statFs.getBlockCount();
            availableBlocks = statFs.getAvailableBlocks();
        }

        // Convertir a gigabytes (GB)
        // GB = byte / (1024 (kilo) x1024 (Mega) x1024 (Giga))
        float totalStorageGB = (blockSize * totalBlocks) / (1024f * 1024f * 1024f);
        float availableStorageGB = (blockSize * availableBlocks) / (1024f * 1024f * 1024f);

        // Formatear a 2 decimales
        storage[0] = String.format("%.2f", availableStorageGB);
        storage[1] = String.format("%.2f", totalStorageGB);

        // Log para depuración
        Log.d("StorageUtil", "Almacenamiento Total: " + totalStorageGB + " GB");
        Log.d("StorageUtil", "Almacenamiento Disponible: " + availableStorageGB + " GB");

        return storage;
    }


}

