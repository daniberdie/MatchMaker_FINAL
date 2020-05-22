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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MenuActivity extends Activity {

    private Button createMatchBtn, nextMatchsBtn, statisticsBtn, mapBtn, backBtn;
    private int played_games;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFireauth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mFireauth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

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

        updateFinishedMatches();

        played_games_total += played_games;

        editor_list_id.putInt("played_matches",played_games_total);
        editor_list_id.commit();
        editor_list_id.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateFinishedMatches(){

        mFirestore.collection("app_data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : documentList) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date myDate = null;
                    try {
                        myDate = simpleDateFormat.parse(documentSnapshot.getString("date"));
                        Date today = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(myDate);
                        c.add(Calendar.DATE,1);
                        myDate = c.getTime();
                        int dia = today.compareTo(myDate);
                        if (dia > 0) {
                            String [] participants = documentSnapshot.getString("participants").split(",");
                            String id_match = documentSnapshot.getId();
                            updateStatisticsPlayedGameAndDelete(participants,documentSnapshot.getString("sport"),id_match);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateStatisticsPlayedGameAndDelete(String[] participants, String sport, String match) {
        for(int i=0; i <= participants.length; i++){
            if(i<participants.length && !participants[i].equals("")){
                updatePlayedMatchesForUsers(participants[i],sport);
                updateTotalPlayedGamesForUsers(participants[i]);
                deleteFromNextMatchesList(participants[i],match);
            }
            else {
                String email = mFireauth.getCurrentUser().getEmail();
                updatePlayedMatchesForUsers(email,sport);
                updateTotalPlayedGamesForUsers(email);
                deleteFromNextMatchesList(email,match);
            }
        }

        mFirestore.document("app_data/" + match).delete();
    }

    private void updateTotalPlayedGamesForUsers(final String email) {
        mFirestore.collection("statistics_total_played").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String total_played = documentSnapshot.getString("total");
                if (total_played == null) {
                    total_played = String.valueOf(1);
                    Map<String, String> active = new HashMap<>();
                    active.put("total", total_played);
                    mFirestore.collection("statistics_total_played").document(email).set(active);
                } else {
                    int played_number = Integer.valueOf(total_played) + 1;
                    mFirestore.collection("statistics_total_played").document(email).update("total", String.valueOf(played_number));
                }
            }
        });
    }

    private void updatePlayedMatchesForUsers(final String email, final String sport) {
        mFirestore.collection("statistics_" + sport).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String played_games = documentSnapshot.getString("played");
                String created_games = documentSnapshot.getString("created");

                if (played_games == null) {
                    played_games = String.valueOf(1);
                    Map<String, String> active = new HashMap<>();
                    active.put("created", created_games);
                    active.put("played", played_games);

                    mFirestore.collection("statistics_" + sport).document(email).set(active);
                } else {
                    int played_number = Integer.valueOf(played_games) + 1;
                    mFirestore.collection("statistics_" + sport).document(email).update("played", String.valueOf(played_number));
                }
            }
        });
    }

    private void deleteFromNextMatchesList(final String user_email, final String match) {
        mFirestore.collection("users_matches").document(user_email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> matches_list = new ArrayList(Arrays.asList(documentSnapshot.getString("matches").split(",")));
                if (matches_list.contains(match)) {
                    if(matches_list.size() > 1){
                        matches_list.remove(match);
                    }else{
                        matches_list.set(matches_list.indexOf(match),"");
                    }
                    mFirestore.collection("users_matches").document(user_email).update("matches", android.text.TextUtils.join(",", matches_list));
                }
            }
        });
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
