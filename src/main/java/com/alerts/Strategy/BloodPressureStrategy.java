package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.Factory.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, PatientRecord record, List<Alert> alerts) {
        if ("Systolic".equals(record.getRecordType()) || "Diastolic".equals(record.getRecordType())) {
            if (isCritical(record)) {
                BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
                alerts.add(factory.createAlert(patient.getPatientId(), "Critical Blood Pressure", record.getTimestamp()));
            }
            checkIncreasingTrend(patient, record, alerts);
            checkDecreasingTrend(patient, record, alerts);
        }
    }

    private boolean isCritical(PatientRecord record) {
        double value = record.getMeasurementValue();
        return ("Systolic".equals(record.getRecordType()) && (value > 180 || value < 90)) ||
               ("Diastolic".equals(record.getRecordType()) && (value > 120 || value < 60));
    }

    private void checkIncreasingTrend(Patient patient, PatientRecord record, List<Alert> alerts) {
        List<PatientRecord> bloodPressureRecords = getRecordsWithinInterval(patient, record.getRecordType(), record.getTimestamp() - 1200000, record.getTimestamp());

        if (bloodPressureRecords.size() < 3) return;

        double first = bloodPressureRecords.get(0).getMeasurementValue();
        double second = bloodPressureRecords.get(1).getMeasurementValue();
        double third = bloodPressureRecords.get(2).getMeasurementValue();

        if ((second - first > 10) && (third - second > 10)) {
            BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
            alerts.add(factory.createAlert(patient.getPatientId(), "Increasing Blood Pressure Trend", record.getTimestamp()));
        }
    }

    private void checkDecreasingTrend(Patient patient, PatientRecord record, List<Alert> alerts) {
        List<PatientRecord> bloodPressureRecords = getRecordsWithinInterval(patient, record.getRecordType(), record.getTimestamp() - 1200000, record.getTimestamp());

        if (bloodPressureRecords.size() < 3) return;

        double first = bloodPressureRecords.get(0).getMeasurementValue();
        double second = bloodPressureRecords.get(1).getMeasurementValue();
        double third = bloodPressureRecords.get(2).getMeasurementValue();

        if ((first - second > 10) && (second - third > 10)) {
            BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
            alerts.add(factory.createAlert(patient.getPatientId(), "Decreasing Blood Pressure Trend", record.getTimestamp()));
        }
    }

    private List<PatientRecord> getRecordsWithinInterval(Patient patient, String recordType, long startTime, long endTime) {
        return patient.getRecords(startTime, endTime).stream()
                .filter(record -> recordType.equals(record.getRecordType()))
                .collect(Collectors.toList());
    }
}
