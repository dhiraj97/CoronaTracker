package com.example.coronatracker;

import android.widget.EditText;

//Class which contains reusable methods, Available for all script.
public class Helper {

    //Validate String empty input
    public static boolean ValidateTextStringData(EditText etText) {
        return etText.getText().toString() != "";
    }

    //Validate Int input
    public static boolean ValidateTextIntegerData(EditText etText) {
        try {
            Integer.parseInt(etText.getText().toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Methods to retrieve Image Id of Patient Type
    public static int getMapIcon(Patient patient) {

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
