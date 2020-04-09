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
    private String strDesc, strLoc, strDate, strTime, strLevel, strPlayers, strUser, id_match;
    private Button finish_exit, delete_match;
    private boolean comesFromCreateMatchActivity;

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
        delete_match = findViewById(R.id.delete_info);

        comesFromCreateMatchActivity = false;

        comesFromCreateMatchActivity = getIntent().getBooleanExtra("boolean_activity",false);

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

        if(checkUserCreatedMatch(this)){
            delete_match.setVisibility(View.VISIBLE);
            delete_match.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMatchAndMoveToNextActivity();
                }
            });
        }else{
            delete_match.setVisibility(View.GONE);
        }


        finish_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextMatchesActivity();
            }
        });
    }

    private void deleteMatchAndMoveToNextActivity() {
        SharedPreferences sharedIdList = this.getSharedPreferences(getString(R.string.id_list),Context.MODE_PRIVATE);

        Set<String> id_matches_list = sharedIdList.getStringSet(getIntent().getStringExtra("sport"), null);

        if(id_matches_list.contains(id_match)){
            id_matches_list.remove(id_match);

            SharedPreferences.Editor editor_list_id = sharedIdList.edit();
            editor_list_id.putStringSet(getIntent().getStringExtra("sport"),id_matches_list);
            editor_list_id.commit();
            editor_list_id.apply();

            moveToNextMatchesActivity();
        }
    }

    private boolean checkUserCreatedMatch(Context context) {
        boolean ret = false;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        String default_user = sharedPref.getString(getString(R.string.saved_user),"admin");

        if(default_user.equals(strUser)){
            ret = true;
        }

        return ret;
    }

    private void moveToNextMatchesActivity() {
        if(comesFromCreateMatchActivity) {
            Intent intent = new Intent(MatchInfoActivity.this, NextMatchesActivity.class);
            intent.putExtra("sport", getIntent().getStringExtra("sport"));
            startActivity(intent);
        }else{
            finish();
        }
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

        id_match = getIntent().getStringExtra("id_match");
        String json = sharedPref.getString(id_match, "");
        if(!json.equals("")) {
            JSONArray jsonArray = new JSONArray(json);
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }

            if (jsonArray.length() > 0) {
                strDesc = list.get(0);
                strLoc = list.get(1);
                strDate = list.get(2);
                strTime = list.get(3);
                strLevel = list.get(4);
                strPlayers = list.get(5);
                strUser = list.get(6);
            }
        }
    }
}
