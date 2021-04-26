package com.example.audiotracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.sql.Ref;
import java.util.UUID;

public class ProjectEditor extends AppCompatActivity {
    Button popupButton;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String pathSave="";
    final int REQUEST_PERMISSION_CODE = 1236;
    int currentTrack=1;
    String projectTitle="";
    String fileName = "";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    FirebaseStorage storage =  FirebaseStorage.getInstance();
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private ProgressDialog mProgress;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Button mainButton = findViewById(R.id.project_button);
        mainButton.setText(message);
        requestPermission();
        Button track1 = findViewById(R.id.track1);
        track1.setEnabled(false);
        projectTitle=message;
        mProgress = new ProgressDialog(this);
        Context context = this;
        downloadFunction();
    }

    public void checkExists(String pathLoad) {
        File file = new File(pathLoad);
        if (file.exists()){
            System.out.println("This File exists on the drive.");
        }
    }

    public void playFunction(View view)
    {
        Button playButton = findViewById(R.id.play_button);
        Button recordButton = findViewById(R.id.record_button);
        recordButton.setEnabled(false);
        mediaPlayer = new MediaPlayer();


        if (playButton.getText().toString().equals("Play")) {
            recordButton.setEnabled(false);
            String pathLoad = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"
                    + projectTitle + "_audio_record" + currentTrack + ".3gp";
            checkExists(pathLoad);


            try {
                mediaPlayer.setDataSource(pathLoad);
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
            recordButton.setEnabled(true);
            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            playButton.setText("Play");
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
            fileName = projectTitle + "_audio_record" + currentTrack + ".3gp";

            if (recordButton.getText().toString().equals("Record")) {

                pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"
                        + projectTitle + "_audio_record" + currentTrack + ".3gp";
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
                    mediaRecorder = null;
                    saveAudio();

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
    private void saveAudio(){
        Log.d("before storing", "before storing");

        StorageReference storageReference = mStorage
                .child("Audio")
                .child(fileName);
        Uri file = Uri.fromFile(new File(pathSave));
        storageReference.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println("hello");
                                myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                                        .child("projects")
                                        .child(projectTitle)
                                        .child("paths")
                                        .child(String.valueOf(currentTrack))
                                        .setValue(uri.toString());

                            }
                        });

            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("bad upload", "Image upload task was not successful.", e);
                    }
                });


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

    }

    public void deleteFunction()
    {
        myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("projects")
                .child(projectTitle)
                .removeValue();

        Intent intent = new Intent(ProjectEditor.this, MainActivity.class);
        int code = 1;
        startActivityForResult(intent, code);
    }


    public void selectTrack1(View view) {
        Button track1 = findViewById(R.id.track1);
        Button track2 = findViewById(R.id.track2);
        Button track3 = findViewById(R.id.track3);
        track1.setEnabled(false);
        track2.setEnabled(true);
        track3.setEnabled(true);
        currentTrack=1;
    }
    public void selectTrack2(View view) {
        Button track1 = findViewById(R.id.track1);
        Button track2 = findViewById(R.id.track2);
        Button track3 = findViewById(R.id.track3);
        track1.setEnabled(true);
        track2.setEnabled(false);
        track3.setEnabled(true);
        currentTrack=2;
    }
    public void selectTrack3(View view) {
        Button track1 = findViewById(R.id.track1);
        Button track2 = findViewById(R.id.track2);
        Button track3 = findViewById(R.id.track3);
        track1.setEnabled(true);
        track2.setEnabled(true);
        track3.setEnabled(false);
        currentTrack=3;
    }

    public void downloadFunction() {
        Boolean tracksPresent[] = {null, null, null};
        for (int i = 1; i < 4; i++)
        {
            String pathLoad = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Download/"
                    + projectTitle + "_audio_record" + currentTrack + ".3gp";
            System.out.println(pathLoad);
            File file = new File(pathLoad);
            tracksPresent[i-1] = file.exists();
        }



        myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("projects").child(projectTitle).child("paths").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isSuccessful()){
                            Log.e("firebase", "error getting data", task.getException());
                        }
                        else{
                            System.out.println("hello");
                            for(DataSnapshot child : task.getResult().getChildren()){

                                for (int i = 1; i <= 3; i++) {
                                    String trackName = projectTitle + "_audio_record" + i + ".3gp";
                                    String pathName = child.getValue().toString();
                                    if (pathName.contains(trackName) && tracksPresent[i-1]) {
                                        //File exists locally and in database, we don't need to do anything.
                                        System.out.println("Yeah, This is in database and in local storage. Current track testing: " + i);
                                    } else if (pathName.contains(trackName) && !tracksPresent[i-1]) {
                                        //File Exists in the database, but not on the user's storage, we need to download the file.
                                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        Uri uri = Uri.parse(child.getValue().toString());
                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                        request.setTitle(trackName);
                                        request.setDescription("Downloading");
                                        System.out.println(Environment.DIRECTORY_DOWNLOADS);
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                projectTitle + "_audio_record" + i + ".3gp");
                                        downloadManager.enqueue(request);

                                        //Koshiro, put the Downloading stuff here, the file that needs to be downloaded can be found with child.getValue(), and the name it needs to have locally is trackName.
                                        System.out.println("In database, but not storage Current track testing: " + i);
                                    } else if (!pathName.contains(trackName) && !tracksPresent[i-1]) {
                                        //File doesn't exist anywhere, so we don't need to do anythji
                                        System.out.println("Doesn't exist anywhere. Current track testing: " + i);
                                    }
                                }


                            }
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        }
                    }
                });
    }
}