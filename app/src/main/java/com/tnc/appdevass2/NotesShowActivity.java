package com.tnc.appdevass2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tnc.appdevass2.ModelClass.NotesInformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotesShowActivity extends AppCompatActivity {
    ArrayList<NotesInformation> myNotes;
    TextView notesTitle, description, date;
    ImageView deleteIcon, editIcon;
    NotesInformation currentNote;
    int position;
    SharedPreferences myPreference;
    String notesFileKey = "NotesFileKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_show);
        initializeViews();
        Intent getLastActivtyIntent = getIntent();
        if (getLastActivtyIntent.hasExtra("NotesDetails")) {
            position = getLastActivtyIntent.getIntExtra("NotesPostionDetails", 0);
        } else {
            Toast.makeText(NotesShowActivity.this, "Please try again!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void getLatestValueForNotesArray() {
        String json = myPreference.getString(notesFileKey, "");
        myNotes = new ArrayList<>();
        try {
            if (!json.equalsIgnoreCase("")) {
                JSONObject fullResult = new JSONObject(json);
                JSONArray jsonArrayArtical = fullResult.getJSONArray("notes");
                for (int x = 0; x < jsonArrayArtical.length(); x++) {
                    JSONObject notesJson = jsonArrayArtical.getJSONObject(x);
                    NotesInformation note = new NotesInformation();
                    note.setTitle(notesJson.getString("topic"));
                    note.setDescription(notesJson.getString("description"));
                    note.setDate(notesJson.getString("date"));
                    myNotes.add(note);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getLatestValueForNotesArray();
        putValuesInview();
    }

    private void saveNotesTitleArray() {
        String json = "";
        if (myNotes != null && myNotes.size() > 0) {
            json = "notes:{[";
            int x = 0;
            for (NotesInformation note : myNotes) {
                json = json + "{" + "topic:" + "\"" + note.getTitle() + "\",description:" + "\""
                        + note.getDescription() + "\",date:" + "\"" + note.getDate() + "\"}";
                if (x != myNotes.size() - 1)
                    json = json + ",";
                x++;
            }
            json = json + "]}";
        }
        SharedPreferences.Editor edit = myPreference.edit();
        edit.putString(notesFileKey, json);
        edit.commit();
    }


    private void putValuesInview() {
        currentNote = myNotes.get(position);
        notesTitle.setText(currentNote.getTitle());
        date.setText(currentNote.getDate());
        description.setText(currentNote.getDescription());
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(NotesShowActivity.this);
                builder.setTitle("Are You Sure?");

                builder.setPositiveButton("Yes", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myNotes.remove(position);
                                saveNotesTitleArray();
                                NotesShowActivity.this.finish();

                            }
                        });
                builder.setNegativeButton("No", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ;//
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditActivty = new Intent(NotesShowActivity.this, NotesEditActivity.class);
                toEditActivty.putExtra("TopicDetails", myNotes);
                toEditActivty.putExtra("TopicPositionDetails", position);
                startActivity(toEditActivty);
            }
        });
    }

    private void initializeViews() {
        notesTitle = findViewById(R.id.title_topic_show);
        description = findViewById(R.id.topicDescriptionShow);
        date = findViewById(R.id.topicDateShow);
        deleteIcon = findViewById(R.id.delete_icon);
        editIcon = findViewById(R.id.edit_icon);
        myPreference = NotesShowActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);

    }


    private void stringForJson() {
        String json = "";
        if (myNotes != null && myNotes.size() > 0) {
            json = "notes:[";
            int x = 0;
            for (NotesInformation note : myNotes) {
                json = json + "{" + "Topic:" + "\"" + note.getTitle() + "\",Description:" + "\""
                        + note.getDescription() + "\",Date:" + "\"" + note.getDate() + "\"}";
                if (x != myNotes.size() - 1)
                    json = json + ",";
                x++;
            }
            json = json + "]";

        }

    }

}
