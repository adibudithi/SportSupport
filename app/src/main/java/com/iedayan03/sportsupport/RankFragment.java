package com.iedayan03.sportsupport;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String fetchRankedPlayers = "http://iedayan03.web.illinois.edu/fetch_ranked_players.php";
    HashMap<String, Double> rankedPlayersMap;
    ArrayList<String> rankedPlayersList;
    ListView playerView;
    private RequestQueue queue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayAdapter<String> listViewAdapter;

    public RankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankFragment newInstance(String param1, String param2) {
        RankFragment fragment = new RankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        rankedPlayersMap = new HashMap<>();
        rankedPlayersList = new ArrayList<>();
        queue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Player rankings");
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        playerView = view.findViewById(R.id.playerViewId);
        listViewAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, rankedPlayersList
        );
        playerView.setAdapter(listViewAdapter);
        loadRankedPlayers();

        playerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Make sure to send more data/information about the soccer field. I have not yet retrieved
                // other information about the soccer field from the database.
                String itemName = (String) adapterView.getItemAtPosition(position);
                String[] result = itemName.split(" ");
                Intent intent = new Intent(getContext(), PlayerViewActivity.class);
                intent.putExtra("Username", result[0]);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     *
     */
    private void loadRankedPlayers() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchRankedPlayers, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject player = jsonArray.getJSONObject(i);
                        String playerUserName = player.getString("Username");
                        Double playerRank = player.getDouble("Rating");
                        String str = (i + 1) + "  -  " + playerUserName + "   (" + String.valueOf(playerRank).substring(0, 3) + ")";
                        rankedPlayersList.add(str);
                        listViewAdapter.notifyDataSetChanged();
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
