package com.iedayan03.sportsupport;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_EMPTY = "";
    EditText etUsername, etPassword;
    private SessionHandler session;
    AlertDialog alertDialog;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());

        // if user is already logged in then just proceed to MainActivity
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        Button registerBtn = findViewById(R.id.registrationBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            // will open the registration activity when the sign up button is clicked
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
//            boolean validLogin = session.getValidLogin();


//            if (result.equals("Success")) {
//                session.loginUser(username, password);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else if (result.equals("Invalid")) {
//                alertDialog = new AlertDialog.Builder(this).create();
//                alertDialog.setTitle("Login Status");
//                alertDialog.setMessage("Username or Password is Incorrect. Please try again.");
//                alertDialog.show();
//            }
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
