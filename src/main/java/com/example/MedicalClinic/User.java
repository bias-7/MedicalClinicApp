package com.example.MedicalClinic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable  {

    private String email;
    private String name;
    private String gender;

    private String ID;


    public User (){

    }

    public User(String email, String name, String gender) {
        this.email = email;
        this.name = name;
        this.gender = gender;

    }

    @Override
    public String toString() {
        return "Name: " + name + "\nGender: " + gender + "\nEmail: " + email + "\n";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
