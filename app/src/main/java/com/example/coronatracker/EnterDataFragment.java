package com.example.coronatracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EnterDataFragment extends Fragment {
    EditText txtFirstName, txtLastName, txtAge, txtStreetAddress, txtCity,
            txtCountry, txtPostalCode, txtLatitude, txtLongitude,
            txtDateOfInfection;
    RadioGroup aliveGroup, recoveredGroup;
    int isAlive = 1;
    int isRecovered = 1;
    Boolean insertStat;
    Button btnSubmit;
    DatabaseHelper dbh;

    public EnterDataFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_data,container, false);

        initialize(view);

        aliveGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.alivePositive:
                        isAlive = 1;
                        break;
                    case R.id.aliveNegative:
                        isAlive = 0;
                        break;
                }
            }
        });
        recoveredGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.recoveredPositive:
                        isRecovered = 1;
                        break;
                    case R.id.recoveredNegative:
                        isRecovered = 0;
                        break;
                }
            }
        });

        dbh = new DatabaseHelper(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int patientId = 0;
                Patient patient = new Patient(patientId, txtFirstName.getText().toString(), txtLastName.getText().toString(),Integer.parseInt(txtAge.getText().toString()),txtStreetAddress.getText().toString(),txtCity.getText().toString(),txtCountry.getText().toString(),txtPostalCode.getText().toString(),Double.parseDouble(txtLatitude.getText().toString()),Double.parseDouble(txtLongitude.getText().toString()),txtDateOfInfection.getText().toString(),isAlive,isRecovered);

                insertStat = dbh.insertPatient(patient);
                if (insertStat) {
                    Toast.makeText(getActivity(), "Record Added Successfully", Toast.LENGTH_SHORT).show();
                } else if (!insertStat) {
                    Toast.makeText(getActivity(), "Record Not Added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private void initialize(View view) {
        txtFirstName = view.findViewById(R.id.txtFirstName);
        txtLastName = view.findViewById(R.id.txtLastName);
        txtAge = view.findViewById(R.id.txtAge);
        txtStreetAddress = view.findViewById(R.id.txtStreetAddress);
        txtCity = view.findViewById(R.id.txtCity);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtPostalCode = view.findViewById(R.id.txtPostalCode);
        txtLatitude = view.findViewById(R.id.txtLatitude);
        txtLongitude = view.findViewById(R.id.txtLongitude);
        txtDateOfInfection = view.findViewById(R.id.txtDateOfInfection);

        btnSubmit = view.findViewById(R.id.btnSubmit);

        aliveGroup = view.findViewById(R.id.aliveRadioGroup);
        recoveredGroup = view.findViewById(R.id.recoveredRadioGroup);
    }
}
