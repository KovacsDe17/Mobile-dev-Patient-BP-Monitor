package com.example.bloodpressuremonitor;

import java.time.LocalDate;

public class MeasureItem {
    private LocalDate date;
    private String timeOfDay;
    private int systolic;
    private int diastolic;
    private int pulse;

    public MeasureItem(LocalDate date, String timeOfDay, int systolic, int diastolic, int pulse) {
        this.date = date;
        this.timeOfDay = timeOfDay;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public int getSystolic() {
        return systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public int getPulse() {
        return pulse;
    }

    @Override
    public String toString() {
        return "MeasureItem{" +
                "date=" + date +
                ", timeOfDay=" + timeOfDay +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", pulse=" + pulse +
                '}';
    }
}
