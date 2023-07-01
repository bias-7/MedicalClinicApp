package com.example.MedicalClinic;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User {

    private String birth;

    private List<String> doctors;

    public Patient() {
        this.doctors = new ArrayList<String>();
    }

    public Patient(String email, String name, String age, String birth) {
        super(email, name, age);
        this.birth = birth;
        this.doctors = new ArrayList<String>();
    }

    @Override
    public String toString() {
        String res = super.toString();
        res = res + "Previous Seen Doctors:";
        for (String docName: doctors)
            res = res + "\n  " + docName;

        return res;
    }

    public void addDoctor(String docName) {
        if (!doctors.contains(docName))
            doctors.add(docName);
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public List<String> getDoctors() {
        return doctors;
    }

    public String getBirth() {
        return birth;
    }

    public void setDoctors(List<String> doctors) {
        this.doctors = doctors;
    }


}
