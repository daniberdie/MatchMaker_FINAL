package com.example.matchmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class InitialActivity extends Activity {

    private ImageButton futbolButton, padelButton, basketButton;
    private Button  logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        futbolButton = findViewById(R.id.futbolButton);
        padelButton  = findViewById(R.id.padelButton);
        basketButton = findViewById(R.id.basketButton);

        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        basketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MenuActivity.class);
                intent.putExtra("sport", "bas");
                startActivity(intent);
            }
        });

        futbolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MenuActivity.class);
                intent.putExtra("sport", "fut");
                startActivity(intent);
            }
        });

        padelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MenuActivity.class);
                intent.putExtra("sport", "pad");
                startActivity(intent);
            }
        });
    }

    private void logoutUser() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.remember_pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(getString(R.string.remember_user),false);

        editor.commit();
        editor.apply();

        Intent intent = new Intent(InitialActivity.this,AccesActivity.class);
        startActivity(intent);
    }
}
