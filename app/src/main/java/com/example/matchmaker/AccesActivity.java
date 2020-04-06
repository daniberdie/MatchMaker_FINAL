package com.example.matchmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccesActivity extends Activity {

    private Button help_button, login_button, register_button;
    private boolean remember_user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acces);

        help_button     = findViewById(R.id.ConfigAccessActivity);
        login_button    = findViewById(R.id.Login);
        register_button = findViewById(R.id.Register);

        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHelpActivity();
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginActivity();
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegisterActivity();
            }
        });
    }

    private void moveToRegisterActivity() {
        Intent intent = new Intent(AccesActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void moveToLoginActivity() {
        getRememverUserValue(this);
        if(remember_user){
            Intent intent_initial = new Intent(AccesActivity.this, InitialActivity.class);
            startActivity(intent_initial);
        } else {
            Intent intent_login = new Intent(AccesActivity.this, LoginActivity.class);
            startActivity(intent_login);
        }
    }

    private void getRememverUserValue(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.remember_pref), Context.MODE_PRIVATE);
        remember_user = sharedPref.getBoolean(getString(R.string.remember_user),false);
    }

    private void moveToHelpActivity() {
        Intent intent = new Intent(AccesActivity.this, HelpActivity.class);
        startActivity(intent);
    }

}
