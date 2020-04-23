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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class UpdateFragment extends Fragment {

    EditText txtId, txtFirstName, txtLastName, txtAge, txtStreetAddress, txtCity, txtProvince,
            txtCountry, txtPostalCode, txtLatitude, txtLongitude,
            txtDateOfInfection;
    RadioGroup aliveGroup, recoveredGroup, genderGroup;
    RadioButton rbAlivePositive, rbAliveNegative, rbRecoveredPositive, rbRecoveredNegative, rbMale, rbFemale;
    LinearLayout linearAutoComplete, linearLayEnterDataFragment;
    TextView tvNoRecords;
    int isAlive = 1;
    int isRecovered = 1;
    String gender = "male";
    Button btnSubmit;
    DatabaseHelper dbh;

    public UpdateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        //Initializing all Widget.
        initialize(view);

        //Changing Text of Button
        btnSubmit.setText("Update");

        //Adding listener to all radio button
        addRadioButtonListener();

        dbh = new DatabaseHelper(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numOfRows = 0;
                if (validateInput()) {
                    Patient patient = new Patient(Integer.parseInt(txtId.getText().toString()), txtFirstName.getText().toString(), txtLastName.getText().toString(), Integer.parseInt(txtAge.getText().toString()), txtStreetAddress.getText().toString(), txtCity.getText().toString(), txtProvince.getText().toString(), txtCountry.getText().toString(), txtPostalCode.getText().toString(), Double.parseDouble(txtLatitude.getText().toString()), Double.parseDouble(txtLongitude.getText().toString()), txtDateOfInfection.getText().toString(), isAlive, isRecovered, gender);
                    numOfRows = dbh.updatePatient(patient);
                }
                if (numOfRows > 0) {
                    Toast.makeText(getContext(), "Successfully Updated.", Toast.LENGTH_SHORT).show();
                    clearFields();
                    txtId.setText("");
                } else {
                    Toast.makeText(getContext(), "Error: Enter valid input", Toast.LENGTH_SHORT).show();
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

                    Cursor cursor = dbh.viewRecord(Integer.parseInt(txtId.getText().toString()));
                    if (cursor.getCount() == 0 || cursor == null) {
                        linearLayEnterDataFragment.setVisibility(View.GONE);
                        tvNoRecords.setVisibility(View.VISIBLE);
                    } else {
                        if (cursor.moveToLast()) {
                            txtFirstName.setText("" + cursor.getString(cursor.getColumnIndex("firstName")));
                            txtLastName.setText("" + cursor.getString(cursor.getColumnIndex("lastName")));
                            txtAge.setText("" + cursor.getInt(cursor.getColumnIndex("age")));
                            txtStreetAddress.setText("" + cursor.getString(cursor.getColumnIndex("streetAddress")));
                            txtCity.setText("" + cursor.getString(cursor.getColumnIndex("city")));
                            txtProvince.setText("" + cursor.getString(cursor.getColumnIndex("province")));
                            txtCountry.setText("" + cursor.getString(cursor.getColumnIndex("country")));
                            txtPostalCode.setText("" + cursor.getString(cursor.getColumnIndex("postalCode")));
                            txtLatitude.setText("" + cursor.getDouble(cursor.getColumnIndex("latitude")));
                            txtLongitude.setText("" + cursor.getDouble(cursor.getColumnIndex("longitude")));
                            txtDateOfInfection.setText("" + cursor.getString(cursor.getColumnIndex("dateOfInfection")));

                            isAlive = cursor.getInt(cursor.getColumnIndex("alive"));
                            if (isAlive == 0) {
                                rbAliveNegative.setChecked(true);
                            } else {
                                rbAlivePositive.setChecked(true);
                            }

                            isRecovered = cursor.getInt(cursor.getColumnIndex("recovered"));
                            if (isRecovered == 0) {
                                rbRecoveredNegative.setChecked(true);
                            } else {
                                rbRecoveredPositive.setChecked(true);
                            }

                            gender = cursor.getString(cursor.getColumnIndex("gender"));
                            if (gender.equals("male")) {
                                rbMale.setChecked(true);
                            } else {
                                rbFemale.setChecked(true);
                            }
                            linearLayEnterDataFragment.setVisibility(View.VISIBLE);
                            tvNoRecords.setVisibility(View.GONE);
                        }

                    }
                    cursor.close();
                    dbh.close();
                } else {
                    linearLayEnterDataFragment.setVisibility(View.GONE);
                    tvNoRecords.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void addRadioButtonListener() {

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
                }
            }
        });
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

    //clearing all fields
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

    //Initialising all widget
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


        rbAlivePositive = view.findViewById(R.id.alivePositive);
        rbAliveNegative = view.findViewById(R.id.aliveNegative);
        rbRecoveredPositive = view.findViewById(R.id.recoveredPositive);
        rbRecoveredNegative = view.findViewById(R.id.recoveredNegative);
        rbMale = view.findViewById(R.id.male);
        rbFemale = view.findViewById(R.id.female);

        linearAutoComplete = view.findViewById(R.id.linearAutoComplete);
        linearLayEnterDataFragment = view.findViewById(R.id.linearLayEnterDataFragment);
        linearAutoComplete.setVisibility(View.GONE);
        linearLayEnterDataFragment.setVisibility(View.GONE);
        tvNoRecords = view.findViewById(R.id.tvNoRecords);
        tvNoRecords.setVisibility(View.GONE);
    }

    //Validating all input using Helper class
    private boolean validateInput() {
        if (Helper.ValidateTextStringData(txtFirstName)
                && Helper.ValidateTextStringData(txtLastName)
                && Helper.ValidateTextStringData(txtStreetAddress)
                && Helper.ValidateTextStringData(txtCity)
                && Helper.ValidateTextStringData(txtProvince)
                && Helper.ValidateTextStringData(txtCountry)
                && Helper.ValidateTextStringData(txtPostalCode)
                && Helper.ValidateTextStringData(txtDateOfInfection)
                && Helper.ValidateTextIntegerData(txtAge)
        ) {
            return true;
        } else {
            Toast.makeText(getContext(), "Error: Enter valid input", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
