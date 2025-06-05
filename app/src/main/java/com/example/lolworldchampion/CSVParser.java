package com.example.lolworldchampion;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class CSVParser {
    public static List<MatchSummary> parseMatchSummaries(InputStream inputStream) {
        List<MatchSummary> matchSummaries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = parseCSVLine(line);

                    if (fields.length >= 4) {
                        String matchId = fields[0].trim();
                        String blueTeam = fields[1].trim();
                        String redTeam = fields[2].trim();
                        String winner = fields[3].trim();

                        matchSummaries.add(new MatchSummary(matchId, blueTeam, redTeam, winner));
                    }
                } catch (Exception e) {
                    Log.e("CSVParser", "Error parsing line: " + line, e);
                }
            }
        } catch (Exception e) {
            Log.e("CSVParser", "Error reading CSV file", e);
            return null;
        }

        return matchSummaries;
    }

    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }
}