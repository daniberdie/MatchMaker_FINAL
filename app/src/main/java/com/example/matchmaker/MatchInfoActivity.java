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
import java.util.List;
import java.util.Set;

public class MatchInfoActivity extends AppCompatActivity {

    private TextView description, location, date, time, level, players;
    private String strUser, id_match;
    private Button finish_exit, delete_match;
    private String comesFromActivity;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFireauth;
    public boolean checkUserCreator = false;

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

    private boolean checkUserCreatedMatch() {

        boolean ret = false;

        id_match = getIntent().getStringExtra("id_match");
        mFirestore.collection("app_data").document(id_match).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                strUser = documentSnapshot.getString("user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchInfoActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });

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

        id_match = getIntent().getStringExtra("id_match");
        mFirestore.collection("app_data").document(id_match).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                description.setText(documentSnapshot.getString("description"));
                location.setText(documentSnapshot.getString("location"));
                date.setText(documentSnapshot.getString("date"));
                time.setText(documentSnapshot.getString("time"));
                level.setText(documentSnapshot.getString("level"));
                players.setText(documentSnapshot.getString("players"));
                strUser = documentSnapshot.getString("user");

                if(checkUserCreatedMatch()){
                    delete_match.setVisibility(View.VISIBLE);
                    delete_match.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            throwToast();
                        }
                    });
                }else if (comesFromActivity.equals("map")){
                    delete_match.setVisibility(View.VISIBLE);
                    delete_match.setText(R.string.join);
                }else{
                    delete_match.setText(R.string.unjoin);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchInfoActivity.this, "Fail", Toast.LENGTH_SHORT).show();
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
