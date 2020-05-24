package com.example.matchmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NextMatchesActivity extends AppCompatActivity {

    private ListView listView;
    private Button back_button;
    private ArrayList<String> matches = new ArrayList<>();
    private List<String> idArrayList = new ArrayList<>();
    private TextView textView;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFireauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_matches);

        mFirestore = FirebaseFirestore.getInstance();
        mFireauth = FirebaseAuth.getInstance();

        LinearLayout layout = findViewById(R.id.next_matches_layout);
        listView = findViewById(R.id.listView_next_matches);
        back_button = findViewById(R.id.back_button_main_next);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextMatchesActivity.this,MenuActivity.class);
                intent.putExtra("sport", getIntent().getStringExtra("sport"));
                startActivity(intent);
            }
        });


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

        createArrayList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(NextMatchesActivity.this, MatchInfoActivity.class);
                intent.putExtra("sport",getIntent().getStringExtra("sport"));
                intent.putExtra("id_match",idArrayList.get(position));
                intent.putExtra("activity","next");
                startActivity(intent);
            }
        });

    }

    private void createArrayList() {

        mFirestore.collection("users_matches").document(mFireauth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    final String[] id_sorted_list = documentSnapshot.getString("matches").split(",");

                    for (int i = 0; i < id_sorted_list.length; i++) {
                        if(!id_sorted_list[i].equals("")){
                            mFirestore.collection("app_data").document(id_sorted_list[i]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        if(documentSnapshot.getString("sport").equals(getIntent().getStringExtra("sport"))){
                                            idArrayList.add(documentSnapshot.getId());
                                            matches.add(documentSnapshot.getString("description") + " | " + documentSnapshot.getString("date") + "  " + documentSnapshot.getString("time"));
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NextMatchesActivity.this, R.layout.list_view_matches, matches);
                                            listView.setAdapter(adapter);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}
