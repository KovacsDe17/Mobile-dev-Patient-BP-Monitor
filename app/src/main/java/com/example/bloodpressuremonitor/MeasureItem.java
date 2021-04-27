package com.example.bloodpressuremonitor;

import java.util.Calendar;

public class MeasureItem {
    private int userId;
    private String id;
    private String date;
    private String timeOfDay;
    private int systolic;
    private int diastolic;
    private int pulse;

    public MeasureItem(){}

    public MeasureItem(int userId, String date, String timeOfDay, int systolic, int diastolic, int pulse) {
        this.userId = userId;
        this.date = date;
        this.timeOfDay = timeOfDay;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
    }

    public String _getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getDate() {
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

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MeasureItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", timeOfDay=" + timeOfDay +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", pulse=" + pulse +
                '}';
    }

    public static int currentTimeOfDay(){
        int tod;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(hour <= 10){
            tod = 0;
        } else if(hour <= 15){
            tod = 1;
        } else {
            tod = 2;
        }

        return tod;
    }

    public static String currentDate(){
        String date;

        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH)+1;
        int d = c.get(Calendar.DAY_OF_MONTH);

        date = y + "-" + (m<10?("0"+m):m) + "-" + d;

        return date;
    }
}
