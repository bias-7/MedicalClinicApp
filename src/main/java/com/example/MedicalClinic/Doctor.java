package com.example.MedicalClinic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Doctor extends User{

    private String specialty;

    public final static String[] days = new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public final static String[] hours = new String[] {"9", "10", "11", "13", "14", "15", "16", "17"};
    private Map<String, Map<String, String>> timeslots;


    public Doctor() {

    }

    public Doctor(String email, String name, String age, String specialty) {
        super(email, name, age);
        this.specialty = specialty;
        this.timeslots = new HashMap<>();
        for (String day : days) {
            HashMap<String, String> timeslot = new HashMap<>();
            this.timeslots.put(day, timeslot);

            for (String hour: hours)
                timeslot.put(hour, "");
        }
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Map<String, Map<String, String>> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(Map<String, Map<String, String>> timeslots) {
        this.timeslots = timeslots;
    }

    public void resetTimeslot(int day, int hour) {
        timeslots.get(days[day - 1]).put(String.valueOf(hour), "");
    }

}
