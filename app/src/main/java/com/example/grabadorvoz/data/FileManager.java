package com.example.grabadorvoz.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.galvancorp.spyapp.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    private Context context;

    public FileManager(Context context){
        this.context = context;
    }

    @SuppressLint("NewApi")
    public Boolean saveFileToGallery(File file) {
        File downloadsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name));
        if (!downloadsFolder.exists()) {
            downloadsFolder.mkdirs();
        }
        File targetFile = new File(downloadsFolder, file.getName());
        try {
            Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            file.delete();
            Toast.makeText(context, "Archivo guardado en la galería", Toast.LENGTH_SHORT).show();
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(targetFile));
            context.sendBroadcast(scanIntent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public List<File> getCachedAudioFiles() {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null && cacheDir.isDirectory()) {
            // Listar todos los archivos sin filtro
            File[] files = cacheDir.listFiles();
            return files != null ? new ArrayList<>(Arrays.asList(files)) : new ArrayList<>();
        }
        return new ArrayList<>();
    }





}
