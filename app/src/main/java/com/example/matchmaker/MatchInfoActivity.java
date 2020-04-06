package com.example.matchmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MatchInfoActivity extends AppCompatActivity {

    private TextView description, location, date, time, level, players;
    private String strDesc, strLoc, strDate, strTime, strLevel, strPlayers;
    private Button finish_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_info);

        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_infoMatch);

        description = findViewById(R.id.saved_description);
        location = findViewById(R.id.saved_ubi);
        date = findViewById(R.id.saved_date);
        time = findViewById(R.id.saved_time);
        level = findViewById(R.id.saved_level);
        players = findViewById(R.id.saved_players);
        finish_exit = findViewById(R.id.exit_info);

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

        try {
            setTextInfoActivity();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finish_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextMatchesActivity();
            }
        });
    }

    private void moveToNextMatchesActivity() {
        Intent intent = new Intent(MatchInfoActivity.this,NextMatchesActivity.class);
        intent.putExtra("sport", getIntent().getStringExtra("sport"));
        startActivity(intent);
    }

    private void setTextInfoActivity() throws JSONException {
        getDataInfo(this);
        description.setText(strDesc);
        location.setText(strLoc);
        date.setText(strDate);
        time.setText(strTime);
        level.setText(strLevel);
        players.setText(strPlayers);

    }

    private void getDataInfo(Context context) throws JSONException {
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.match_shared_data), Context.MODE_PRIVATE);

        String id_match = getIntent().getStringExtra("id_match");
        String json = sharedPref.getString(id_match, null);
        JSONArray jsonArray = new JSONArray(json);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }


            //TODO: Revisar ordre he fet un apanyo temporal XD
            strDesc = list.get(0);
            strLoc = list.get(1);
            strDate = list.get(2);
            strTime = list.get(3);
            strLevel = list.get(4);
            strPlayers = list.get(5);
    }
}
