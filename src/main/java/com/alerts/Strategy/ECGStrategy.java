package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.Factory.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class ECGStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, PatientRecord record, List<Alert> alerts) {
        if ("ECG".equals(record.getRecordType()) && record.getMeasurementValue() > 1.5) {
            ECGAlertFactory factory = new ECGAlertFactory();
            alerts.add(factory.createAlert(patient.getPatientId(), "Abnormal ECG", record.getTimestamp()));
        }
    }
}
