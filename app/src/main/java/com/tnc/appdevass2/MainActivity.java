package com.tnc.appdevass2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tnc.appdevass2.ModelClass.NotesInformation;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences myPreference;
    ArrayList<NotesInformation> myNotes;
    String notesFileKey = "NotesFileKey";
    ListView listTopics;
    ArrayList<String> notesTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myPreference = MainActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);
        myNotes = new ArrayList<>();
        notesTopic = new ArrayList<>();
        listTopics = findViewById(R.id.listViewForNotes);
        getNotesTitleArray();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditActivty = new Intent(MainActivity.this, NotesEditActivity.class);
                toEditActivty.putExtra("TopicDetails", myNotes);
                startActivity(toEditActivty);
            }
        });
    }

    @Override
    protected void onResume() {
        notesTopic = new ArrayList<>();
        super.onResume();
        getNotesTitleArray();
        if (myNotes != null) {
            getNotesTopicFromArray();
            ShowTopics();
        }
    }

    private void getNotesTopicFromArray() {
        for (NotesInformation note : myNotes) {
            notesTopic.add(note.getTitle());
        }
    }

    private void ShowTopics() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.notes_title_list_view, R.id.topicListView, notesTopic);
        listTopics.setAdapter(adapter);
        listTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                Intent toShowAcitivty = new Intent(MainActivity.this, NotesShowActivity.class);
                toShowAcitivty.putExtra("NotesDetails", myNotes);
                toShowAcitivty.putExtra("NotesPostionDetails", position);
                startActivity(toShowAcitivty);
            }
        });

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


    private void getNotesTitleArray() {
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


}
