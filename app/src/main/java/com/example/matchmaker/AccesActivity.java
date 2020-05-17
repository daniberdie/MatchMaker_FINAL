package com.example.matchmaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class AccesActivity extends Activity {

    private Button help_button, login_button;
    private boolean remember_user;
    private final int MY_REQUEST_CODE = 7117;
    private List<AuthUI.IdpConfig> providers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acces);

        //Autenticació firebase
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        new AsyncConnectionCheck().execute();

        help_button     = findViewById(R.id.ConfigAccessActivity);
        login_button    = findViewById(R.id.Login);

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
    }

    private void moveToLoginActivity() {
        showSignInOptions();
        //Intent intent_login = new Intent(AccesActivity.this, LoginActivity.class);
        //startActivity(intent_login);
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

        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
                Intent intent_initial = new Intent(AccesActivity.this, InitialActivity.class);
                startActivity(intent_initial);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveToHelpActivity() {
        Intent intent = new Intent(AccesActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    public class AsyncConnectionCheck extends AsyncTask<String, String, String> {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        @Override
        protected String doInBackground(String... strings) {

            //Comprobar connexió internet en segon pla
            if (networkInfo != null) {
                if (networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        return "Wifi connected!";
                    }
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return "Mobile connected!";
                    }
                }
            }
            return "disconnected";
        }

        @Override
        protected void onPostExecute(String result)
        {
            throwToast(result);
        }
    }

    public void throwToast(String result) {
        if(result.equals("disconnected")){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.internetConnection))
                    .setTitle(getString(R.string.notConnected))
                    .setPositiveButton(getString(R.string.retry), (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AsyncConnectionCheck().execute();
                        }
                    }))
                    .setCancelable(false)
                    .create();

            dialog.show();


        }
        Toast toast = Toast.makeText(this,result, Toast.LENGTH_LONG);
        toast.show();
    }

}
