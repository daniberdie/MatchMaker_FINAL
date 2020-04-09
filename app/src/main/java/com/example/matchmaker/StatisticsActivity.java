package com.example.matchmaker;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Set;

public class StatisticsActivity extends Activity {
    private TextView totalGames, createdGames, activeGames, sportUssage,
            result1, result2, result3, result4;
    private LinearLayout layTotal, layCreated, layActive, layUssage, layoutMenu;
    private Button backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        layTotal = findViewById(R.id.layResult1);
        layActive = findViewById(R.id.layResult2);
        layCreated = findViewById(R.id.layResult3);
        layUssage = findViewById(R.id.layResult4);
        layoutMenu = findViewById(R.id.activity_menu);
        totalGames = findViewById(R.id.totalGames);
        createdGames = findViewById(R.id.createdGames);
        activeGames = findViewById(R.id.activeGames);
        sportUssage = findViewById(R.id.sportGames);
        result1 = findViewById(R.id.result1);
        result2 = findViewById(R.id.result2);
        result3 = findViewById(R.id.result3);
        result4 = findViewById(R.id.result4);
        if (getIntent().getStringExtra("sport").equals("fut")) {
            setFutTheme();
        } else if (getIntent().getStringExtra("sport").equals("bas")) {
            setBasTheme();
        } else if (getIntent().getStringExtra("sport").equals("pad")) {
            setPadTheme();
        }
        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getStatistics(this);
    }

    private void getStatistics(Context context) {
        //Active games
        SharedPreferences sharedIdList = context.getSharedPreferences(getString(R.string.id_list), Context.MODE_PRIVATE);
        Set<String> id_matches = sharedIdList.getStringSet(getIntent().getStringExtra("sport"), Collections.<String>emptySet());

        result2.setText(Integer.toString(id_matches.size()));

        //Played & Created
        SharedPreferences preferencesStats = context.getSharedPreferences(getIntent().getStringExtra("sport"), Context.MODE_PRIVATE);

        int created_games = preferencesStats.getInt("created_matches", 0);
        int played_games = preferencesStats.getInt("played_matches", 0);

        result1.setText(Integer.toString(played_games));
        result3.setText(Integer.toString(created_games));

        SharedPreferences preferencesStatsPadel = context.getSharedPreferences("pad", Context.MODE_PRIVATE);
        SharedPreferences preferencesStatsFut = context.getSharedPreferences("fut", Context.MODE_PRIVATE);
        SharedPreferences preferencesStatsBas = context.getSharedPreferences("bas", Context.MODE_PRIVATE);

        int played_gamesPadel = preferencesStatsPadel.getInt("played_matches", 0);
        int played_gamesFut = preferencesStatsFut.getInt("played_matches", 0);
        int played_gamesBas = preferencesStatsBas.getInt("played_matches", 0);

        int total_played = played_gamesBas + played_gamesFut + played_gamesPadel;
        int total_sport = (total_played != 0) ? (played_games * 100) / total_played : 0;

        result4.setText(Integer.toString(total_sport) + " %");

    }

    private void setPadTheme() {
        layoutMenu.setBackground(getResources().getDrawable(R.drawable.padel_initial));
        layTotal.setBackgroundColor(getResources().getColor(R.color.colorPadel));
        layCreated.setBackgroundColor(getResources().getColor(R.color.colorPadel));
        layActive.setBackgroundColor(getResources().getColor(R.color.colorPadel));
        layUssage.setBackgroundColor(getResources().getColor(R.color.colorPadel));
    }

    private void setBasTheme() {
        layoutMenu.setBackground(getResources().getDrawable(R.drawable.basketball));
        layTotal.setBackgroundColor(getResources().getColor(R.color.colorBasket));
        layCreated.setBackgroundColor(getResources().getColor(R.color.colorBasket));
        layActive.setBackgroundColor(getResources().getColor(R.color.colorBasket));
        layUssage.setBackgroundColor(getResources().getColor(R.color.colorBasket));
    }

    private void setFutTheme() {
        layoutMenu.setBackground(getResources().getDrawable(R.drawable.football));
        layTotal.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        layCreated.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        layActive.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        layUssage.setBackgroundColor(getResources().getColor(R.color.colorFutbol));
        totalGames.setTextColor(getResources().getColor(R.color.colorWhite));
        createdGames.setTextColor(getResources().getColor(R.color.colorWhite));
        activeGames.setTextColor(getResources().getColor(R.color.colorWhite));
        sportUssage.setTextColor(getResources().getColor(R.color.colorWhite));
        result1.setTextColor(getResources().getColor(R.color.colorWhite));
        result2.setTextColor(getResources().getColor(R.color.colorWhite));
        result3.setTextColor(getResources().getColor(R.color.colorWhite));
        result4.setTextColor(getResources().getColor(R.color.colorWhite));
    }
}