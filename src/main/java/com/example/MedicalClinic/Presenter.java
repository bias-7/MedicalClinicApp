package com.example.MedicalClinic;

import android.util.Log;

import java.util.function.Consumer;

public class Presenter {

    private Model model;
    private MainActivity view;

    public Presenter(Model model, MainActivity view) {
        this.model = model;
        this.view = view;
    }

    public void login(String email, String password) {
        model.authenticate(email, password, (User user) -> {

            if (user == null) return;

            if (user instanceof Patient)
                view.redirectToPatientDashboard(user.getID());
            else
                view.redirectToDoctorDashboard(user.getID());
        });
    }
}

