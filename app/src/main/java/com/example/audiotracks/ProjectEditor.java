
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

/**
 *  The Project Editor function contains our audio manipulation tools
 *  it creates buttons for user input that control the audio
 *  to start recording, stop recording, play a single track or play all tracks
 * @author Ehab Hanhan
 * @author Michael LaRussa
 * @author Koshiro Kawai
 * @author Sahej Hundal
 * @version 1.0
 */
public class ProjectEditor extends AppCompatActivity {
    Button popupButton;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer1;
    MediaPlayer mediaPlayer2;
    MediaPlayer mediaPlayer3;
    String pathSave="";
    final int REQUEST_PERMISSION_CODE = 1236;
    int currentTrack=1;
    Project currentProject = new Project();
    String fileName = "";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    FirebaseStorage storage =  FirebaseStorage.getInstance();
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private ProgressDialog mProgress;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    /**
     * onCreate sets the text main button to the project title, then it requests permission to access the user's storage and microphone, finally calls the download function
     * @param savedInstanceState saves a state of the page
     */
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
        currentProject.setName(message);
        mProgress = new ProgressDialog(this);
        Context context = this;
        downloadFunction();
    }

    /**
     *  checkExists returns true if the file exists on the drive false if the file wasn't found
     * @param pathLoad the location of the project
     * @return whether or not the file exists on the drive
     */
    public boolean checkExists(String pathLoad) {
        if (pathLoad != null) {
            File file = new File(pathLoad);
            if (file.exists()) {
                System.out.println("This File exists on the drive.");
                return true;
            }
        }
        return false;
    }

    /**
     *  playFunction plays the selected audio track recorded by the user
     * @param view stores the location of where this function called from
     */
    public void playFunction(View view)
    {
        Button playButton = findViewById(R.id.play_button);
        Button recordButton = findViewById(R.id.record_button);
        recordButton.setEnabled(false);
        if(mediaPlayer==null) {
            mediaPlayer = new MediaPlayer();
        }
        if (playButton.getText().toString().equals("Play")) {
            recordButton.setEnabled(false);
            String pathLoad = currentProject.getPath(currentTrack - 1);
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
                mediaPlayer=null;
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

    /**
     *  playAll plays all tracks recorded by the user on the current project
     * @param view stores the location of where this function called from
     */
    public void playAll(View view) {
        Button playButton = findViewById(R.id.play_button);
        Button playAllButton = findViewById(R.id.playAllButton);
        Button recordButton = findViewById(R.id.record_button);
        Boolean atleastOne=false;
        recordButton.setEnabled(false);
        playButton.setEnabled(false);
        if(mediaPlayer1 == null) {
            mediaPlayer1 = new MediaPlayer();
        }
        if(mediaPlayer2 == null) {
            mediaPlayer2 = new MediaPlayer();
        }
        if(mediaPlayer3 == null) {
            mediaPlayer3 = new MediaPlayer();
        }
        if (playAllButton.getText().toString().equals("Play All")) {
            recordButton.setEnabled(false);
            String pathLoad = currentProject.paths[0];
            if(checkExists(pathLoad)) {
                try {
                    mediaPlayer1.setDataSource(pathLoad);
                    mediaPlayer1.prepare();
                } catch(IOException e){
                    e.printStackTrace();
                }
                atleastOne=true;
            }
            pathLoad = currentProject.paths[1];
            if(checkExists(pathLoad)) {
                try {
                    mediaPlayer2.setDataSource(pathLoad);
                    mediaPlayer2.prepare();
                } catch(IOException e){
                    e.printStackTrace();
                }
                atleastOne=true;
            }
            pathLoad = currentProject.paths[2];
            if(checkExists(pathLoad)) {
                try {
                    mediaPlayer3.setDataSource(pathLoad);
                    mediaPlayer3.prepare();
                } catch(IOException e){
                    e.printStackTrace();
                }
                atleastOne=true;
            }
            if(atleastOne) {
                mediaPlayer1.start();
                mediaPlayer2.start();
                mediaPlayer3.start();
                playAllButton.setText("Stop");
                Toast.makeText(ProjectEditor.this, "Playing", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ProjectEditor.this, "Nothing to play!", Toast.LENGTH_SHORT).show();
            }
        }
        else if (playAllButton.getText().toString().equals("Stop"))
        {
            recordButton.setEnabled(true);
            playButton.setEnabled(true);
            if(mediaPlayer1!=null){
                mediaPlayer1.stop();
                mediaPlayer1.release();
                mediaPlayer1=null;
            }
            if(mediaPlayer2!=null){
                mediaPlayer2.stop();
                mediaPlayer2.release();
                mediaPlayer2=null;
            }
            if(mediaPlayer3!=null){
                mediaPlayer3.stop();
                mediaPlayer3.release();
                mediaPlayer3=null;
            }
            playAllButton.setText("Play All");
        }
    }

    /**
     * recordFunction records on the selected track of the current project
     * @param view stores the location of where this function called from
     */
    public void recordFunction(View view)
    {
        if(checkPermissionFromDevice()) {
            Button playButton = findViewById(R.id.play_button);
            Button recordButton = findViewById(R.id.record_button);
            fileName = currentProject.getName() + "_audio_record" + currentTrack + ".3gp";

            if (recordButton.getText().toString().equals("Record")) {

                currentProject.addPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"
                        + currentProject.getName() + "_audio_record" + currentTrack + ".3gp", currentTrack - 1);
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

    /**
     * popupMenu creates the popup menu with options to save, export, rename, or delete a project
     * @param view stores the location of where this function called from
     */
    public void popupMenu(View view) {
        popupButton = findViewById(R.id.project_button);
        PopupMenu p = new PopupMenu(ProjectEditor.this, popupButton);
        p.getMenuInflater().inflate(R.menu.project_popup_menu, p .getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //When an Item in the drop down is pressed, we need to know which one is pressed.
                if (item.getTitle().toString().equals("Delete")) {
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

    /**
     * setupMediaRecorder initializes the microphone on the user's device
     */
    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(currentProject.getPath(currentTrack - 1));
    }

    /**
     * requestPermission requests the user's permission to access their storage device as well as microphone
     */
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }


    /**
     * onRequestPermissionsResult notifies the user if permission has been granted or not
     */
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

    /**
     * checkPermissionFromDevice checks if the user had previously consented to microphone access
     * @return true if user has granted access
     */
    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * saveAudio saves the current track
     */
    private void saveAudio(){
        Log.d("before storing", "before storing");

        StorageReference storageReference = mStorage
                .child("Audio")
                .child(fileName);
        Uri file = Uri.fromFile(new File(currentProject.getPath(currentTrack - 1)));
        storageReference.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            /**'
             * onSuccess gets the download url for a file uploaded and then passes it to the next onSuccess function
             * @param taskSnapshot request to the database that contains all the specific information about the file that was just uploaded
             */
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            /**
                             *  onSuccess takes the url and puts it into the realtime database
                             * @param uri url reference of the file that was just uploaded
                             */
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println("hello");
                                myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                                        .child("projects")
                                        .child(currentProject.getName())
                                        .child("paths")
                                        .child(String.valueOf(currentTrack))
                                        .setValue(uri.toString());

                            }
                        });

            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    /**
                     *  onFaliure throws an exeption
                     * @param e exception
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("bad upload", "Image upload task was not successful.", e);
                    }
                });


    }

    /**
     *  deleteFunction deletes all references to the current project
     */
    public void deleteFunction()
    {
        myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("projects")
                .child(currentProject.getName())
                .removeValue();

        Intent intent = new Intent(ProjectEditor.this, MainActivity.class);
        int code = 1;
        startActivityForResult(intent, code);
    }


    /**
     *  selectTrack1 selects the first track
     * @param view stores the location of where this function called from
     */
    public void selectTrack1(View view) {
        Button track1 = findViewById(R.id.track1);
        Button track2 = findViewById(R.id.track2);
        Button track3 = findViewById(R.id.track3);
        track1.setEnabled(false);
        track2.setEnabled(true);
        track3.setEnabled(true);
        currentTrack=1;
    }

    /**
     * selectTrack 2 selects the second track
     * @param view stores the location of where this function called from
     */
    public void selectTrack2(View view) {
        Button track1 = findViewById(R.id.track1);
        Button track2 = findViewById(R.id.track2);
        Button track3 = findViewById(R.id.track3);
        track1.setEnabled(true);
        track2.setEnabled(false);
        track3.setEnabled(true);
        currentTrack=2;
    }

    /**
     * select 3 selects the third track
     * @param view stores the location of where this function called from
     */
    public void selectTrack3(View view) {
        Button track1 = findViewById(R.id.track1);
        Button track2 = findViewById(R.id.track2);
        Button track3 = findViewById(R.id.track3);
        track1.setEnabled(true);
        track2.setEnabled(true);
        track3.setEnabled(false);
        currentTrack=3;
    }

    /**
     * downloadFunction determines wheter files need to be downloaded from the database, and if so downloads the projects from the database onto local storage
     */
    public void downloadFunction() {
        Boolean tracksPresent[] = {null, null, null};
        for (int i = 1; i < 4; i++)
        {
            String pathLoad = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"
                    + currentProject.getName() + "_audio_record" + currentTrack + ".3gp";
            System.out.println(pathLoad);
            File file = new File(pathLoad);
            if (file.exists())
            {
                currentProject.addPath(pathLoad, i-1);
            }
            tracksPresent[i-1] = file.exists();
        }

        myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("projects").child(currentProject.getName()).child("paths").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    /**
                     * onComplete compares the tracks on the device and on the database to see which projects need to be downloaded
                     * @param task paths to the audio files contained in the current project
                     */
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isSuccessful()){
                            Log.e("firebase", "error getting data", task.getException());
                        }
                        else{
                            System.out.println("hello");
                            for(DataSnapshot child : task.getResult().getChildren()){

                                for (int i = 1; i <= 3; i++) {
                                    String trackName = currentProject.getName() + "_audio_record" + i + ".3gp";
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
                                                currentProject.getName() + "_audio_record" + i + ".3gp");
                                        downloadManager.enqueue(request);
                                        currentProject.addPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"
                                                + currentProject.getName() + "_audio_record" + i + ".3gp", i - 1);
                                        System.out.println(currentProject.paths[i-1]);

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