package com.example.lolworldchampion;

import java.util.List;

public class Frame {
    private int frameInterval;
    private List<FrameEvent> events;

    public Frame() {
    }

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