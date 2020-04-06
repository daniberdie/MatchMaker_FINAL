package com.example.matchmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button  back_button, enter_button;
    private EditText user, password;
    private CheckBox remember_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        back_button = findViewById(R.id.back_button_login);
        enter_button = findViewById(R.id.enter_button);

        user = findViewById(R.id.user);
        password = findViewById(R.id.password);
        remember_check = findViewById(R.id.remember_check);

        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMainActivity();
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAccessActivity();
            }
        });
    }

    private void moveToMainActivity() {

        if(validateLogin(this)){
            //TODO: Mantenir sessió iniciada
            rememberUserChecker(this);
            Intent intent = new Intent(LoginActivity.this, InitialActivity.class);
            startActivity(intent);
        }else{
            Toast toast = Toast.makeText(this,getString(R.string.invalid_user_pass), Toast.LENGTH_LONG);
            toast.show();
        }


    }

    private void rememberUserChecker(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.remember_pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(remember_check.isChecked()){
            editor.putBoolean(getString(R.string.remember_user),true);
        } else {
            editor.putBoolean(getString(R.string.remember_user),false);
        }

        editor.commit();
        editor.apply();
    }

    private boolean validateLogin(Context context) {

        boolean ret = false;

        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        String default_user = sharedPref.getString(getString(R.string.saved_user),"admin");
        String default_password = sharedPref.getString(getString(R.string.saved_password),"admin");

        if(user.getText().toString().equals(default_user) && password.getText().toString().equals(default_password)){
            ret = true;
        }

        return ret;
    }

    private void moveToAccessActivity() {
        finish();
    }
}
