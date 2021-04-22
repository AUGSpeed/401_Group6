package com.example.audiotracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ProjectEditor extends AppCompatActivity {
    Button popupButton;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String pathSave="";
    final int REQUEST_PERMISSION_CODE = 1236;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Button mainButton = findViewById(R.id.project_button);
        mainButton.setText(message);
        requestPermission();
    }

    public void playFunction(View view)
    {
        Button playButton = findViewById(R.id.play_button);
        Button recordButton = findViewById(R.id.record_button);
        mediaPlayer = new MediaPlayer();
        if (playButton.getText().toString().equals("Play")) {
            recordButton.setEnabled(false);

            try {
                mediaPlayer.setDataSource(pathSave);
                mediaPlayer.prepare();


            } catch(IOException e){
                e.printStackTrace();
            }

            mediaPlayer.start();
            playButton.setText("Stop");
            Toast.makeText(ProjectEditor.this, "Playing", Toast.LENGTH_SHORT).show();
        }
        else if (playButton.getText().toString().equals("Stop"))
        {
            mediaPlayer.stop();
            recordButton.setEnabled(true);
        }



        //System.out.println(mediaPlayer.getDuration());
        /*This code is incredibly laggy, but does let us determine when the recording ends.
        (while(mediaPlayer.isPlaying())
        {
            if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration())
            {
                Toast.makeText(ProjectEditor.this, "End of Audio", Toast.LENGTH_SHORT).show();
            }
        }
        */

    }

    public void recordFunction(View view)
    {
        if(checkPermissionFromDevice()) {
            Button playButton = findViewById(R.id.play_button);
            Button recordButton = findViewById(R.id.record_button);
            if (recordButton.getText().toString().equals("Record")) {

                pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + UUID.randomUUID().toString() + "_audio_record.3gp";
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playButton.setEnabled(false);
                recordButton.setText("Stop");
                recordButton.setEnabled(true);
                Toast.makeText(ProjectEditor.this, "Recording", Toast.LENGTH_SHORT).show();
            }
            else if (recordButton.getText().toString().equals("Stop")) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                }
                playButton.setEnabled(true);
                recordButton.setText("Record");
                recordButton.setEnabled(true);
                Toast.makeText(ProjectEditor.this, "Stopped Recording", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            requestPermission();
        }
    }

    public void popupMenu(View view) {
        popupButton = findViewById(R.id.project_button);
        PopupMenu p = new PopupMenu(ProjectEditor.this, popupButton);
        p.getMenuInflater().inflate(R.menu.project_popup_menu, p .getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //When an Item in the drop down is pressed, we need to know which one is pressed.
                if (item.getTitle().toString().equals("Save")) {
                    saveFunction();
                    return true;
                }
                else if (item.getTitle().toString().equals("Export")) {
                    exportFunction();
                    return true;
                }
                else if (item.getTitle().toString().equals("Rename")) {
                    renameFunction();
                    return true;
                }
                else if (item.getTitle().toString().equals("Delete")) {
                    deleteFunction();
                    return true;
                }
                else {
                    Toast.makeText(ProjectEditor.this,"didn't work", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
        p.show();
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED )
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    public void saveFunction()
    {
        System.out.println("Saving...");
    }

    public void exportFunction()
    {
        System.out.println("Exporting...");
    }

    public void renameFunction()
    {
        System.out.println("Renaming...");
    }

    public void deleteFunction()
    {
        System.out.println("Deleting...");
    }
}