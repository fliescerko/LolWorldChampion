package com.example.lolworldchampion;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Frame implements Serializable {
    private static final long serialVersionUID = 1L;
    private int frameInterval;
    private List<FrameEvent> events;
    private Map<Integer, ParticipantFrame> participantFrames;

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

    public Map<Integer, ParticipantFrame> getParticipantFrames() {
        return participantFrames;
    }

    public void setParticipantFrames(Map<Integer, ParticipantFrame> participantFrames) {
        this.participantFrames = participantFrames;
    }
}