package com.alerts.Factory;

import com.alerts.Alert;


public class BloodPressureAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp, "High");
    }
}
