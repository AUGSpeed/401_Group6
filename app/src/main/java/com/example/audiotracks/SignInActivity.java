package com.example.audiotracks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.audiotracks.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * SignInActivity connects to the google database to have the user sign in through their google account
 * @author Ehab Hanhan
 * @author Michael LaRussa
 * @author Koshiro Kawai
 * @author Sahej Hundal
 * @version 1.0
 */
public class SignInActivity extends AppCompatActivity{
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private ActivitySignInBinding mBinding;
    private GoogleSignInClient mSignInClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;


    /**
     * onCreate creates the button to sign in
     * @param savedInstanceState saves a state of the page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This codelab uses View Binding
        // See: https://developer.android.com/topic/libraries/view-binding
        //mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Set click listeners
        mBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             *  onClick prompts the user to sign in to their google account
             */
            public void onClick(View view) {
                signIn();
            }
        });
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    /**
     *  onActivityResult allows the user access to the application if user was able to sign in
     * @param requestCode tells the server which account we are signing in with
     * @param resultCode unused
     * @param data the account with which we are signing in with
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent in signIn()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * firebaseAuthWithGoogle accesses the firebase database using a google account
     * @param acct the google account used to sign in
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    /***
                     * If sign in succeeds the auth state listener will be notified and logic to handle the signed in user can be handled in the listener.
                     * @param authResult the result of the authentication (success/unsuccessful)
                     */
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Log.d(TAG, "signInWithCredential:success");
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    /**
                     * onFaliure If sign in fails, display a message to the user.
                     * @param e
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "signInWithCredential", e);
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * signIn signs the user in
     */
    private void signIn() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




}
