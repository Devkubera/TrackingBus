package com.example.projectandroid;

public class TimeSchedule {
    private Long id;
    private String position;
    private String time;

    public TimeSchedule() {}

    public TimeSchedule(Long id, String position, String time) {
        this.id = id;
        this.position = position;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
