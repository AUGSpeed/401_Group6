package com.example.audiotracks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectEditor extends AppCompatActivity {
    Button popupButton;
//push test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Button mainButton = findViewById(R.id.project_button);
        mainButton.setText(message);
    }

    public void playFunction(View view)
    {
        System.out.println("Playing Audio...");
    }

    public void stopFunction(View view)
    {
        System.out.println("Stopping Audio...");
    }

    public void recordFunction(View view)
    {
        System.out.println("Recording Audio...");
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