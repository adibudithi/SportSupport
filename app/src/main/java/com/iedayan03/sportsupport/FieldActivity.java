package com.iedayan03.sportsupport;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// NOTE: INCOMPLETE
public class FieldActivity extends AppCompatActivity {

    private static final String FIELD_NAME = "Field Name";
    private static final String FIELD_ADDRESS = "Field Address";
    private static final String FIELD_PLACE_ID = "Field PlaceId";
    private static final String joinGameURL = "http://iedayan03.web.illinois.edu/join_game.php";
    private static final String leaveGameURL = "http://iedayan03.web.illinois.edu/leave_game.php";
    private static final String fetchPlayersURL = "http://iedayan03.web.illinois.edu/fetch_players.php";
    private static final String JOIN_GAME_ERROR_RESPONSE = "You Can Only Join Once";
    private static final String LEAVE_GAME_ERROR_RESPONSE = "You Have Already Left The Game";

    private ArrayList<String> playerNames;
    private ListView playerListView;
    private ArrayAdapter<String> adapter;
    private TextView fieldNameTextView;
    private TextView fieldAddressTextView;

    Button joinBtn;
    Button leaveBtn;

    private SessionHandler session;
    private User currUser;
    private String playerName;
    private String place_id; // primary key of Field
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        queue = Volley.newRequestQueue(this);
        session = new SessionHandler(getApplicationContext());
        currUser = session.getUserDetails();
        playerName = currUser.getUsername();

        joinBtn = findViewById(R.id.joinBtnId);
        leaveBtn = findViewById(R.id.leaveBtnId);

        fieldNameTextView = findViewById(R.id.fieldNameId);
        fieldAddressTextView = findViewById(R.id.fieldAddressId);
        String fieldName = getIntent().getExtras().getString(FIELD_NAME);
        String fieldAddress = getIntent().getExtras().getString(FIELD_ADDRESS);
        place_id = getIntent().getExtras().getString(FIELD_PLACE_ID);
        fieldNameTextView.setText(fieldName);
        fieldAddressTextView.setText(fieldAddress);

        playerNames = new ArrayList<>(22);
        loadPlayers();
        playerListView = findViewById(R.id.playerListViewId);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playerNames);
        playerListView.setAdapter(adapter);

        /**
         * OnClickListener that adds a player's name to the arraylist "playerName"
         */
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if player has already been added
                if (playerNames.indexOf(playerName) == -1) {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, joinGameURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    int retval = Integer.parseInt(response);
                                    if (retval == 1) {
                                        playerNames.add(playerName);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("place_id", place_id);
                            params.put("Username", playerName);
                            return params;
                        }
                    };

                    queue.add(postRequest);
                } else {
                    Toast.makeText(getApplicationContext(), JOIN_GAME_ERROR_RESPONSE, Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * OnClickListener that removes a player's name from the arraylist "playerName"
         */
        leaveBtn.setOnClickListener(new View.OnClickListener() {

            private int indexOfPlayer;

            @Override
            public void onClick(View view) {
                indexOfPlayer = playerNames.indexOf(playerName);

                // check if player is in the list
                if (indexOfPlayer != -1) {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, leaveGameURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    int retval = Integer.parseInt(response);
                                    if (retval == 1) {
                                        playerNames.remove(indexOfPlayer);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("place_id", place_id);
                            params.put("Username", playerName);
                            return params;
                        }
                    };

                    queue.add(postRequest);
                } else {
                    Toast.makeText(getApplicationContext(), LEAVE_GAME_ERROR_RESPONSE, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Initializes the arraylist 'playerNames' with other players who have already joined the game.
     */
    private void loadPlayers() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchPlayersURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject field = jsonArray.getJSONObject(i);
                        String playerName = field.getString("Username");
                        playerNames.add(playerName);
                        adapter.notifyDataSetChanged();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);
    }
}
