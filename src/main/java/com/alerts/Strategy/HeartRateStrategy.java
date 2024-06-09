package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.Factory.AlertFactory;
import com.alerts.Factory.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class HeartRateStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, PatientRecord record, List<Alert> alerts) {
        if ("HeartRate".equals(record.getRecordType()) && record.getMeasurementValue() > 100) {
            AlertFactory factory = new ECGAlertFactory();
            alerts.add(factory.createAlert(patient.getPatientId(), "High Heart Rate", record.getTimestamp()));
        }
    }
}
