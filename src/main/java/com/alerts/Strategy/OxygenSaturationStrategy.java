package com.alerts.Strategy;

import com.alerts.Alert;

import com.alerts.Factory.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, PatientRecord record, List<Alert> alerts) {
        if ("Saturation".equals(record.getRecordType())) {
            double value = record.getMeasurementValue();
            if (value < 92) {
                BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
                alerts.add(factory.createAlert(patient.getPatientId(), "Low Blood Saturation", record.getTimestamp()));
            }
            checkRapidSaturationDrop(patient, record, alerts);
        }
    }

    private void checkRapidSaturationDrop(Patient patient, PatientRecord record, List<Alert> alerts) {
        List<PatientRecord> saturationRecords = getRecordsWithinInterval(patient, "Saturation", record.getTimestamp() - 600000, record.getTimestamp());

        if (saturationRecords.size() < 2) return;

        double initial = saturationRecords.get(0).getMeasurementValue();
        double current = saturationRecords.get(saturationRecords.size() - 1).getMeasurementValue();

        if (initial - current >= 5) {
            BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
            alerts.add(factory.createAlert(patient.getPatientId(), "Rapid Blood Saturation Drop", record.getTimestamp()));
        }
    }

    private List<PatientRecord> getRecordsWithinInterval(Patient patient, String recordType, long startTime, long endTime) {
        return patient.getRecords(startTime, endTime).stream()
                .filter(record -> recordType.equals(record.getRecordType()))
                .collect(Collectors.toList());
    }
}
