package com.iedayan03.sportsupport;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    ListView fieldListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // we will need to query the fields from our database and store it as JSON
        String[] items = {"Field1", "Field2", "Field2"};
        fieldListView = view.findViewById(R.id.fieldListView);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
            getActivity(), android.R.layout.simple_list_item_1, items
        );

        fieldListView.setAdapter(listViewAdapter);



        return view;
    }

    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


}
