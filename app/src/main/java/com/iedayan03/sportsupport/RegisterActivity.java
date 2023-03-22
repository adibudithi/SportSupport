package com.iedayan03.sportsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
            new RegisterWorker(this).execute(fullName, username, password);
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

    private class RegisterWorker extends AsyncTask<String, Void, String> {

        private Context context;
        String register_url = "http://iedayan03.web.illinois.edu/register.php";
        private String fullName;
        private String userName;
        private String password;

        public RegisterWorker(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                fullName = params[0];
                userName = params[1];
                password = params[2];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("FullName","UTF-8")+"="+URLEncoder.encode(fullName,"UTF-8")+"&"
                        + URLEncoder.encode("Username","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
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
                session.loginUser(userName, password); // when user has signed up, login the user
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else if (result.equals("Invalid")) {
                alertDialog.setMessage("Please Try Again.");
                alertDialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
