package com.example.audiotracks;

import android.app.ProgressDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import junit.framework.TestCase;

public class ProjectEditorTest extends TestCase {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();


    public void testCheckExists() {

    }

    public void testPlayFunction() {
    }

    public void testPlayAll() {
    }

    public void testRecordFunction() {
    }

    public void testPopupMenu() {
    }

    public void testDeleteFunction() {
    }

    public void testSelectTrack1() {
    }

    public void testSelectTrack2() {
    }

    public void testSelectTrack3() {
    }

    public void testDownloadFunction() {
    }
}