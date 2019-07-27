package com.iedayan03.sportsupport;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FieldActivity extends AppCompatActivity {

    private static final String FIELD_NAME = "Field Name";
    public static final String FIELD_ADDRESS = "Field Address";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        session = new SessionHandler(getApplicationContext());
        currUser = session.getUserDetails();
        playerName = currUser.getUsername();

        joinBtn = findViewById(R.id.joinBtnId);
        leaveBtn = findViewById(R.id.leaveBtnId);

        fieldNameTextView = findViewById(R.id.fieldNameId);
        fieldAddressTextView = findViewById(R.id.fieldAddressId);
        String fieldName = getIntent().getExtras().getString(FIELD_NAME);
        String fieldAddress = getIntent().getExtras().getString(FIELD_ADDRESS);
        fieldNameTextView.setText(fieldName);
        fieldAddressTextView.setText(fieldAddress);

        playerNames = new ArrayList<>(22);
        playerListView = findViewById(R.id.playerListViewId);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playerNames);
        playerListView.setAdapter(adapter);

        /**
         * OnClickListener that adds a player's name to the arraylist "playerName"
         */
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = currUser.getUsername();

                // check if player has already been added
                if (playerNames.indexOf(playerName) == -1) {
                    playerNames.add(playerName);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * OnClickListener that removes a player's name from the arraylist "playerName"
         */
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = currUser.getUsername();
                int index = playerNames.indexOf(playerName);

                // check if player is in the list
                if (index != -1) {
                    playerNames.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
