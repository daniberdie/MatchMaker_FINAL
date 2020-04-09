package com.example.matchmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuActivity extends Activity {

    private Button createMatchBtn, nextMatchsBtn, statisticsBtn, mapBtn, backBtn;
    private int played_games;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        createMatchBtn = findViewById(R.id.buttonCreateMatch);
        nextMatchsBtn  = findViewById(R.id.buttonNextMatch);
        statisticsBtn  = findViewById(R.id.buttonStatistics);
        mapBtn         = findViewById(R.id.buttonMap);

        backBtn        = findViewById(R.id.back_button);

        if(getIntent().getStringExtra("sport").equals("fut"))
        {
            setFutTheme();
        }
        else if(getIntent().getStringExtra("sport").equals("bas"))
        {
            setBasTheme();
        }
        else if(getIntent().getStringExtra("sport").equals("pad"))
        {
            setPadTheme();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, InitialActivity.class);
                startActivity(intent);
            }
        });

        createMatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CreateMatchActivity.class);
                intent.putExtra("sport", getIntent().getStringExtra("sport"));
                startActivity(intent);
            }
        });

        nextMatchsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, NextMatchesActivity.class);
                intent.putExtra("sport", getIntent().getStringExtra("sport"));
                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,MapsActivity.class);
                intent.putExtra("sport", getIntent().getStringExtra("sport"));
                startActivity(intent);
            }
        });

        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: afegir activitat estadístiques
                Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                intent.putExtra("sport", getIntent().getStringExtra("sport"));
                startActivity(intent);
            }
        });

        createStatisticsPreferences(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createStatisticsPreferences(Context context) {
        SharedPreferences statisticsPreferences = context.getSharedPreferences(getIntent().getStringExtra("sport"), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_list_id = statisticsPreferences.edit();

        int played_games_total = statisticsPreferences.getInt("played_matches", 0);

        try {
            updateFinishedMatches(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        played_games_total += played_games;

        editor_list_id.putInt("played_matches",played_games_total);
        editor_list_id.commit();
        editor_list_id.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateFinishedMatches(Context context) throws JSONException {

        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.match_shared_data),Context.MODE_PRIVATE);

        SharedPreferences sharedIdList = context.getSharedPreferences(getString(R.string.id_list), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_sharedIdList = sharedIdList.edit();

        Set<String> id_matches = sharedIdList.getStringSet(getIntent().getStringExtra("sport"), Collections.<String>emptySet());

        String [] idArrayList = id_matches.toArray(new String [id_matches.size()]);

        for(int i = 0; i < idArrayList.length; i++){
            String json = sharedPref.getString(idArrayList[i], "");
            if(!json.equals("")) {
                JSONArray jsonArray = new JSONArray(json);
                List<String> list = new ArrayList<String>();
                for (int x = 0; x < jsonArray.length(); x++) {
                    list.add(jsonArray.getString(x));
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    Date myDate = simpleDateFormat.parse(list.get(2));
                    Date today = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(myDate);
                    c.add(Calendar.DATE,1);
                    myDate = c.getTime();
                    int dia = today.compareTo(myDate);
                    if (dia > 0) {
                        played_games++;
                        id_matches.remove(idArrayList[i]);

                        editor_sharedIdList.putStringSet(getIntent().getStringExtra("sport"),id_matches);
                        editor_sharedIdList.commit();
                        editor_sharedIdList.apply();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setFutTheme()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_menu);

        layout.setBackground(getResources().getDrawable(R.drawable.football));

        createMatchBtn.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        createMatchBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        nextMatchsBtn.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        nextMatchsBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        statisticsBtn.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        statisticsBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mapBtn.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        mapBtn.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void setBasTheme()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_menu);

        layout.setBackground(getResources().getDrawable(R.drawable.basketball));

        createMatchBtn.setBackgroundColor(getResources().getColor(R.color.colorBasket));
        nextMatchsBtn.setBackgroundColor(getResources().getColor(R.color.colorBasket));
        statisticsBtn.setBackgroundColor(getResources().getColor(R.color.colorBasket));
        mapBtn.setBackgroundColor(getResources().getColor(R.color.colorBasket));
    }

    private void setPadTheme()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_menu);

        layout.setBackground(getResources().getDrawable(R.drawable.padel_initial));

        createMatchBtn.setBackgroundColor(getResources().getColor(R.color.colorPadel));
        nextMatchsBtn.setBackgroundColor(getResources().getColor(R.color.colorPadel));
        statisticsBtn.setBackgroundColor(getResources().getColor(R.color.colorPadel));
        mapBtn.setBackgroundColor(getResources().getColor(R.color.colorPadel));
    }
}
