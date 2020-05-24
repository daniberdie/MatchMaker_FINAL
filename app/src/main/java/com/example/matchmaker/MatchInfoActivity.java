package com.example.matchmaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchInfoActivity extends AppCompatActivity {

    private TextView description, location, date, time, level, players;
    private String strUser, id_match;
    private Button finish_exit, delete_match;
    private String comesFromActivity;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFireauth;
    private int joined_players, total_players;
    public boolean checkUserCreator = false;
    String [] participants;
    String participants_join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_info);

        mFirestore = FirebaseFirestore.getInstance();
        mFireauth = FirebaseAuth.getInstance();

        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_infoMatch);

        description = findViewById(R.id.saved_description);
        location = findViewById(R.id.saved_ubi);
        date = findViewById(R.id.saved_date);
        time = findViewById(R.id.saved_time);
        level = findViewById(R.id.saved_level);
        players = findViewById(R.id.saved_players);
        finish_exit = findViewById(R.id.exit_info);
        delete_match = findViewById(R.id.delete_info);

        comesFromActivity = getIntent().getStringExtra("activity");
        id_match = getIntent().getStringExtra("id_match");

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

        getDataInfo();

        finish_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comesFromActivity.equals("map")){
                    backToMapsActivity();
                }else{
                    moveToNextMatchesActivity();
                }
            }
        });
    }

    private void backToMapsActivity() {
        Intent intent = new Intent(MatchInfoActivity.this,MapsActivity.class);
        intent.putExtra("sport", getIntent().getStringExtra("sport"));
        startActivity(intent);
    }

    private void deleteMatchAndMoveToNextActivity() {

        for(int i=0; i <= participants.length; i++){
            if(i<participants.length && !participants[i].equals("")) deleteFromNextMatchesList(participants[i],false);
            else deleteFromNextMatchesList(strUser,false);
        }

        mFirestore.document("app_data/" + id_match).delete();
        moveToNextMatchesActivity();

    }

    private boolean checkUserCreatedMatch() {

        boolean ret = false;

        if(strUser.equals(mFireauth.getCurrentUser().getEmail())){
            ret = true;
        }

        return ret;
    }

    private void moveToNextMatchesActivity() {
        Intent intent = new Intent(MatchInfoActivity.this, NextMatchesActivity.class);
        intent.putExtra("sport", getIntent().getStringExtra("sport"));
        startActivity(intent);
    }

    private void getDataInfo(){

        mFirestore.collection("app_data").document(id_match).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                description.setText(documentSnapshot.getString("description"));
                location.setText(documentSnapshot.getString("location"));
                date.setText(documentSnapshot.getString("date"));
                time.setText(documentSnapshot.getString("time"));
                level.setText(documentSnapshot.getString("level"));
                participants = documentSnapshot.getString("participants").split(",");
                if(participants[0].equals("")){
                    joined_players = 1;
                }else{
                    joined_players = participants.length + 1; // Se suma 1 per contar el creador;
                }

                total_players = Integer.parseInt(documentSnapshot.getString("players"));

                players.setText(String.valueOf(joined_players) + "/" + String.valueOf(total_players));
                strUser = documentSnapshot.getString("user");

                if(checkUserCreatedMatch()){
                    delete_match.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            throwToast();
                        }
                    });
                }else if (comesFromActivity.equals("map") && !isJoined()){
                    delete_match.setText(R.string.join);
                    delete_match.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(joined_players >= total_players){
                                Toast.makeText(MatchInfoActivity.this, "Max players", Toast.LENGTH_SHORT).show();
                            }else{
                                joinMatch();
                            }
                        }
                    });
                }else{
                    delete_match.setText(R.string.unjoin);
                    delete_match.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unjoinMatch();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchInfoActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isJoined() {
        List<String> participants_joined = Arrays.asList(participants);
        return participants_joined.contains(mFireauth.getCurrentUser().getEmail());
    }

    private void unjoinMatch() {
        mFirestore.collection("app_data").document(id_match).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final List<String> participants_list = new ArrayList(Arrays.asList(documentSnapshot.getString("participants").split(",")));
                final String email = mFireauth.getCurrentUser().getEmail();

                if(participants_list.contains(email)){
                    if(participants_list.size() > 1){
                        participants_list.remove(email);
                    }else{
                        participants_list.set(participants_list.indexOf(email),"");
                    }

                    if(Globals.mapStatistics.containsKey(getIntent().getStringExtra("sport"))){
                        Integer[] stats = Globals.mapStatistics.get(getIntent().getStringExtra("sport"));

                        stats[1] = stats[1] - 1;


                        Globals.mapStatistics.put(getIntent().getStringExtra("sport"), stats);
                    } else {
                        Integer [] integers = new Integer [3];
                        integers[0] = 0;
                        integers[1] = 0;
                        integers[2] = 0;
                        Globals.mapStatistics.put(getIntent().getStringExtra("sport"),integers);
                    }

                    mFirestore.collection("app_data").document(id_match).update("participants", android.text.TextUtils.join(",", participants_list));
                    Toast.makeText(MatchInfoActivity.this, "Match removed", Toast.LENGTH_SHORT).show();
                    deleteFromNextMatchesList(mFireauth.getCurrentUser().getEmail(),true);
                }
            }
        });
    }

    private void deleteFromNextMatchesList(final String user_email, final boolean moveToNextMatches) {
        mFirestore.collection("users_matches").document(user_email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> matches_list = new ArrayList(Arrays.asList(documentSnapshot.getString("matches").split(",")));
                if (matches_list.contains(id_match)) {
                    if(matches_list.size() > 1){
                        matches_list.remove(id_match);
                    }else{
                        matches_list.set(matches_list.indexOf(id_match),"");
                    }
                    mFirestore.collection("users_matches").document(user_email).update("matches", android.text.TextUtils.join(",", matches_list));

                }

                if(moveToNextMatches){
                    moveToNextMatchesActivity();
                }

            }
        });
    }

    private void joinMatch() {
        mFirestore.collection("app_data").document(id_match).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                participants_join = documentSnapshot.getString("participants");
                if (participants_join == "") {
                    participants_join = mFireauth.getCurrentUser().getEmail();
                } else {
                    participants_join += "," + mFireauth.getCurrentUser().getEmail();
                }

                mFirestore.collection("app_data").document(id_match).update("participants", participants_join);
                Toast.makeText(MatchInfoActivity.this, "Added to NextMatches", Toast.LENGTH_SHORT).show();

                if(Globals.mapStatistics.containsKey(getIntent().getStringExtra("sport"))){
                    Integer[] stats = Globals.mapStatistics.get(getIntent().getStringExtra("sport"));

                    stats[1] = stats[1] + 1;

                    Globals.mapStatistics.put(getIntent().getStringExtra("sport"), stats);

                }else{
                    Integer [] integers = new Integer [3];
                    integers[0] = 0;
                    integers[1] = 1;
                    integers[2] = 0;
                    Globals.mapStatistics.put(getIntent().getStringExtra("sport"),integers);
                }

                addToNextMatchesList();

            }
        });
    }

    private void addToNextMatchesList() {
        mFirestore.collection("users_matches").document(mFireauth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String matches = documentSnapshot.getString("matches");
                if (matches == "" || matches == null) {
                    matches = String.valueOf(id_match);
                    Map<String, String> user_matches = new HashMap<>();
                    user_matches.put("matches", matches);
                    mFirestore.collection("users_matches").document(mFireauth.getCurrentUser().getEmail()).set(user_matches);
                } else {
                    matches += "," + String.valueOf(id_match);
                    mFirestore.collection("users_matches").document(mFireauth.getCurrentUser().getEmail()).update("matches", matches);
                }

                moveToNextMatchesActivity();
            }
        });
    }

    private void throwToast() {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setMessage(getString(R.string.confirmDelete))
            .setTitle(getString(R.string.deleteMatch))
            .setPositiveButton(getString(R.string.yes), (new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteMatchAndMoveToNextActivity();
                }
            }))
            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .setCancelable(false)
            .create();

        dialog.show();
    }

}
