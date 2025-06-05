package com.example.lolworldchampion;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ParticipantCSVParser {
    public static Map<String, Map<Integer, String>> parseParticipantCSV(Context context) {
        Map<String, Map<Integer, String>> participantMap = new HashMap<>();

        try (InputStream inputStream = context.getAssets().open("participantname.csv");
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",", -1); // 使用-1保留空字段
                    if (fields.length >= 3) {
                        String matchId = fields[0].trim();
                        int participantId = Integer.parseInt(fields[1].trim());
                        String participantName = fields[2].trim();

                        if (!matchId.isEmpty() && !participantName.isEmpty()) {
                            participantMap.computeIfAbsent(matchId, k -> new HashMap<>())
                                    .put(participantId, participantName);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ParticipantCSVParser", "Error parsing line: " + line, e);
                }
            }
        } catch (Exception e) {
            Log.e("ParticipantCSVParser", "Error reading CSV file", e);
        }

        Log.d("ParticipantCSVParser", "Parsed " + participantMap.size() + " matches");
        return participantMap;
    }
}