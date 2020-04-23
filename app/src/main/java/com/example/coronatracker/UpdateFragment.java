package com.example.coronatracker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class UpdateFragment extends Fragment {

    EditText txtId,txtFirstName, txtLastName, txtAge, txtStreetAddress, txtCity,txtProvince,
            txtCountry, txtPostalCode, txtLatitude, txtLongitude,
            txtDateOfInfection;
    RadioGroup aliveGroup, recoveredGroup, genderGroup;
    RadioButton btnAlivePositive, btnAliveNegative, btnRecoveredPositive, btnRecoveredNegative;
    int isAlive = 1;
    int isRecovered = 1;
    String gender;
    Button btnSubmit;
    DatabaseHelper dbh;

    public UpdateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update,container, false);

        initialize(view);

        btnSubmit.setText("Update");

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
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.male:
                        gender = "male";
                        break;
                    case R.id.female:
                        gender = "female";
                        break;
                    case R.id.other:
                        gender = "other";
                        break;
                }
            }
        });


        dbh = new DatabaseHelper(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Patient patient = new Patient(Integer.parseInt(txtId.getText().toString()), txtFirstName.getText().toString(), txtLastName.getText().toString(),Integer.parseInt(txtAge.getText().toString()),txtStreetAddress.getText().toString(),txtCity.getText().toString(),txtProvince.getText().toString(),txtCountry.getText().toString(),txtPostalCode.getText().toString(),Double.parseDouble(txtLatitude.getText().toString()),Double.parseDouble(txtLongitude.getText().toString()),txtDateOfInfection.getText().toString(),isAlive,isRecovered, gender);
                int numOfRows = dbh.updatePatient(patient);
                if (numOfRows > 0) {
                    Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error Updating", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //Populates the fields wrt the id entered
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearFields();
                if (charSequence.length() != 0) {
                    dbh = new DatabaseHelper(getActivity());
                    Cursor cursor = dbh.viewRecord(Integer.parseInt(txtId.getText().toString()));
                    if (cursor == null) {
                        Toast.makeText(getContext(), "No record Found", Toast.LENGTH_SHORT).show();
                    } else {
                        if (cursor.moveToFirst()) {
                            do {
                                txtFirstName.setText("" + cursor.getString(cursor.getColumnIndex("firstName")));
                                txtLastName.setText("" + cursor.getString(cursor.getColumnIndex("lastName")));
                                txtAge.setText(""+cursor.getInt(cursor.getColumnIndex("age")));
                                txtStreetAddress.setText(""+cursor.getString(cursor.getColumnIndex("streetAddress")));
                                txtCity.setText(""+cursor.getString(cursor.getColumnIndex("city")));
                                txtProvince.setText(""+cursor.getString(cursor.getColumnIndex("province")));
                                txtCountry.setText(""+cursor.getString(cursor.getColumnIndex("country")));
                                txtPostalCode.setText(""+cursor.getString(cursor.getColumnIndex("postalCode")));
                                txtLatitude.setText(""+cursor.getDouble(cursor.getColumnIndex("latitude")));
                                txtLongitude.setText(""+cursor.getDouble(cursor.getColumnIndex("longitude")));
                                txtDateOfInfection.setText(""+cursor.getString(cursor.getColumnIndex("dateOfInfection")));

                                switch (cursor.getInt(cursor.getColumnIndex("alive"))) {
                                    case 0:
                                        btnAliveNegative.setChecked(true);
                                        break;
                                    case 1:
                                        btnAlivePositive.setChecked(true);
                                        break;
                                }
                                switch (cursor.getInt(cursor.getColumnIndex("recovered"))) {
                                    case 0:
                                        btnRecoveredNegative.setChecked(true);
                                        break;
                                    case 1:
                                        btnRecoveredPositive.setChecked(true);
                                        break;
                                }
                            } while (cursor.moveToNext());
                        }
                    }
                    cursor.close();
                    dbh.close();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtDateOfInfection.setInputType(InputType.TYPE_NULL);
        txtDateOfInfection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            txtDateOfInfection.setText(dayOfMonth + "/" + (monthOfYear + 1)
                    + "/" + year);
        }
    };
    private void clearFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtAge.setText("");
        txtStreetAddress.setText("");
        txtCity.setText("");
        txtProvince.setText("");
        txtCountry.setText("");
        txtPostalCode.setText("");
        txtLatitude.setText("");
        txtLongitude.setText("");
        txtDateOfInfection.setText("");

    }
    private void initialize(View view) {
        txtId = view.findViewById(R.id.txtUpdateId);
        txtFirstName = view.findViewById(R.id.txtFullName);
        txtLastName = view.findViewById(R.id.txtLastName);
        txtAge = view.findViewById(R.id.txtAgeGender);
        txtStreetAddress = view.findViewById(R.id.txtStreetAddress);
        txtCity = view.findViewById(R.id.txtFullAddress);
        txtProvince = view.findViewById(R.id.txtProvince);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtPostalCode = view.findViewById(R.id.txtPostalCode);
        txtLatitude = view.findViewById(R.id.txtLatitude);
        txtLongitude = view.findViewById(R.id.txtLongitude);
        txtDateOfInfection = view.findViewById(R.id.txtDateOfInfection);

        btnSubmit = view.findViewById(R.id.btnSubmit);

        aliveGroup = view.findViewById(R.id.aliveRadioGroup);
        recoveredGroup = view.findViewById(R.id.recoveredRadioGroup);
        genderGroup = view.findViewById(R.id.genderRadioGroup);


        btnAlivePositive = view.findViewById(R.id.alivePositive);
        btnAliveNegative = view.findViewById(R.id.aliveNegative);
        btnRecoveredPositive = view.findViewById(R.id.recoveredPositive);
        btnRecoveredNegative = view.findViewById(R.id.recoveredNegative);

    }
}
