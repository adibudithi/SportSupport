package com.iedayan03.sportsupport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class HomeFragment extends Fragment {

    ListView fieldListView;
    ArrayList<String> fieldArray;
    private RequestQueue mQueue;
    private String fetchMoviesUrl = "http://iedayan03.web.illinois.edu/fetch_fields.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fieldListView = view.findViewById(R.id.fieldListView);
        fieldArray = new ArrayList<>();
        mQueue = Volley.newRequestQueue(getActivity());
        loadFields();
        return view;
    }

    /**
     * This method makes a GET request to our database using the Volley library and stores the response into fieldArray
     */
    private void loadFields() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchMoviesUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject field = jsonArray.getJSONObject(i);
                        String fieldName = field.getString("place_name");
                        fieldArray.add(fieldName);
                    }

                    displayFields();
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

        mQueue.add(request);
    }

    private void displayFields() {
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(
            getActivity(), android.R.layout.simple_list_item_1, fieldArray
        );

        fieldListView.setAdapter(listViewAdapter);
    }

}
