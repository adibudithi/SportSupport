package com.iedayan03.sportsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class GameStatRecordActivity extends AppCompatActivity {

    public static String SUBMIT_GAME_URL = "http://iedayan03.web.illinois.edu/submit_game.php";

    int assists = 0, goals = 0;

    String place_id, playerName;

    TextView assistCount, goalCount;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stat_record);

        requestQueue = Volley.newRequestQueue(this);

        SessionHandler userSession = new SessionHandler(getApplicationContext());

        playerName = userSession.getUserDetails().getUsername();
        place_id = getIntent().getStringExtra("place_id");

        assistCount = findViewById(R.id.assists_record_view);
        goalCount = findViewById(R.id.goals_record_view);

        assistCount.setText(String.format(
                getString(R.string.assists_recorded_template),
                assists
        ));

        goalCount.setText(String.format(
                getString(R.string.goals_recorded_template),
                goals
        ));
    }

    public void increaseAssists(View view) {
        assistCount.setText(String.format(
                getString(R.string.assists_recorded_template),
                ++assists
        ));
    }

    public void decreaseAssists(View view) {
        if (assists > 0) {
            assistCount.setText(String.format(
                    getString(R.string.assists_recorded_template),
                    --assists
            ));
        }
    }

    public void increaseGoals(View view) {
        goalCount.setText(String.format(
                getString(R.string.goals_recorded_template),
                ++goals
        ));
    }

    public void decreaseGoals(View view) {
        if (goals > 0) {
            goalCount.setText(String.format(
                    getString(R.string.goals_recorded_template),
                    --goals
            ));
        }
    }

    public void endGame(View view) {
        requestQueue.add(submitGameRequest);
        Intent profileIntent = new Intent(this, MainActivity.class);
        startActivity(profileIntent);
        finish();
    }

    private StringRequest submitGameRequest = new StringRequest(
            Request.Method.POST,
            SUBMIT_GAME_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (Integer.parseInt(response) == 1)
                        Toast.makeText(GameStatRecordActivity.this,
                                "Your game stats have been submitted",
                                Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(GameStatRecordActivity.this,
                                "Something Went Wrong",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("place_id", place_id);
            params.put("Username", playerName);
            params.put("assists", String.format("%d",assists));
            params.put("goals", String.format("%d",goals));
            return params;
        }
    };
}
