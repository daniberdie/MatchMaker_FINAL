package com.example.matchmaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.Clock;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CreateMatchActivity extends AppCompatActivity {

    private EditText description_editText, location_editText, date_editText, time_editText, players_editText;
    private Spinner level_spinner;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Button buttonBack,buttonCreate;
    private int num_players, id_match;
    private String description, location, date, time, level,players, user, position_map;
    private final int PLACE_PICKER_REQUEST = 1;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFireAuth;
    public int id_new_match = 0;
    private boolean isFinished = false;
    public Context context = this;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        mFirestore = FirebaseFirestore.getInstance();
        mFireAuth = FirebaseAuth.getInstance();

        //Millora input de dades
        description_editText = findViewById(R.id.description_editText);

        location_editText = findViewById(R.id.ubication_editText);
        location_editText.setKeyListener(null);

        date_editText = findViewById(R.id.date_editText);
        date_editText.setKeyListener(null);
        time_editText = findViewById(R.id.time_editText);
        time_editText.setKeyListener(null);
        level_spinner = findViewById(R.id.level_spinner);
        players_editText = findViewById(R.id.players_editText);

        buttonBack = findViewById(R.id.back_button_main_create);
        buttonCreate = findViewById(R.id.create_button);

        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_createMatch);

        if(getIntent().getStringExtra("sport").equals("fut"))
        {
            layout.setBackground(getResources().getDrawable(R.drawable.football));
        }
        else if(getIntent().getStringExtra("sport").equals("bas"))
        {
            layout.setBackground(getResources().getDrawable(R.drawable.basketball));
        }
        else if(getIntent().getStringExtra("sport").equals("pad"))
        {
            layout.setBackground(getResources().getDrawable(R.drawable.padel_initial));
        }

        String [] level_options = {getString(R.string.all_level),getString(R.string.low_level),getString(R.string.mid_level),getString(R.string.high_level)};

        ArrayAdapter <String> adapter = new ArrayAdapter<>(CreateMatchActivity.this, R.layout.spinner_item_level_options, level_options);
        level_spinner.setAdapter(adapter);

        //Mostrar calendari al apretar a sobre del editText de la data
        date_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCalendar();
            }
        });

        //Mostrar rellotge al apretar a sobre del editText de la data
        time_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayClock();
            }
        });

        location_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlacePicker();
            }
        });

        //Back Button
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Create Button
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createNewMatch();
            }
        });


        //Escriure al EditText de la data
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth + "/" + month + "/" + year;
                date_editText.setText(date);
            }
        };

        //Escriure al EditText de la hora
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = convertDate(hourOfDay) + ":" + convertDate(minute);
                time_editText.setText(time);
            }
        };
    }

    private void getPlacePicker() {
        Intent intent = new Intent(CreateMatchActivity.this, MapsActivityPlacePicker.class);
        startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == SECOND_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                String returnAddressString = data.getStringExtra(Intent.EXTRA_TEXT);
                location_editText.setText(returnAddressString);

                position_map = data.getStringExtra("position");
            }
        }
    }

    private void createNewMatch() {
        if(!checkEmptyFields()){
            if(num_players > 40){
                Toast toast = Toast.makeText(this,getString(R.string.max_players), Toast.LENGTH_LONG);
                toast.show();
            } else{
                saveDataFromNewMatch();
            }

        }
        else{
            Toast toast = Toast.makeText(this,getString(R.string.empty_fields), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //TODO: Revisar us de Globals
    private void saveDataFromNewMatch(){

        mFirestore.collection("id_matches").document("id").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                String id_match_firebase = documentSnapshot.getString("id");
                id_new_match = Integer.parseInt(id_match_firebase) + 1;
                mFirestore.collection("id_matches").document("id").update("id",String.valueOf(id_new_match));

                DocumentReference docRef = mFirestore.collection("app_data").document(String.valueOf(id_new_match));
                Map<String, String> mapData = new HashMap<>();
                mapData.put("date", date);
                mapData.put("description", description);
                mapData.put("level", level);
                mapData.put("location", location);
                mapData.put("players", players);
                mapData.put("position_map", position_map);
                mapData.put("sport", getIntent().getStringExtra("sport"));
                mapData.put("time", time);
                mapData.put("user", mFireAuth.getCurrentUser().getEmail());
                mapData.put("participants", "");

                docRef.set(mapData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateMatchActivity.this, "Added to firebase", Toast.LENGTH_SHORT).show();
                        mFirestore.collection("users_matches").document(mFireAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String matches = documentSnapshot.getString("matches");
                                if(matches == null) {
                                    matches = String.valueOf(id_new_match);
                                    Map<String, String> user_matches = new HashMap<>();
                                    user_matches.put("matches", matches);
                                    mFirestore.collection("users_matches").document(mFireAuth.getCurrentUser().getEmail()).set(user_matches);
                                }
                                else{
                                    matches += "," + String.valueOf(id_new_match);
                                    mFirestore.collection("users_matches").document(mFireAuth.getCurrentUser().getEmail()).update("matches",matches);
                                }

                                //Statistics
                                SharedPreferences statisticsPreferences = context.getSharedPreferences(getIntent().getStringExtra("sport"), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor_statPref = statisticsPreferences.edit();

                                int created_games_total = statisticsPreferences.getInt("created_matches", 0) + 1;

                                //Created Matches Statistics
                                /*Integer[] statistics = Globals.mapStatistics.get(getIntent().getStringExtra("sport"));
                                statistics[0] = statistics [0]++;
                                Globals.mapStatistics.put(getIntent().getStringExtra("sport"),statistics);*/

                                editor_statPref.putInt("created_matches",created_games_total);
                                editor_statPref.commit();
                                editor_statPref.apply();

                                Intent intent = new Intent(CreateMatchActivity.this, MatchInfoActivity.class);
                                intent.putExtra("sport", getIntent().getStringExtra("sport"));
                                intent.putExtra("activity","create");
                                intent.putExtra("id_match",Integer.toString(id_new_match));
                                startActivity(intent);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateMatchActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMatchActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private boolean checkEmptyFields() {
        boolean ret = false;
        getFieldsValues();
        if(description == "" || location == "" || date == "" || time == "" || level == "" || num_players <= 0){
            ret = true;
        }

        return ret;
    }

    private void getFieldsValues() {

        description = description_editText.getText().toString();
        location    = location_editText.getText().toString();
        date        = date_editText.getText().toString();
        time        = time_editText.getText().toString();
        level       = level_spinner.getSelectedItem().toString();
        players     = players_editText.getText().toString();

        if(players.equals("")) {
            players = "0";
        }

        num_players = Integer.parseInt(players);

        //Obtenir usuari
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        user = sharedPref.getString(getString(R.string.saved_user),"admin");

    }


    //Afegir 0 davant de els minuts o hores en cas de ser de una xifra.
    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    private void displayClock() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(CreateMatchActivity.this,
                                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                        mTimeSetListener,
                                                        hour+2,minute,true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void displayCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(CreateMatchActivity.this,
                                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                        mDateSetListener,
                                                        year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
