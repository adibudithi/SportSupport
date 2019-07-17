package com.iedayan03.sportsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    public void Login(View view) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String type = "login";
        backgroundWorker.execute(type, username, password);
    }

    // maybe separate login and register as different activities
    public void Register(View view) {
        // have not setup the new edit texts so won't work until fixed
    }
}
