package com.example.MedicalClinic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.LinkedList;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see < a href=" ">Testing documentation</ a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    MainActivity view;

    @Mock
    Model model;

    @Captor
    private ArgumentCaptor<Consumer<User>> captor;


    @Test
    public void testPresenter1() {
        // Test log in as Patient Successful

        String email = "123@mail.com";
        String password = "123456";

        User patient = new Patient();

        Presenter presenter = new Presenter(model, view);
        presenter.login(email, password);

        // verify if model.authenticate() has run
        verify(model).authenticate(eq(email), eq(password), captor.capture());
        Consumer<User> callback = captor.getValue();
        callback.accept(patient);

        // verify if view.redirectToPatientDashboard has run
        verify(view, times(1)).redirectToPatientDashboard(any());
    }


    @Test
    public void testPresenter2() {
        // Test log in as Patient Un-successful

        String email = "123@mail.com";
        String password = "123456";

        User patient = null;

        Presenter presenter = new Presenter(model, view);
        presenter.login(email, password);

        // verify if model.authenticate() has run
        verify(model).authenticate(eq(email), eq(password), captor.capture());
        Consumer<User> callback = captor.getValue();
        callback.accept(patient);

        // verify if view.redirectToPatientDashboard has run
        verify(view, times(0)).redirectToPatientDashboard(any());
    }

    @Test
    public void testPresenter3() {
        // Test log in as Doctor Successful

        String email = "123@mail.com";
        String password = "123456";

        User doctor = new Doctor();

        Presenter presenter = new Presenter(model, view);
        presenter.login(email, password);

        // verify if model.authenticate() has run
        verify(model).authenticate(eq(email), eq(password), captor.capture());
        Consumer<User> callback = captor.getValue();
        callback.accept(doctor);

        // verify if view.redirectToPatientDashboard has run
        verify(view, times(1)).redirectToDoctorDashboard(any());
    }


    @Test
    public void testPresenter4() {
        // Test log in as Doctor un-successful

        String email = "123@mail.com";
        String password = "123456";

        User doctor = null;

        Presenter presenter = new Presenter(model, view);
        presenter.login(email, password);

        // verify if model.authenticate() has run
        verify(model).authenticate(eq(email), eq(password), captor.capture());
        Consumer<User> callback = captor.getValue();
        callback.accept(doctor);

        // verify if view.redirectToPatientDashboard has run
        verify(view, times(0)).redirectToDoctorDashboard(any());
    }
}