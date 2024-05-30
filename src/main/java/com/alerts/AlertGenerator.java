package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    @SuppressWarnings("unused")
    private DataStorage dataStorage;
    private List<Alert> alerts;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
    }

    /**
     * Evaluates patient data to check for conditions that may trigger alerts.
     * @param patient The patient whose data needs to be evaluated.
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());

        for (PatientRecord record : records) {
            evaluateHeartRate(patient, record);
            evaluateBloodPressure(patient, record);
            evaluateBloodSaturation(patient, record);
            evaluateECG(patient, record);
            evaluateHypotensiveHypoxemia(patient, records);
        }

        
    }

    private void evaluateHeartRate(Patient patient, PatientRecord record) {
        if ("HeartRate".equals(record.getRecordType()) && record.getMeasurementValue() > 100) {
            triggerAlert(patient.getPatientId(), "High Heart Rate", record.getTimestamp(), "Heart rate exceeds 100 bpm.");
        }
    }

    private void evaluateBloodPressure(Patient patient, PatientRecord record) {
        if ("Systolic".equals(record.getRecordType()) || "Diastolic".equals(record.getRecordType())) {
            checkBloodPressureTrend(patient, record);
            checkCriticalBloodPressureThreshold(patient, record);
        }
    }

    private void checkBloodPressureTrend(Patient patient, PatientRecord record) {
        List<PatientRecord> systolicRecords = getLastNRecords(patient, "Systolic", 3);
        List<PatientRecord> diastolicRecords = getLastNRecords(patient, "Diastolic", 3);

        if (systolicRecords.size() < 3 || diastolicRecords.size() < 3) return;

        boolean increasingTrend = true;
        boolean decreasingTrend = true;

        for (int i = 1; i < systolicRecords.size(); i++) {
            double previousSystolic = systolicRecords.get(i - 1).getMeasurementValue();
            double currentSystolic = systolicRecords.get(i).getMeasurementValue();
            double previousDiastolic = diastolicRecords.get(i - 1).getMeasurementValue();
            double currentDiastolic = diastolicRecords.get(i).getMeasurementValue();

            if (currentSystolic - previousSystolic <= 10 || currentDiastolic - previousDiastolic <= 10) increasingTrend = false;
            if (previousSystolic - currentSystolic <= 10 || previousDiastolic - currentDiastolic <= 10) decreasingTrend = false;
        }

        if (increasingTrend) {
            triggerAlert(patient.getPatientId(), "Increasing Blood Pressure Trend", record.getTimestamp(), "Blood pressure increasing trend over 3 consecutive readings.");
        } else if (decreasingTrend) {
            triggerAlert(patient.getPatientId(), "Decreasing Blood Pressure Trend", record.getTimestamp(), "Blood pressure decreasing trend over 3 consecutive readings.");
        }
    }

    private void checkCriticalBloodPressureThreshold(Patient patient, PatientRecord record) {
        double systolic = 0;
        double diastolic = 0;
        if ("Systolic".equals(record.getRecordType())) {
            systolic = record.getMeasurementValue();
        } else if ("Diastolic".equals(record.getRecordType())) {
            diastolic = record.getMeasurementValue();
        }

        if (systolic > 180 || systolic < 90 || diastolic > 120 || diastolic < 60) {
            triggerAlert(patient.getPatientId(), "Critical Blood Pressure", record.getTimestamp(), "Critical blood pressure levels detected.");
        }
    }

    private void evaluateBloodSaturation(Patient patient, PatientRecord record) {
        if ("Saturation".equals(record.getRecordType())) {
            double value = record.getMeasurementValue();
            if (value < 92) {
                triggerAlert(patient.getPatientId(), "Low Blood Saturation", record.getTimestamp(), "Blood saturation below 92%.");
            }
            checkRapidSaturationDrop(patient, record);
        }
    }

    private void checkRapidSaturationDrop(Patient patient, PatientRecord record) {
        List<PatientRecord> saturationRecords = getRecordsWithinInterval(patient, "Saturation", record.getTimestamp() - 600000, record.getTimestamp());

        if (saturationRecords.size() < 2) return;

        double initial = saturationRecords.get(0).getMeasurementValue();
        double current = saturationRecords.get(saturationRecords.size() - 1).getMeasurementValue();

        if (initial - current >= 5) {
            triggerAlert(patient.getPatientId(), "Rapid Blood Saturation Drop", record.getTimestamp(), "Blood saturation dropped by 5% or more within 10 minutes.");
        }
    }

    private void evaluateECG(Patient patient, PatientRecord record) {
        if ("ECG".equals(record.getRecordType()) && record.getMeasurementValue() > 1.5) {
            triggerAlert(patient.getPatientId(), "Abnormal ECG", record.getTimestamp(), "ECG value exceeds 1.5 mV.");
        }
    }

    private void evaluateHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        boolean lowBloodPressure = false;
        boolean lowBloodSaturation = false;

        for (PatientRecord record : records) {
            if ("Systolic".equals(record.getRecordType()) && record.getMeasurementValue() < 90) {
                lowBloodPressure = true;
            }
            if ("Saturation".equals(record.getRecordType()) && record.getMeasurementValue() < 92) {
                lowBloodSaturation = true;
            }
        }

        if (lowBloodPressure && lowBloodSaturation) {
            triggerAlert(patient.getPatientId(), "Hypotensive Hypoxemia", System.currentTimeMillis(), "Combined low blood pressure and low blood oxygen saturation detected.");
        }
    }

    /**
     * Triggers an alert if a specified condition is met.
     * @param patientId The ID of the patient for whom the alert is being triggered.
     * @param condition The condition that triggered the alert.
     * @param timestamp The time at which the alert was triggered.
     * @param message A message describing the alert condition.
     */
    private void triggerAlert(int patientId, String condition, long timestamp, String message) {
        Alert alert = new Alert(String.valueOf(patientId), condition, timestamp);
        alerts.add(alert);
        System.out.println("Alert triggered: " + message);
    }

    /**
     * Retrieves the list of alerts.
     * @return A list of alerts.
     */
    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }

    private List<PatientRecord> getLastNRecords(Patient patient, String recordType, int n) {
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : records) {
            if (recordType.equals(record.getRecordType())) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords.subList(Math.max(filteredRecords.size() - n, 0), filteredRecords.size());
    }

    private List<PatientRecord> getRecordsWithinInterval(Patient patient, String recordType, long startTime, long endTime) {
        List<PatientRecord> records = patient.getRecords(startTime, endTime);
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : records) {
            if (recordType.equals(record.getRecordType())) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
}
