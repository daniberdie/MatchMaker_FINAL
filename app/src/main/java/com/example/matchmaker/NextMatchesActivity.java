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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NextMatchesActivity extends AppCompatActivity {

    private ListView listView;
    private Button back_button;
    private ArrayList<String> matches = new ArrayList<>();
    private String[] id_sorted_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_matches);

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

        try {
            createArrayList(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(NextMatchesActivity.this, MatchInfoActivity.class);
                intent.putExtra("sport",getIntent().getStringExtra("sport"));
                intent.putExtra("id_match",id_sorted_list[position]);
                startActivity(intent);
            }
        });

    }

    private void createArrayList(Context context) throws JSONException {
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.match_shared_data),Context.MODE_PRIVATE);
        SharedPreferences sharedIdList = context.getSharedPreferences(getString(R.string.id_list), Context.MODE_PRIVATE);
        Set<String> id_matches = sharedIdList.getStringSet(getString(R.string.id_number_list), null);

        String [] idArrayList = id_matches.toArray(new String [id_matches.size()]);

        id_sorted_list = new String[idArrayList.length];

        for(int i = 0; i < idArrayList.length; i++){
            String json = sharedPref.getString(idArrayList[i], null);
            JSONArray jsonArray = new JSONArray(json);
            List<String> list = new ArrayList<String>();
            for (int x = 0; x < jsonArray.length(); x++) {
                list.add(jsonArray.getString(x));
            }
            matches.add(list.get(0) + " | " + list.get(2) + "  " + list.get(3));
            id_sorted_list[i] = idArrayList[i];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_view_matches, matches);

        listView.setAdapter(adapter);


    }


}
