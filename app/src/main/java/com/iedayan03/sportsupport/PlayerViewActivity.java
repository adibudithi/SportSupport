package com.iedayan03.sportsupport;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerViewActivity extends AppCompatActivity {

    private static final String USERNAME = "Username";
    private String username;
    private String fullName;
    private String position;
    private int goals;
    private int assists;

    private RequestQueue queue;

    TextView fullNameTextView;
    TextView userNameTextView;
    TextView positionTextView;
    TextView goalsTextView;
    TextView assistsTextView;

    private String fetchUserURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view);

        username = getIntent().getExtras().getString(USERNAME);
        fetchUserURL = "http://iedayan03.web.illinois.edu/fetch_user.php?Username=" + username;
        queue = Volley.newRequestQueue(this);

        fullNameTextView = findViewById(R.id.fullNameId);
        userNameTextView = findViewById(R.id.userNameId);
        positionTextView = findViewById(R.id.positionId);
        goalsTextView = findViewById(R.id.goalsId);
        assistsTextView = findViewById(R.id.assistsId);

        loadUserDetails();

        userNameTextView.setText(username);
        if (fullName != null) fullNameTextView.setText(fullName);
        if (position != null) positionTextView.setText(position);
        goalsTextView.setText(String.valueOf(goals));
        assistsTextView.setText(String.valueOf(assists));
    }

    /**
     *
     */
    private void loadUserDetails() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchUserURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject player = jsonArray.getJSONObject(0);
                    fullName = player.getString("FullName");
                    position = player.getString("Position");
                    goals = player.getInt("Goals");
                    assists = player.getInt("Assists");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
    }
}
