package com.iedayan03.sportsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_EMPTY = "";
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        Button registerBtn = findViewById(R.id.registrationBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void Login(View view) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInputs(username, password)) {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String type = "login";
            backgroundWorker.execute(type, username, password);
        }
    }

    /**
     * Helper function to check if the user has entered his/her name
     * @return true if account details are not empty, false otherwise
     */
    private boolean validateInputs(String username, String password) {
        if (KEY_EMPTY.equals(username)) {
            etUsername.setError("Username can not be empty");
            etUsername.requestFocus();
            return false;
        } else if (KEY_EMPTY.equals(password)) {
            etPassword.setError("Password can not be empty");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}
