package com.example.noteapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.noteapp.R;
import com.example.noteapp.entities.Note;
import com.example.noteapp.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private final NotesListener notesListener;
    private Timer timer;
    private final List<Note> noteSource;

    public NoteAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        noteSource = notes;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false)
        );
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position), position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note) {
            textTitle.setText(note.getTitle());

            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            // Kiểm tra và sửa đường dẫn nếu chỉ có tên file
            String imagePath = note.getImagePath();
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                imageNote.setVisibility(View.VISIBLE);

                // Nếu chỉ là tên file (không có đường dẫn), thêm đường dẫn thư mục mặc định
                if (!imagePath.startsWith("/storage") && !imagePath.startsWith("content://")) {
                    imagePath = "/storage/emulated/0/Download/" + imagePath;
                }

                File imageFile = new File(imagePath);

                if (imagePath.startsWith("content://")) {
                    // Xử lý ảnh từ URI
                    Glide.with(itemView.getContext())
                            .load(Uri.parse(imagePath))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_image)
                                    .error(R.drawable.baseline_error_24)
                                    .centerCrop())
                            .into(imageNote);
                } else if (imageFile.exists()) {
                    // Xử lý ảnh từ file path
                    Glide.with(itemView.getContext())
                            .load(imageFile)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_image)
                                    .error(R.drawable.baseline_error_24)
                                    .centerCrop())
                            .into(imageNote);
                } else {
                    Log.e("NoteAdapter", "File not found: " + imagePath);
                    imageNote.setImageResource(R.drawable.baseline_error_24);
                }
            } else {
                imageNote.setVisibility(View.GONE);
            }
        }

    }

    public void searchNotes(final String searchKeyWord) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyWord.trim().isEmpty()) {
                    notes = noteSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : noteSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyWord.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyWord.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                  @SuppressLint("NotifyDataSetChanged")
                  @Override
                  public void run() {
                      notifyDataSetChanged();
                  }
              });
            }
        }, 500);
    }
    public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }
}