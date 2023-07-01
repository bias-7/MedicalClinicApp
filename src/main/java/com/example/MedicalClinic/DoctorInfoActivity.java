package com.example.MedicalClinic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DoctorInfoActivity extends AppCompatActivity implements View.OnClickListener{


    private Button btnBook;

    private TextView tvChangeDate, tvChangeTime, tvDate, tvTime, tvName, tvGender, tvSpecialty;

    private Spinner spinner;

    private ListView timeslotListView;

    private Calendar calendar;

    private Patient loggedInUser;
    private Doctor selectedDoctor;

    private String selectedDay;
    private String selectedHour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);

        tvName = (TextView) findViewById(R.id.textViewName);
        tvGender = (TextView) findViewById(R.id.textViewGender);
        tvSpecialty = (TextView) findViewById(R.id.textViewSpecialty);

        Intent intent = getIntent();
        loggedInUser = (Patient) intent.getSerializableExtra(getString(R.string.loggedInUser));
        selectedDoctor = (Doctor) intent.getSerializableExtra(getString(R.string.userInfo));

        tvName.setText(selectedDoctor.getName());
        tvGender.setText(selectedDoctor.getGender());
        tvSpecialty.setText(selectedDoctor.getSpecialty());

        calendar = Calendar.getInstance();

      spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Object> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Doctor.days);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(a);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedDay = (String) parent.getItemAtPosition(pos);

                List<String> availableHours = new ArrayList<String>();

                Map<String, String> timeslot = selectedDoctor.getTimeslots().get(selectedDay);
                for (String hour : Doctor.hours) {
                    if (timeslot.get(hour).equals(""))
                        availableHours.add(hour);
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(DoctorInfoActivity.this, android.R.layout.simple_list_item_1, availableHours);
                timeslotListView.setAdapter(aa);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        timeslotListView = (ListView) findViewById(R.id.timeslotListView);
        timeslotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                selectedHour = (String) parent.getItemAtPosition(pos);
                bookAppointment();
            }
        });
    }

    private void changeDate() {
        // get the current year, month, day value in integer
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);   // 0 is Jan, 11 is Dec
        int date = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                // update the calender
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, date);
                // update TextView
                tvDate.setText(DateFormat.format(Appointment.DATE_FORMAT, calendar));
            }
        }, year, month, date);

        datePickerDialog.show();
    }

    private void changeTime() {

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                // update the calendar
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                // update TextView
                tvTime.setText(DateFormat.format(Appointment.TIME_FORMAT, calendar));
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void bookAppointment() {

        calendar = Calendar.getInstance();

        int todayDay = calendar.get(Calendar.DAY_OF_WEEK);
        int day = getDayOfWeek(selectedDay);

        int diff = ((day - todayDay) + 7) % 7;

        if (diff == 0) {
            int todayHour = calendar.get(Calendar.HOUR_OF_DAY);
            int hour = Integer.parseInt(selectedHour);
            if (hour < todayHour)
                diff += 7;
        }

        // increment the date
        calendar.add(Calendar.DATE, diff);
        // change the hour
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(selectedHour));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Toast.makeText(this, calendar.getTime().toString(), Toast.LENGTH_LONG).show();


        Appointment app = new Appointment(
                selectedDoctor.getID(), selectedDoctor.getName(),
                loggedInUser.getID(), loggedInUser.getName(),
                DateFormat.format(Appointment.DATETIME_FORMAT, calendar).toString()
        );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Appointments");
        String key = reference.push().getKey();

        reference.child(key).setValue(app).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DoctorInfoActivity.this, "Appointment has been booked successfully", Toast.LENGTH_LONG).show();

                    // update the doctor timeslot availability
                    selectedDoctor.getTimeslots().get(selectedDay).put(selectedHour, loggedInUser.getName());
                    // also update to the firebase database
                    FirebaseDatabase.getInstance().getReference("Doctors")
                            .child(selectedDoctor.getID()).setValue(selectedDoctor);

                    // redirect back to patient dashboard
                    Intent intent = new Intent(DoctorInfoActivity.this, PatientDashboardActivity.class);
                    intent.putExtra(getString(R.string.user_key), loggedInUser.getID());
                    startActivity(intent);
                } else {
                    Toast.makeText(DoctorInfoActivity.this, "Failed to book appointment!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private int getDayOfWeek(String day) {
        if (day.toLowerCase().contains("sun")) return 1;
        if (day.toLowerCase().contains("mon")) return 2;
        if (day.toLowerCase().contains("tue")) return 3;
        if (day.toLowerCase().contains("wed")) return 4;
        if (day.toLowerCase().contains("thu")) return 5;
        if (day.toLowerCase().contains("fri")) return 6;
        if (day.toLowerCase().contains("sat")) return 7;
        return -1;
    }

    @Override
    public void onClick(View view) {
    }
}