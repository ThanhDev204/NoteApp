package com.example.noteapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.noteapp.R;
import com.example.noteapp.database.NotesDatabase;
import com.example.noteapp.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView txtDateTime;
    private View viewSubtitleIndicator;
    private String selectedNoteColor;
    private String selectedImagePath;
    private ImageView imageNote;
    private TextView txtWebUrl;
    private LinearLayout layoutWebUrl;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private AlertDialog dialogUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgSave = findViewById(R.id.imgSave);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        txtDateTime = findViewById(R.id.txtDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndication);
        imageNote = findViewById(R.id.imageNote);
        txtWebUrl = findViewById(R.id.txtWebUrl);
        layoutWebUrl = findViewById(R.id.layoutWebUrl);

        txtDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
        selectedNoteColor = "#333333";
        selectedImagePath = "";
        initMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void saveNote() {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(txtDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);
        if(layoutWebUrl.getVisibility() == View.VISIBLE){
            note.setWebLink(txtWebUrl.getText().toString());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d("zzzzzzzzzzzzzzzzzzz", "Đang lưu note vào database...");
                NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Log.d("zzzzzzzzzzzzzzzzzzz", "Lưu note thành công! Kết thúc Activity.");
                Log.d("zzzzzzzzzzzzzzzzzzz","Image:"+note.getImagePath());
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
        new SaveNoteTask().execute();
    }

    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);
        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.baseline_done_24);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#fdbe3b";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.baseline_done_24);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#f44842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.baseline_done_24);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#3a52fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.baseline_done_24);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.baseline_done_24);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddUrlDialog();
            }
        });

    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage() {
        try {
            Log.d("ImagePicker", "Đang mở trình quản lý tệp...");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // Chỉ hiển thị file ảnh
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), REQUEST_CODE_SELECT_IMAGE);
        } catch (Exception e) {
            Log.e("SELECT_IMAGE", "Lỗi khi mở trình quản lý tệp: " + e.getMessage());
            Toast.makeText(this, "Không thể mở trình quản lý tệp!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Quyền truy cập ảnh đã được cấp!");
                selectImage(); // Gọi lại chọn ảnh sau khi cấp quyền
            } else {
                Log.e("PERMISSION", "Quyền truy cập ảnh bị từ chối!");
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String imagePath = getRealPathFromURI(this, selectedImageUri);
                Log.d("SELECT_IMAGE", "Đường dẫn ảnh thực tế: " + imagePath);

                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        // Cập nhật đường dẫn ảnh
                        selectedImagePath = getRealPathFromURI(this, selectedImageUri);

                        Log.d("SELECT_IMAGE", "Đường dẫn ảnh: " + selectedImagePath);
                    } catch (Exception e) {
                        Log.e("SELECT_IMAGE", "Lỗi khi xử lý ảnh: " + e.getMessage());
                        Toast.makeText(this, "Lỗi khi chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("SELECT_IMAGE", "URI ảnh null!");
                }
            }
        }
    }


    private String getRealPathFromURI(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            if (documentId.startsWith("raw:")) {
                return documentId.replaceFirst("raw:", ""); // Trả về đường dẫn đầy đủ
            }
        }

        // Kiểm tra nếu là URI từ MediaStore
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            try {
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e("GET_REAL_PATH", "Lỗi lấy đường dẫn: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        // Nếu là file trực tiếp
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }

        return filePath;
    }
    private void showAddUrlDialog(){
        if(dialogUrl==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,(ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogUrl = builder.create();
            if(dialogUrl.getWindow()!=null){
                dialogUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputUrl = view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();
            view.findViewById(R.id.txtAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputUrl.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this, "Enter Url", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                    }else{
                        txtWebUrl.setText(inputUrl.getText().toString());
                        layoutWebUrl.setVisibility(View.VISIBLE);
                        dialogUrl.dismiss();
                    }
                }
            });
            view.findViewById(R.id.txtCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogUrl.dismiss();
                }
            });
        }
        dialogUrl.show();
    }


}