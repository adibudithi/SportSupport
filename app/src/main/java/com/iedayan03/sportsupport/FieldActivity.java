package com.iedayan03.sportsupport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import static android.view.View.*;

public class FieldActivity extends AppCompatActivity {

    private static final String FIELD_NAME = "Field Name";
    private static final String FIELD_ADDRESS = "Field Address";
    private static final String FIELD_PLACE_ID = "Field PlaceId";
    private static final String USERNAME = "Username";

    private static final String joinGameURL = "http://iedayan03.web.illinois.edu/join_game.php";
    private static final String leaveGameURL = "http://iedayan03.web.illinois.edu/leave_game.php";
    private static final String swapTeamURL = "http://iedayan03.web.illinois.edu/swap_team.php";

    private static final String JOIN_GAME_ERROR_RESPONSE = "You Can Only Join Once";
    private static final String LEAVE_GAME_ERROR_RESPONSE = "You Have Already Left The Game";

    private ArrayList<String>  homePlayerNames, awayPlayerNames;
    private ListView homePlayerListView, awayPlayerListView;
    private ArrayAdapter<String> homeAdapter, awayAdapter;

    private TextView fieldNameTextView;
    private TextView fieldAddressTextView;

    Button joinLeaveBtn, swapTeamBtn, startGameBtn;
    boolean isJoined = false;

    private SessionHandler session;
    private User currUser;
    private String playerName;
    private String place_id; // primary key of Field
    private RequestQueue queue;
    private String fieldAddress;
    private String fieldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        queue = Volley.newRequestQueue(this);
        session = new SessionHandler(getApplicationContext());
        currUser = session.getUserDetails();
        playerName = currUser.getUsername();

        joinLeaveBtn = findViewById(R.id.joinLeaveBtnId);
        swapTeamBtn = findViewById(R.id.swapTeamBtnId);
        startGameBtn = findViewById(R.id.start_game_button);
        swapTeamBtn.setClickable(isJoined); // not pressable if no team joined
        startGameBtn.setClickable(isJoined); // ditto

        fieldNameTextView = findViewById(R.id.fieldNameId);
        fieldAddressTextView = findViewById(R.id.fieldAddressId);
        fieldName = getIntent().getExtras().getString(FIELD_NAME);
        fieldAddress = getIntent().getExtras().getString(FIELD_ADDRESS);
        place_id = getIntent().getExtras().getString(FIELD_PLACE_ID);
        fieldNameTextView.setText(fieldName);
        fieldAddressTextView.setText(fieldAddress);

        homePlayerNames = new ArrayList<>(11);
        homePlayerListView = findViewById(R.id.homePlayerListViewId);
        homeAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, homePlayerNames);
        homePlayerListView.setAdapter(homeAdapter);

        awayPlayerNames = new ArrayList<>(11);
        awayPlayerListView = findViewById(R.id.awayPlayerListViewId);
        awayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, awayPlayerNames);
        awayPlayerListView.setAdapter(awayAdapter);

        /*
         * OnClickListener that adds a player's name to the arraylist "playerName"
         */
        joinLeaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if player has already been added
                if (!isJoined) {
                    joinGame();
                } else {
                    leaveGame();
                }
            }
        });

        swapTeamBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest postRequest = new StringRequest(Request.Method.POST, swapTeamURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                int retval = Integer.parseInt(response);
                                if (retval == 1) {
                                    loadPlayers();
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
            }
        });


        homePlayerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String username = homePlayerNames.get(position);
                Intent intent = new Intent(view.getContext(), PlayerViewActivity.class);
                intent.putExtra(USERNAME, username);
                startActivity(intent);
            }
        });
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPlayers();
        updateIsJoined();
    }

    private void updateIsJoined() {
        isJoined = homePlayerNames.contains(playerName)
                || awayPlayerNames.contains(playerName);
        joinLeaveBtn.setText(isJoined ?
                getString(R.string.leave_button_text) :
                getString(R.string.join_button_text) );

        swapTeamBtn.setClickable(isJoined);
        swapTeamBtn.setAlpha(isJoined ? 1 : (float) 0.5);

        startGameBtn.setClickable(isJoined);
        startGameBtn.setAlpha(isJoined ? 1 : (float) 0.5);

    }

    /**
     * Initializes the arraylist 'homePlayerNames' with other players who have already joined the game.
     */
    private void loadPlayers() {
        // Need to send information about which field it is by sending a POST request.
        final String fetchPlayersURL = "http://iedayan03.web.illinois.edu/fetch_players.php?place_id=" + place_id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchPlayersURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray userNameArray;
                    JSONObject responseData = response.getJSONObject("data");
                    homePlayerNames.clear();
                    awayPlayerNames.clear();
                    if (responseData.has("isHome")) {
                        userNameArray = responseData.getJSONArray("isHome");

                        for (int i = 0; i < userNameArray.length(); i++) {
                            String playerName = (String) userNameArray.get(i);
                            homePlayerNames.add(playerName);
                            homeAdapter.notifyDataSetChanged();
                        }
                    }
                    if (responseData.has("isAway")){
                        userNameArray = responseData.getJSONArray("isAway");

                        for (int i = 0; i < userNameArray.length(); i++) {
                            String playerName = (String) userNameArray.get(i);
                            awayPlayerNames.add(playerName);
                            awayAdapter.notifyDataSetChanged();
                        }
                    }

                    Log.d("JSON response", responseData.toString());
                    updateIsJoined();


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

    private void joinGame() {
        if (homePlayerNames.indexOf(playerName) == -1) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, joinGameURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            int retval = Integer.parseInt(response);
                            if (retval == 1) {
                                homePlayerNames.add(playerName);
                                homeAdapter.notifyDataSetChanged();
                                updateIsJoined();
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

    private void leaveGame(){
        final int indexOfPlayer = Math.max(
                homePlayerNames.indexOf(playerName),
                awayPlayerNames.indexOf(playerName));

        // check if player is in the list, if player does not exist, do not remove again (should be safe though).
        if (indexOfPlayer > -1) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, leaveGameURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            int retval = Integer.parseInt(response);
                            if (retval == 1) {
                                // avert eyes, should be safe lol
                                homePlayerNames.remove(playerName);
                                awayPlayerNames.remove(playerName);
                                homeAdapter.notifyDataSetChanged();
                                awayAdapter.notifyDataSetChanged();
                                updateIsJoined();
                            } else if (retval == -1) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Cannot leave game after it has ended",
                                        Toast.LENGTH_SHORT).show();
                            } else if (retval == 0) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "SQL ERROR",
                                        Toast.LENGTH_SHORT).show();
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

    /**
     * On click method invoked when user clicks on the FieldAddressTextView
     * @param view
     */
    public void displayLocation(View view) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + fieldAddress);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void recordGame(View view) {
        Intent recordGameIntent = new Intent(this, GameStatRecordActivity.class);
        recordGameIntent.putExtra("place_id", place_id);
        startActivity(recordGameIntent);
    }
}
