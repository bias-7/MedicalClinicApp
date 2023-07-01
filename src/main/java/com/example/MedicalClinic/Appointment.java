package com.example.MedicalClinic;

import android.text.format.DateFormat;

import java.util.Calendar;

public class Appointment {


    public final static String DATE_FORMAT = "EEEE, dd MMM yyyy";
    public final static String TIME_FORMAT = "hh:mm a";
    public final static String DATETIME_FORMAT = DATE_FORMAT + ", " + TIME_FORMAT;


    private String doctorID;
    private String doctorName;

    private String patientID;
    private String patientName;

    private String dateTime;

    public Appointment() {

    }

    public Appointment(String doctorID, String doctorName, String patientID, String patientName, Calendar calendar) {
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.patientID = patientID;
        this.patientName = patientName;
        this.dateTime = DateFormat.format(DATETIME_FORMAT, calendar).toString();
    }

    public Appointment(String doctorID, String doctorName, String patientID, String patientName, String dateTime) {
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.patientID = patientID;
        this.patientName = patientName;
        this.dateTime = dateTime;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}