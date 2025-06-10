package com.example.lolworldchampion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private Spinner yearSpinner;
    private Spinner leagueSpinner;
    private Button filterButton;
    private String selectedYear;
    private String selectedLeague;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        yearSpinner = findViewById(R.id.yearSpinner);
        leagueSpinner = findViewById(R.id.leagueSpinner);
        filterButton = findViewById(R.id.filterButton);

        // Set up year options
        List<String> years = new ArrayList<>();
        years.add("All Years");
        years.add("2022");
        years.add("2023");
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Set up league options
        List<String> leagues = new ArrayList<>();
        leagues.add("All Leagues");
        leagues.add("MSI");
        leagues.add("WLDs");
        ArrayAdapter<String> leagueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, leagues);
        leagueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(leagueAdapter);

        // Spinner selection listeners
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = position == 0 ? null : years.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = null;
            }
        });

        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLeague = position == 0 ? null : leagues.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLeague = null;
            }
        });

        // Filter button click listener
        filterButton.setOnClickListener(v -> {
            Log.d("FilterActivity", "Selected Year: " + selectedYear + ", League: " + selectedLeague);
            Intent intent = new Intent(FilterActivity.this, MatchListActivity.class);
            intent.putExtra("year", selectedYear);
            intent.putExtra("league", selectedLeague);
            startActivity(intent);
        });
    }
}