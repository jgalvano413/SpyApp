package com.example.grabadorvoz.widgets.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.grabadorvoz.data.FileManager;
import com.galvancorp.spyapp.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int AUDIO_FILE = 0;
    private static final int AUDIO_VIDEO_FILE = 1;
    private Boolean loading = false;
    private Activity a;
    private FileManager manager;
    private Context context;
    public List<File> files;

    public FileAdapter(List<File> files, FileManager manager,Activity activity) {
        this.files = files;
        this.manager = manager;
        this.a = activity;
    }

    @Override
    public int getItemViewType(int position) {
        File file = files.get(position);
        if (isAudioFile(file)) {
            return AUDIO_FILE;
        } else if (isAudioVideoFile(file)) {
            return AUDIO_VIDEO_FILE;
        } else {
            return -1; // Para casos desconocidos, si los hay
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == AUDIO_FILE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_audio, parent, false);
            return new AudioFileViewHolder(view);
        } else if (viewType == AUDIO_VIDEO_FILE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_video, parent, false);
            return new AudioVideoFileViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_error, parent, false);
            return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = files.get(position);
        String date = getFormatdate(file.getName());
        if (holder instanceof AudioFileViewHolder) {
            ((AudioFileViewHolder) holder).bind(file,position,date,manager,this);
        } else if (holder instanceof AudioVideoFileViewHolder) {
            ((AudioVideoFileViewHolder) holder).bind(file,position,date,manager,this);
        }
        if (position >= files.size() - 1 && !loading) loadSuccesfull();
        holder.itemView.setTranslationY(3000);
        holder.itemView.animate().translationY(0).setDuration(500).start();
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private boolean isAudioFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav") || fileName.endsWith(".flac"); // Añadir más extensiones si es necesario
    }

    private boolean isAudioVideoFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mkv"); // Añadir más extensiones si es necesario
    }

    private void loadSuccesfull(){
        loading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            a.findViewById(R.id.loadngLayout).setVisibility(View.VISIBLE);
                            a.findViewById(R.id.main_layout).setVisibility(View.GONE);
                        }
                    });
                    Thread.sleep(2500);
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            a.findViewById(R.id.loadngLayout).setVisibility(View.GONE);
                            a.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ViewHolder para archivo de audio
    public static class AudioFileViewHolder extends RecyclerView.ViewHolder {
        MediaPlayer mediaPlayer;
        TextView fileName,dateView;
        MaterialButton saveButton,listenButton;

        public AudioFileViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.nameFileview);
            saveButton = itemView.findViewById(R.id.btn_save);
            listenButton = itemView.findViewById(R.id.btn_sound);
            dateView = itemView.findViewById(R.id.dateFileview);
            mediaPlayer = new MediaPlayer();
        }

        public void bind(File file,int position,String date,FileManager manager,FileAdapter adapter){
            fileName.setText(file.getName());
            dateView.setText(date);
            listenButton.setOnClickListener( click -> {
                try {
                    if (mediaPlayer.isPlaying()) {
                        stopAudio();
                    } else {
                        playAudio(file);
                    }
                } catch (Exception e){
                    Log.e("Reproduccion Error","IUNTILS",e);
                }

            });
            saveButton.setOnClickListener( click -> {
                if (manager.saveFileToGallery(file)){
                    adapter.files.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            });
        }

        private void playAudio(File file) {
            try {
                if (mediaPlayer != null) {
                    releasePlayer();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                listenButton.setText("Detener");
                mediaPlayer.setOnCompletionListener(mp -> {
                    listenButton.setText("Reproducir");
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(itemView.getContext(), "Error al reproducir el archivo", Toast.LENGTH_SHORT).show();
            }
        }

        private void stopAudio() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                listenButton.setText("Reproducir");
            }
        }

        public void releasePlayer() {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

    }

    // ViewHolder para archivo de audio y video
    public static class AudioVideoFileViewHolder extends RecyclerView.ViewHolder {
        MediaPlayer mediaPlayer;
        TextView fileName, dateView;
        MaterialButton saveButton, listenButton;
        VideoView videoView;  // Agregar VideoView

        public AudioVideoFileViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.nameFileview);
            saveButton = itemView.findViewById(R.id.btn_save);
            listenButton = itemView.findViewById(R.id.btn_sound);
            dateView = itemView.findViewById(R.id.dateFileview);
            videoView = itemView.findViewById(R.id.videoView);  // Obtener VideoView
        }

        public void bind(File file, int position, String date, FileManager manager, FileAdapter adapter) {
            fileName.setText(file.getName());
            dateView.setText(date);

            listenButton.setOnClickListener(click -> {
                try {
                    if (videoView.isPlaying()) {
                        listenButton.setText("Ver");
                        videoView.setVisibility(View.GONE);
                        videoView.stopPlayback();  // Detener el video si está en reproducción
                    } else {
                        // Reproducir video
                        listenButton.setText("Detener");
                        Uri videoUri = Uri.fromFile(file);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(videoUri);
                        videoView.start();  // Iniciar la reproducción del video
                    }
                } catch (Exception e) {
                    Log.e("Reproducción Error", "Error al reproducir el video", e);
                }
            });

            saveButton.setOnClickListener(click -> {
                if (manager.saveFileToGallery(file)) {
                    adapter.files.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            });
        }
    }


    // ViewHolder para otros tipos de archivos (por ejemplo, desconocidos)
    public static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView fileName,date;
        Button delete;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.nameFileview);
            date = itemView.findViewById(R.id.dateFileview);
            delete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(File file,int position,String date,FileAdapter adapter){
            fileName.setText(file.getName());
            fileName.setText(date);
            delete.setOnClickListener( click -> {
                file.delete();
                adapter.files.remove(position);
                adapter.notifyItemRemoved(position);
            });
        }
    }


    public String getFormatdate(String date){
        String[] parts = date.split("_");
        String hour = parts[2].replace("-",":");
        String day = parts[1].replace("-","/");
        String formattedDate = day + " " + hour;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            Date dateObj = dateFormat.parse(formattedDate);
            SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            String complete = dayOfWeekFormat.format(dateObj) + " " +formattedDate;
            return complete.replace(".mp3","");
        } catch (ParseException e) {
            e.printStackTrace();
            return "Fecha inválida";
        }
    }
}
