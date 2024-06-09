package com.alerts.Strategy;

import com.alerts.Alert;
import com.alerts.Factory.AlertFactory;
import com.alerts.Factory.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, PatientRecord record, List<Alert> alerts) {
        if ("Systolic".equals(record.getRecordType()) || "Diastolic".equals(record.getRecordType())) {
            if (!checkBloodPressureTrend(patient, alerts)) {
                checkCriticalBloodPressureThreshold(record, alerts);
            }
        }
    }

    private boolean checkBloodPressureTrend(Patient patient, List<Alert> alerts) {
        List<PatientRecord> systolicRecords = getLastNRecords(patient, "Systolic", 3);
        List<PatientRecord> diastolicRecords = getLastNRecords(patient, "Diastolic", 3);

        if (systolicRecords.size() < 3 || diastolicRecords.size() < 3) return false;

        boolean increasingTrend = true;
        boolean decreasingTrend = true;

        for (int i = 1; i < systolicRecords.size(); i++) {
            double prevSystolic = systolicRecords.get(i - 1).getMeasurementValue();
            double currSystolic = systolicRecords.get(i).getMeasurementValue();
            double prevDiastolic = diastolicRecords.get(i - 1).getMeasurementValue();
            double currDiastolic = diastolicRecords.get(i).getMeasurementValue();

            if (currSystolic - prevSystolic < 10 || currDiastolic - prevDiastolic < 10) increasingTrend = false;
            if (prevSystolic - currSystolic < 10 || prevDiastolic - currDiastolic < 10) decreasingTrend = false;
        }

        AlertFactory factory = new BloodPressureAlertFactory();
        if (increasingTrend) {
            alerts.add(factory.createAlert(patient.getPatientId(), "Increasing Blood Pressure Trend", System.currentTimeMillis()));
            return true;
        } else if (decreasingTrend) {
            alerts.add(factory.createAlert(patient.getPatientId(), "Decreasing Blood Pressure Trend", System.currentTimeMillis()));
            return true;
        }

        return false;
    }

    private void checkCriticalBloodPressureThreshold(PatientRecord record, List<Alert> alerts) {
        double systolic = "Systolic".equals(record.getRecordType()) ? record.getMeasurementValue() : 0;
        double diastolic = "Diastolic".equals(record.getRecordType()) ? record.getMeasurementValue() : 0;

        if (systolic > 180 || systolic < 90 || diastolic > 120 || diastolic < 60) {
            AlertFactory factory = new BloodPressureAlertFactory();
            alerts.add(factory.createAlert(record.getPatientId(), "Critical Blood Pressure", record.getTimestamp()));
        }
    }

    private List<PatientRecord> getLastNRecords(Patient patient, String recordType, int n) {
        List<PatientRecord> filteredRecords = patient.getRecords(0, System.currentTimeMillis()).stream()
                .filter(record -> recordType.equals(record.getRecordType()))
                .collect(Collectors.toList());
        return filteredRecords.subList(Math.max(filteredRecords.size() - n, 0), filteredRecords.size());
    }
}
