package com.iedayan03.sportsupport;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FieldActivity extends AppCompatActivity {

    TextView fieldNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        fieldNameTextView = findViewById(R.id.fieldNameId);
        String fieldName = getIntent().getExtras().getString("Field Name");
        fieldNameTextView.setText(fieldName);
    }
}
