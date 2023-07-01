package com.example.MedicalClinic;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.ThrowOnExtraProperties;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {
    private Context context;
    private int resource;

    public AppointmentAdapter(Context context, int resource, List<Appointment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    private class ViewHolder {
        TextView doctorName;
        TextView patientName;
        TextView dateTime;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();

            holder.doctorName = (TextView) convertView.findViewById(R.id.textViewDoctorName);
            holder.patientName = (TextView) convertView.findViewById(R.id.textViewPatientName);
            holder.dateTime = (TextView) convertView.findViewById(R.id.textViewDateTime);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.doctorName.setText(getItem(position).getDoctorName());
        holder.patientName.setText(getItem(position).getPatientName());
        holder.dateTime.setText(getItem(position).getDateTime());

        return convertView;
    }
}