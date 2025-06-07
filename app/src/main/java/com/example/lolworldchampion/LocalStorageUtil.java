package com.example.lolworldchampion;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LocalStorageUtil {
    private static final String FAVORITE_EVENTS_FILE = "favorite_events.dat";

    public static void saveFavoriteEvents(Context context, List<FavoriteEvent> events) {
        try (FileOutputStream fos = context.openFileOutput(FAVORITE_EVENTS_FILE, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(events);
        } catch (IOException e) {
            Log.e("LocalStorageUtil", "Error saving favorite events", e);
        }
    }

    public static List<FavoriteEvent> loadFavoriteEvents(Context context) {
        List<FavoriteEvent> events = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FAVORITE_EVENTS_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            events = (List<FavoriteEvent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("LocalStorageUtil", "Error loading favorite events", e);
        }
        return events;
    }
}