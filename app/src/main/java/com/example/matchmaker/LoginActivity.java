package com.example.matchmaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button  back_button, enter_button;
    private EditText user, password;
    private CheckBox remember_check;
    private boolean remember_user;
    private final int MY_REQUEST_CODE = 7117;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Autenticació firebase
        providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

        showSignInOptions();

        back_button = findViewById(R.id.back_button_login);
        enter_button = findViewById(R.id.enter_button);

        user = findViewById(R.id.user);
        password = findViewById(R.id.password);
        remember_check = findViewById(R.id.remember_check);

        //checkLogin();

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

    private void showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().
                                createSignInIntentBuilder().
                                setAvailableProviders(providers).
                                setTheme(R.style.MyTheme).
                                build(),MY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MY_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        /*// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }*/
    }

    //Ús adequat sharedPreferences
    private void checkLogin() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.remember_pref), Context.MODE_PRIVATE);
        remember_user = sharedPref.getBoolean(getString(R.string.remember_user),false);
        if(remember_user) {
            user.setText(sharedPref.getString("username", ""));
            password.setText(sharedPref.getString("password", ""));
            remember_check.setEnabled(true);
        }
    }

    private void moveToMainActivity() {

        if(validateLogin(this)){
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
            editor.putString("username", user.getText().toString());
            editor.putString("password", password.getText().toString());

        } else {
            editor.putBoolean(getString(R.string.remember_user),false);
        }

        editor.commit();
        editor.apply();
    }

    private boolean validateLogin(Context context) {

        /*boolean ret = false;

        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        String default_user = sharedPref.getString(getString(R.string.saved_user),"admin");
        String default_password = sharedPref.getString(getString(R.string.saved_password),"admin");

        if(user.getText().toString().equals(default_user) && password.getText().toString().equals(default_password)){
            ret = true;
        }

        return ret;*/
        return true;
    }

    private void moveToAccessActivity() {
        finish();
    }
}
