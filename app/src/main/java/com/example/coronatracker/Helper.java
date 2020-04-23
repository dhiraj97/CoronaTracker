package com.example.coronatracker;

import android.widget.EditText;

public class Helper {

    public static boolean ValidateTextStringData(EditText etText) {
        return etText.getText().toString() != "";
    }

    public static boolean ValidateTextIntegerData(EditText etText) {
        try {
            Integer.parseInt(etText.getText().toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
