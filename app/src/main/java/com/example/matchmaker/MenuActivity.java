package com.example.matchmaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MenuActivity extends Activity {

    private Button createMatchBtn, nextMatchsBtn, statisticsBtn, mapBtn, backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
                finish();
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
