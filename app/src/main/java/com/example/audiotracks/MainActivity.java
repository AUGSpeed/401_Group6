package com.example.audiotracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import org.w3c.dom.Text;

/**
 * This is our main function
 * First it runs onCreate which checks if the user is logged in
 * If the user is logged in then it takes the user to our main menu.
 * If the user is not logged in then they are taken to our sign in page engined through google.
 * @author Ehab Hanhan
 * @author Michael LaRussa
 * @author Koshiro Kawai
 * @author Sahej Hundal
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    Button popupButton;

    public static final int TEXT_REQUEST = 1;
    public static final String EXTRA_MESSAGE =
            "com.example.audiotracks.extra.MESSAGE";
    //FireBase instance variable
    private FirebaseAuth mFirebaseAuth;
    public static final String ANONYMOUS = "anonymous";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    private SharedPreferences mSharedPreferences;
    private GoogleSignInClient mSignInClient;


    /**
     * onCreate checks if the user is signed in and if true, sets the content pane for our application
     * displaying a user interface for our main menu which consists of
     * a button (currently labelled as "button") which upon clicking creates a popup menu
     * with an option to create a new project, as well as displaying a list of the users current projects ( if any exist )
     *
     * if the user isn't signed in then it loads a signin page engined by google
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This is just a test to see if we can create multiple projects here. Spoiler, we can.
        LinearLayout projectView = (LinearLayout)findViewById(R.id.projectView);
        /*Project test = new Project();
        test.setName("This is a test");
        TextView ed = new TextView(this);
        ed.setText(test.name);
        projectView.addView(ed);
        */
      // Initialize Firebase Auth and check if the user is signed in
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            System.out.println("not logged in");
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        else{
            System.out.println("Logged in");
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mSignInClient = GoogleSignIn.getClient(this, gso);
        }
        myRef.child(mFirebaseAuth.getCurrentUser().getUid()).child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * onDataChange searches for current projects on the signed in user's account and displays all of them
             * @param dataSnapshot contains all of the user's projects
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Project project = new Project();
                    project.name = snapshot.getKey();
                    System.out.println(project.name);
                    TextView ed = new TextView(MainActivity.this);
                    ed.setText(project.name);
                    ed.setTextSize(30);
                    ed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, ProjectEditor.class);
                            String message = ed.getText().toString();

                            intent.putExtra(EXTRA_MESSAGE, message);
                            startActivityForResult(intent, TEXT_REQUEST);
                        }
                    });
                    projectView.addView(ed);
                }
            }

            /**
             *  onCancelled error handling
             * @param databaseError error handling
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /**
     * popupMenuExample creates a popup menu with the option to create a new project
     * @param view stores the location of where this function called from
     */
    public void popupMenuExample(View view) {
        popupButton = findViewById(R.id.popup);
        PopupMenu p = new PopupMenu(MainActivity.this, popupButton);
        p.getMenuInflater().inflate(R.menu.main_popup_menu, p .getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            /**
             * onMenuItemClick checks for when the user selects an option from the popup menu
             * @param item the id of the menu option
             * @return boolean will always return true, if false the menu stays open
             */
            public boolean onMenuItemClick(MenuItem item) {
                //When an Item in the drop down is pressed, we need to know which one is pressed.
                if (item.getTitle().toString().equals("New Project")) {
                    newProject();
                    return true;
                }
                else {
                    Toast.makeText(MainActivity.this,"didn't work", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
        p.show();
    }

    /**
     *  newProject is executed when the user presses New Project in the drop down menu
     */
    private void newProject() {
        Toast.makeText(MainActivity.this,"new project", Toast.LENGTH_SHORT).show();
        showAddProjectDialog(MainActivity.this);
    }

    /**
     * showAddProjectDialog prompts the user to create a name for their project
     * @param c context stores the location of where this function called from
     */
    private void showAddProjectDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Name your new Project:")
                //.setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    /**
                     *  onClick tracks when the user clicks the text box to enter a name
                     * @param dialog shows the message "Name your new Project:" inside of the text box for the user to input a name for their new project
                     * @param which stores which button is clicked within the dialog box
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This Variable holds the string the user just entered. We will pass it to the second screen, where the user can choose to save the project or not, putting it into the data structure we choose for storing these projects.
                        String task = String.valueOf(taskEditText.getText());
                        Intent intent = new Intent(MainActivity.this, ProjectEditor.class);
                        String message = taskEditText.getText().toString();
                        myRef.child(mFirebaseAuth.getCurrentUser().getUid())
                                .child("projects").child(message).child("paths").child("1").setValue("");

                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivityForResult(intent, TEXT_REQUEST);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    /**
     * getUserPhotoUrl() loads the user's profile picture from the google database
     */
    @Nullable
    private String getUserPhotoUrl() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            return user.getPhotoUrl().toString();
        }

        return null;
    }

    /**
     * getUserName() returns the user's name
     * @return the  user's name
     */
    private String getUserName() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            return user.getDisplayName();
        }

        return ANONYMOUS;
    }

}