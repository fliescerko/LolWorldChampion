package com.example.lolworldchampion;

import java.io.Serializable;
import java.util.List;

public class Frame implements Serializable {
    private static final long serialVersionUID = 1L;
    private int frameInterval;
    private List<FrameEvent> events;

    public int getFrameInterval() {
        return frameInterval;
    }

    public void setFrameInterval(int frameInterval) {
        this.frameInterval = frameInterval;
    }

    public List<FrameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<FrameEvent> events) {
        this.events = events;
    }
}