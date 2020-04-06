package com.example.matchmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private Button back_button, save_button;
    EditText register_user, register_password;
    TextView error_text_register;

    private static int maxCharacterLength = 20;
    private static int minCharacterLength = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        back_button = findViewById(R.id.back_button_register);
        save_button = findViewById(R.id.save_button);

        register_user = findViewById(R.id.register_user);
        register_password = findViewById(R.id.register_password);

        error_text_register = findViewById(R.id.error_text_register);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAccessActivity();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void saveUserData() {

        if(validateRegister()){

            //Guardar usuari i contrassenya
            setPreferences(this);

            if(error_text_register.getVisibility() == View.VISIBLE)
            {
                error_text_register.setVisibility(View.INVISIBLE);
            }

            moveToAccessActivity();
        }else{
            error_text_register.setVisibility(View.VISIBLE);
        }
    }

    private void setPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_user), register_user.getText().toString());
        editor.putString(getString(R.string.saved_password), register_password.getText().toString());
        editor.commit();
        editor.apply();
    }

    private boolean validateRegister() {
        boolean ret = true;

        if(register_user.getText().length() < minCharacterLength || register_user.getText().length() > maxCharacterLength ||
           register_password.getText().length() < minCharacterLength || register_password.getText().length() > maxCharacterLength){
            ret = false;
        }

        return ret;
    }

    private void moveToAccessActivity() {
        finish();
    }
}
