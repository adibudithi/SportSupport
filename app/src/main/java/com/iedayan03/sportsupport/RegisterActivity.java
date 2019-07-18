package com.iedayan03.sportsupport;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private static final String KEY_EMPTY = "";

    EditText etFullName, etUsername, etPassword;
    private SessionHandler session;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        session = new SessionHandler(getApplicationContext());

        etFullName = findViewById(R.id.fullName);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void Register(View view) {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInput(fullName, username, password)) {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String type = "register";
            backgroundWorker.execute(type, fullName, username, password);
//            boolean validLogin = session.getValidLogin();
//
//            if (validLogin == true) {
//                session.loginUser(username, password);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else if (validLogin == false) {
//                alertDialog = new AlertDialog.Builder(this).create();
//                alertDialog.setTitle("Login Status");
//                alertDialog.setMessage("Username or Password is Incorrect. Please try again.");
//                alertDialog.show();
//            }
        }
    }

    private boolean validateInput(String fullName, String username, String password) {
        if (KEY_EMPTY.equals(fullName)) {
            etFullName.setError("Name can not be empty");
            etFullName.requestFocus();
            return false;
        } else if (KEY_EMPTY.equals(username)) {
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
