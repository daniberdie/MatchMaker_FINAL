package com.example.matchmaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

public class HelpActivity extends Activity {

    private     TextView description;
    private     Button back_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        description = findViewById(R.id.description);
        description.setText(getString(R.string.App_Description1) + "\n" + getString(R.string.App_Description2) + "\n" + getString(R.string.App_Description3));

        back_button = findViewById(R.id.back_button_help);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        description.setMovementMethod(new ScrollingMovementMethod());
    }
}
