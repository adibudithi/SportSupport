package com.iedayan03.sportsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_EMPTY = "";
    EditText etUsername, etPassword;
    private SessionHandler session;
    AlertDialog alertDialog;

    /**
     *
     * @param savedInstanceState
     */
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

    /**
     *
     * @param view
     */
    public void Login(View view) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInputs(username, password)) {
             new LoginWorker(this).execute(username, password);
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

    /**
     * This is a helper class that established connection with the database behind the scenes.
     */
    private class LoginWorker extends AsyncTask<String, Void, String> {

        Context context;
        private String userName;
        private String password;
        String login_url = "http://iedayan03.web.illinois.edu/login.php";

        public LoginWorker(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                userName = params[0];
                password = params[1];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Username","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                        + URLEncoder.encode("Password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new android.app.AlertDialog.Builder(context).create();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Success")) {
                session.loginUser(userName, password);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (result.equals("Invalid")) {
                alertDialog.setMessage("Username or Password is Incorrect. Please try again.");
                alertDialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
