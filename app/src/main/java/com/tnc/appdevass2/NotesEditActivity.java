package com.tnc.appdevass2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tnc.appdevass2.ModelClass.NotesInformation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

public class NotesEditActivity extends AppCompatActivity {
    EditText datePicker, titleditor, descriptionEdit, mistakes;
    ArrayList<NotesInformation> myNotes;
    int currentPosition = 0;
    NotesInformation currentNote;
    ImageView saveicon;
    TextView dateText;
    Vector<String> dictionary;
    String notesFileKey = "NotesFileKey";
    SharedPreferences myPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);
        initailizeVariable();
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                datePicker.setText(sdf.format(myCalendar.getTime()));
            }
        };
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(NotesEditActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Intent fromLastActivty = getIntent();
        myNotes = (ArrayList<NotesInformation>) fromLastActivty.getSerializableExtra("TopicDetails");
        currentNote = new NotesInformation();
        if (fromLastActivty.hasExtra("TopicPositionDetails")) {
            currentPosition = fromLastActivty.getIntExtra("TopicPositionDetails", 0);
            currentNote = myNotes.get(currentPosition);
            fillviews();
        } else {
            datePicker.setVisibility(View.GONE);
            dateText.setVisibility(View.GONE);
        }
        listnerforSave();
        threadForMistakes();


    }

    private void listnerforSave() {
        saveicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleditor.getText().toString().equals("")) {
                    Toast.makeText(NotesEditActivity.this, "Please Enter some title", Toast.LENGTH_LONG).show();
                    return;
                }
                if (descriptionEdit.getText().toString().equals("")) {
                    Toast.makeText(NotesEditActivity.this, "Please Enter some description", Toast.LENGTH_LONG).show();
                    return;
                }
                if (datePicker.getVisibility() == View.VISIBLE && datePicker.getText().toString().equals("")) {
                    Toast.makeText(NotesEditActivity.this, "Please Select some date", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    currentNote.setTitle(titleditor.getText().toString());
                    currentNote.setDescription(descriptionEdit.getText().toString());
                    if (datePicker.getText().toString().equals("")) {
                        String myFormat = "dd/MM/yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        currentNote.setDate(sdf.format(new Date()));
                        myNotes.add(currentNote);
                    } else {
                        currentNote.setDate(datePicker.getText().toString());
                        if (myNotes == null)
                            myNotes = new ArrayList<>();

                    }
                    saveNotesTitleArray();
                    Toast.makeText(NotesEditActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                    NotesEditActivity.this.finish();


                }
            }
        });
    }

    private void saveNotesTitleArray() {
        String json = "";
        if (myNotes != null && myNotes.size() > 0) {
            json = "{notes:[";
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


    private void threadForMistakes() {

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    // get text from input
                    String txtInput = descriptionEdit.getText().toString();
                    String previous = mistakes.getText().toString();
                    String currentWords = "";
                    // break int words
                    StringTokenizer st = new StringTokenizer(txtInput);
                    while (st.hasMoreTokens()) {
                        String word = st.nextToken();
                        boolean wordfound = false;
                        for (int i = 0; i < dictionary.size(); i++) {
                            if (dictionary.elementAt(i).equalsIgnoreCase(word)) {
                                wordfound = true;
                                break;
                            }
                        }
                        if (!wordfound) {
                            currentWords = currentWords + word + "\n";
                        }

                    }
                    final String myword = currentWords;
                    if (!currentWords.equalsIgnoreCase(previous))
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mistakes.setText(myword);
                            }
                        });
                    // compare words with dictionary
                    // words not found added to mistakes
                    // sleep for 5 seconds
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mistakes.setText("");
//
//                        }
//                    });


                }
            }
        });
        t.start();
    }

    private void fillviews() {
        datePicker.setVisibility(View.VISIBLE);
        titleditor.setText(currentNote.getTitle());
        descriptionEdit.setText(currentNote.getDescription());
        datePicker.setText(currentNote.getDate());

    }

    private void initailizeVariable() {
        datePicker = findViewById(R.id.topicDateEdit);
        titleditor = findViewById(R.id.title_topic_edit);
        descriptionEdit = findViewById(R.id.topicDescriptionEdit);
        mistakes = findViewById(R.id.topicMistakesEdit);
        saveicon = findViewById(R.id.save_icon);
        dateText = findViewById(R.id.datetvedit);
        myPreference=getSharedPreferences("Notes",Context.MODE_PRIVATE);
        dictionary = new Vector<>();
        try {
            InputStream istream = getResources().openRawResource(R.raw.words);
            BufferedReader fin = new BufferedReader(new InputStreamReader(istream));
            while (true) {
                String temp = fin.readLine();
                if (temp == null)
                    break;
                dictionary.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
