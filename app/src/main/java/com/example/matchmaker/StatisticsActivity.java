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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatisticsActivity extends Activity {
    private TextView totalGames, createdGames, activeGames, sportUssage,
            result1, result2, result3, result4;
    private LinearLayout layTotal, layCreated, layActive, layUssage, layoutMenu;
    private Button backBtn;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFireauth;
    private String played, created, active;
    private int games_actived=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        mFireauth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

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

        getStatistics();
    }

    private void getStatistics() {
        if(Globals.mapStatistics.containsKey(getIntent().getStringExtra("sport"))){
            Integer [] stats = Globals.mapStatistics.get(getIntent().getStringExtra("sport"));
            int fut_plays = 0, bas_plays = 0, pad_plays = 0;

            if(Globals.mapStatistics.containsKey("fut")){
                Integer [] stats_fut = Globals.mapStatistics.get("fut");
                fut_plays = stats_fut[0];
            }
            if(Globals.mapStatistics.containsKey("bas")){
                Integer [] stats_bas = Globals.mapStatistics.get("bas");
                bas_plays = stats_bas[0];
            }
            if(Globals.mapStatistics.containsKey("pad")){
                Integer [] stats_pad = Globals.mapStatistics.get("pad");
                pad_plays = stats_pad[0];
            }

            int total_sports_played = fut_plays + bas_plays + pad_plays;

            int total_sport = (total_sports_played != 0) ? (Integer.parseInt(played) * 100) / total_sports_played : 0;

            result1.setText(Integer.toString(stats[0]));
            result2.setText(Integer.toString(stats[1]));
            result3.setText(Integer.toString(stats[2]));
            result4.setText(Integer.toString(total_sport) + " %");
        } else {
            result1.setText("0");
            result2.setText("0");
            result3.setText("0");
            result4.setText("0" + " %");
        }


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