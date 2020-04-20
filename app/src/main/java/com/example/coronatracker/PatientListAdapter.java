package com.example.coronatracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coronatracker.R;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Patient> mList;

    public PatientListAdapter(List<Patient> list, Context ctx) {
        super();
        mList = list;
    }

    //Provide a direct reference to each of the views within a data item
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtId, txtFirstName, txtLastName, txtAge, txtCity,
                txtCountry, txtDateOfInfection, txtRecovered, txtAlive;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtFirstName = itemView.findViewById(R.id.txtFirstName);
            txtLastName = itemView.findViewById(R.id.txtLastName);
            txtAge = itemView.findViewById(R.id.txtAge);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtCountry = itemView.findViewById(R.id.txtCountry);
            txtDateOfInfection = itemView.findViewById(R.id.txtDateOfInfection);
            txtAlive = itemView.findViewById(R.id.txtAlive);
            txtRecovered = itemView.findViewById(R.id.txtRecovered);
        }

    }

    @NonNull
    @Override
    //Usually involves inflating a layout from XML and returning the holder
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    //Involves populating data into the item through holder
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Patient patient = mList.get(position);
        ((ViewHolder) holder).txtId.setText("Id: " + patient.getId());
        ((ViewHolder) holder).txtFirstName.setText("First Name: " + patient.getFirstName());
        ((ViewHolder) holder).txtLastName.setText("Last Name: " + patient.getLastName());
        ((ViewHolder) holder).txtAge.setText("Age: " + patient.getAge());
        ((ViewHolder) holder).txtCity.setText("City: " + patient.getCity());
        ((ViewHolder) holder).txtCountry.setText("Country: " + patient.getCountry());
        ((ViewHolder) holder).txtDateOfInfection.setText("Date of Infection: " + patient.getDateOfInfection());
        ((ViewHolder) holder).txtAlive.setText("Alive: " + patient.getAlive());
        ((ViewHolder) holder).txtRecovered.setText("Recovered: " + patient.getRecovered());
    }


    @Override
    //Returns the total count of items in the list
    public int getItemCount() {
        return mList.size();
    }
}
