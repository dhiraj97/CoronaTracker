package com.example.coronatracker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class DeleteFragment extends Fragment {
    CardView cvPatient;
    private EditText txtId;
    private Button btnDelete, btnView;
    private DatabaseHelper dbh;
    private TextView txtIdView, txtFullName, txtAgeGender, txtFullAddress, txtDateOfInfection, txtCurrentStatus;
    private ImageView imgProfile;

    public DeleteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete, container, false);
        txtId = view.findViewById(R.id.txtDeletePatientById);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnView = view.findViewById(R.id.btnView);
        txtIdView = view.findViewById(R.id.txtId);
        txtFullName = view.findViewById(R.id.txtFullName);
        txtAgeGender = view.findViewById(R.id.txtAgeGender);
        txtFullAddress = view.findViewById(R.id.txtFullAddress);
        txtDateOfInfection = view.findViewById(R.id.txtDateOfInfection);
        txtCurrentStatus = view.findViewById(R.id.txtCurrentStatus);
        imgProfile = view.findViewById(R.id.imgProfile);
        cvPatient = view.findViewById(R.id.cvPatient);
        dbh = new DatabaseHelper(getActivity());

        cvPatient.setVisibility(View.GONE);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowToDelete = 0;

                //Get ID of patient that user want to delete
                try {
                    rowToDelete = Integer.parseInt(txtId.getText().toString());
                } catch (Exception e) {

                }

                //Retrieving Record from Database by passing ID
                Cursor cursor = dbh.viewRecord(rowToDelete);

                //If no result from database then toast to user
                if (cursor.getCount() == 0) {
                    cvPatient.setVisibility(View.GONE);
                    txtId.setText("");
                    Toast.makeText(getContext(), "No Record found.", Toast.LENGTH_SHORT).show();
                } else {
                    //Setting data in to Text Views
                    if (cursor.moveToLast()) {
                        Patient patient = new Patient();
                        patient.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        patient.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                        patient.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                        patient.setAge(cursor.getInt(cursor.getColumnIndex("age")));
                        patient.setCity(cursor.getString(cursor.getColumnIndex("city")));
                        patient.setStreetAddress(cursor.getString(cursor.getColumnIndex("streetAddress")));
                        patient.setPostalCode(cursor.getString(cursor.getColumnIndex("postalCode")));
                        patient.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                        patient.setCountry(cursor.getString(cursor.getColumnIndex("country")));
                        patient.setDateOfInfection(cursor.getString(cursor.getColumnIndex("dateOfInfection")));
                        patient.setAlive(cursor.getInt(cursor.getColumnIndex("alive")));
                        patient.setRecovered(cursor.getInt(cursor.getColumnIndex("recovered")));
                        patient.setGender(cursor.getString(cursor.getColumnIndex("gender")));

                        txtIdView.setText("(" + patient.getId() + ")");
                        txtFullName.setText(patient.getLastName() + " " + patient.getFirstName());
                        txtAgeGender.setText(patient.getAge() + ", " + patient.getGender().substring(0, 1).toUpperCase() + patient.getGender().substring(1));
                        txtFullAddress.setText(patient.getStreetAddress() + ", " + patient.getCity() + ", " + patient.getProvince() + ", " + patient.getCountry() + ", " + patient.getPostalCode());
                        txtDateOfInfection.setText("Date of Infection: " + patient.getDateOfInfection());
                        imgProfile.setImageResource(getMapIcon(patient));
                        String mCurrentStatus = "";
                        if (patient.getAlive() == 1) {
                            if (patient.getRecovered() == 1) {
                                mCurrentStatus = "Recovered";
                            } else {
                                mCurrentStatus = "Still Infected";
                            }
                        } else {
                            mCurrentStatus = "Death";
                        }
                        txtCurrentStatus.setText("Current Status: " + mCurrentStatus);
                        cvPatient.setVisibility(View.VISIBLE);
                    }
                    cursor.close();
                    dbh.close();
                }

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rowToDelete = 0, countDeletedRow;

                //Get ID of Patient that user want to delete
                try {
                    rowToDelete = Integer.parseInt(txtId.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: Enter valid input", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Checking if ID is valid
                if (rowToDelete > 0) {
                    //Deleting record from table
                    countDeletedRow = dbh.deleteRecord(rowToDelete);

                    //Validating result and showing toast
                    if (countDeletedRow > 0) {
                        Toast.makeText(getContext(), "Record Deleted Successfully", Toast.LENGTH_SHORT).show();
                        txtId.setText("");
                        cvPatient.setVisibility(View.GONE);
                    }
                    if (countDeletedRow == 0) {
                        Toast.makeText(getContext(), "Error: The record you trying to delete doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error: Enter valid input", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public int getMapIcon(Patient patient) {

        if (patient.getAlive() == 0) {
            return R.drawable.death;
        } else if (patient.getRecovered() == 1) {
            return R.drawable.discharged;
        } else if (patient.getGender().equals("male")) {
            return R.drawable.activemale;
        } else {
            return R.drawable.activefemale;
        }
    }
}
