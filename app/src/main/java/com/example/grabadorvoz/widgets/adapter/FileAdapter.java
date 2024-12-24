package com.example.grabadorvoz.widgets.adapter;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabadorvoz.data.FileManager;
import com.galvancorp.spyapp.R;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int AUDIO_FILE = 0;
    private static final int AUDIO_VIDEO_FILE = 1;

    private  FileManager manager;
    private Context context;
    private List<File> files;

    public FileAdapter(Context context, List<File> files, FileManager manager) {
        this.context = context;
        this.files = files;
        this.manager = manager;
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
        if (viewType == AUDIO_FILE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_audio_file, parent, false);
            return new AudioFileViewHolder(view);
        } else if (viewType == AUDIO_VIDEO_FILE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_audio_video_file, parent, false);
            return new AudioVideoFileViewHolder(view);
        } else {
            // Fallback si no es un tipo reconocido
            View view = LayoutInflater.from(context).inflate(R.layout.item_default, parent, false);
            return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = files.get(position);
        if (holder instanceof AudioFileViewHolder) {
            ((AudioFileViewHolder) holder).fileName.setText(file.getName());
            ((AudioFileViewHolder) holder).saveButton.setOnClickListener(v -> manager.saveFileToGallery(file));
        } else if (holder instanceof AudioVideoFileViewHolder) {
            ((AudioVideoFileViewHolder) holder).fileName.setText(file.getName());
            ((AudioVideoFileViewHolder) holder).saveButton.setOnClickListener(v -> manager.saveFileToGallery(file));
        }
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

    // ViewHolder para archivo de audio
    public static class AudioFileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        Button saveButton;

        public AudioFileViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            saveButton = itemView.findViewById(R.id.save_button);
        }
    }

    // ViewHolder para archivo de audio y video
    public static class AudioVideoFileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        Button saveButton;

        public AudioVideoFileViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            saveButton = itemView.findViewById(R.id.save_button);
        }
    }

    // ViewHolder para otros tipos de archivos (por ejemplo, desconocidos)
    public static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
        }
    }
}
