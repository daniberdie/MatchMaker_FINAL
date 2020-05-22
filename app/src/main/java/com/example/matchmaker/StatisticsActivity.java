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

        getStatistics(this);
    }

    private void getStatistics(Context context) {

        mFirestore.collection("statistics_" + getIntent().getStringExtra("sport")).document(mFireauth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                played = documentSnapshot.getString("played");
                if(played == null) played = "0";

                created = documentSnapshot.getString("created");
                if(created == null) created = "0";

                mFirestore.collection("users_matches").document(mFireauth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        checkActiveGamesBySport(documentSnapshot.getString("matches").split(","));
                    }
                });
            }
        });
    }

    private void checkActiveGamesBySport(String[] matches) {
        for(int i = 0; i < matches.length; i++){
            mFirestore.collection("app_data").document(matches[i]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString("sport").equals(getIntent().getStringExtra("sport"))){
                        games_actived++;
                        getTotalPlayedGames();
                    }
                }
            });
        }
    }

    private void getTotalPlayedGames() {
        mFirestore.collection("statistics_total_played").document(mFireauth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int total_sports_played;
                String total = documentSnapshot.getString("total");
                if(total == null) total_sports_played = 0;
                else total_sports_played = Integer.valueOf(total);
                int total_sport = (total_sports_played != 0) ? (Integer.parseInt(played) * 100) / total_sports_played : 0;

                result1.setText(played);
                result2.setText(String.valueOf(games_actived));
                result3.setText(created);
                result4.setText(Integer.toString(total_sport) + " %");
            }
        });
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