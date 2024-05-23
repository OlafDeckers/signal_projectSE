package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.ArrayList;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.triggeredAlerts = new ArrayList<>();
    }

    /**
     * Retrieves the list of triggered alerts.
     *
     * @return the list of triggered alerts
     */
    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    private void detectTrendAlerts(Patient patient, List<PatientRecord> records) {

        if (records.size() < 3) {
            // Not enough records for trend analysis
            return;
        }

        for (int i = 2; i < records.size(); i++) {
            PatientRecord current = records.get(i);
            PatientRecord previous = records.get(i - 1);
            PatientRecord prevPrev = records.get(i - 2);

            double currentSystolic = current.getSystolicBloodPressure();
            double prevSystolic = previous.getSystolicBloodPressure();
            double prevPrevSystolic = prevPrev.getSystolicBloodPressure();

            double currentDiastolic = current.getDiastolicBloodPressure();
            double prevDiastolic = previous.getDiastolicBloodPressure();
            double prevPrevDiastolic = prevPrev.getDiastolicBloodPressure();

            // Checking for increasing trend
            if (currentSystolic >= prevSystolic + 10 && prevSystolic >= prevPrevSystolic + 10) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Increasing Blood Pressure Trend", current.getTimestamp()));
            }

            if (currentDiastolic >= prevDiastolic + 10 && prevDiastolic >= prevPrevDiastolic + 10) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Increasing Blood Pressure Trend", current.getTimestamp()));
            }

            // Checking for decreasing trend
            if (currentSystolic <= prevSystolic - 10 && prevSystolic <= prevPrevSystolic - 10) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Decreasing Blood Pressure Trend", current.getTimestamp()));
            }

            if (currentDiastolic <= prevDiastolic - 10 && prevDiastolic <= prevPrevDiastolic - 10) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Decreasing Blood Pressure Trend", current.getTimestamp()));
            }
        }
    }

    private void checkCriticalThresholds(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            double systolic = record.getSystolicBloodPressure();
            double diastolic = record.getDiastolicBloodPressure();

            // Checking for systolic blood pressure above 180 mmHg or below 90 mmHg
            if (systolic > 180 || systolic < 90) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Critical Systolic Blood Pressure", record.getTimestamp()));
            }

            // Checking for diastolic blood pressure above 120 mmHg or below 60 mmHg
            if (diastolic > 120 || diastolic < 60) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Critical Diastolic Blood Pressure", record.getTimestamp()));
            }
        }
    }

    private void detectLowSaturationAlerts(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            double saturation = record.getBloodSaturation();

            // Checking for low saturation below 92%
            if (saturation < 92) {
                triggerAlert(new Alert(patient.getPatientId(), "Low Blood Saturation", record.getTimestamp()));
            }
        }
    }

    public void detectRapidDropAlerts(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (int i = 1; i < records.size(); i++) {
            PatientRecord current = records.get(i);
            PatientRecord previous = records.get(i - 1);

            double currentSaturation = current.getBloodSaturation();
            double prevSaturation = previous.getBloodSaturation();

            long currentTime = current.getTimestamp();
            long prevTime = previous.getTimestamp();

            // Checking for a drop of 5% or more within a 10-minute interval
            if (prevSaturation - currentSaturation >= 5 && (currentTime - prevTime) <= 600000) {
                triggerAlert(
                        new Alert(patient.getPatientId(), "Rapid Drop in Blood Saturation", current.getTimestamp()));
            }
        }
    }

    private void detectHypotensiveHypoxemiaAlerts(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            double systolicBP = record.getSystolicBloodPressure();
            double saturation = record.getBloodSaturation();

            // Checking for both conditions: systolic BP below 90 mmHg and saturation below
            // 92%
            if (systolicBP < 90 && saturation < 92) {
                triggerAlert(new Alert(patient.getPatientId(), "Hypotensive Hypoxemia Alert", record.getTimestamp()));
            }
        }
    }

    private void detectAbnormalHeartRateAlerts(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            double heartRate = record.getHeartRate();

            // Checking for heart rate below 50 bpm or above 100 bpm
            if (heartRate < 50 || heartRate > 100) {
                triggerAlert(new Alert(patient.getPatientId(), "Abnormal Heart Rate Alert", record.getTimestamp()));
            }
        }
    }

    private void detectIrregularBeatAlerts(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        for (PatientRecord record : records) {
            // Assuming there is a method to detect irregular beat patterns in the record
            boolean hasIrregularBeat = record.hasIrregularBeat();

            // Triggering alert if irregular beat pattern detected
            if (hasIrregularBeat) {
                triggerAlert(new Alert(patient.getPatientId(), "Irregular Beat Alert", record.getTimestamp()));
            }
        }
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        long endTime = patient.getMostRecentRecordTimestamp();// Current time
        long startTime = patient.getNewestTimeStamp(); // 10 minutes before the current time

        List<PatientRecord> records = patient.getRecords(startTime, endTime);

        if (!records.isEmpty()) {
            if ("BloodPressure".equals(records.get(0).getRecordType())) {
                detectTrendAlerts(patient, records);
                checkCriticalThresholds(patient);
            }

            if ("BloodSaturation".equals(records.get(0).getRecordType())) {
                detectLowSaturationAlerts(patient);
                detectRapidDropAlerts(patient);
                detectHypotensiveHypoxemiaAlerts(patient);
            }
               
            if ("HeartRate".equals(records.get(0).getRecordType())) {
                detectAbnormalHeartRateAlerts(patient);
            }

            if ("ECG".equals(records.get(0).getRecordType())) {
                detectIrregularBeatAlerts(patient);
            }
            // Check for Blood Saturation Alerts

            // Check for ECG Alerts

            // Check for Combined Alert: Hypotensive Hypoxemia
        }
    }

    /**
     * Triggers an alert for the monitoring system and adds it to the list of
     * triggered alerts.
     * This method can be extended to notify medical staff, log the alert, or
     * perform other actions.
     * The method currently assumes that the alert information is fully formed when
     * passed as an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Print the alert information to the console
        System.out.println("Alert triggered for Patient " + alert.getPatientId() + ": "
                + alert.getCondition() + " at " + alert.getTimestamp());

        // Add the triggered alert to the list of triggered alerts
        triggeredAlerts.add(alert);
    }

    /**
     * Triggers a new alert based on external input from nurses or patients.
     *
     * @param patientId the ID of the patient for whom the alert is triggered
     * @param condition the condition associated with the alert
     * @param timestamp the time at which the alert was triggered
     */
    public void triggerNewAlert(int patientId, String condition, long timestamp) {
        // Retrieve the patient from the data storage
        Patient patient = dataStorage.getPatient(patientId);

        // Check if the patient exists
        if (patient != null) {
            // Create a new alert and trigger it
            Alert alert = new Alert(patientId, condition, timestamp);
            triggeredAlerts.add(alert);
            triggerAlert(alert);
        } else {
            // Patient not found, handle accordingly (e.g., log error)
            System.err.println("Patient with ID " + patientId + " not found.");
        }
    }

    /**
     * Untriggers an existing triggered alert based on external events.
     *
     * @param patientId the ID of the patient for whom the alert is untriggered
     * @param condition the condition associated with the triggered alert to be
     *                  untriggered
     */
    public void untriggerAlert(int patientId, String condition) {
        triggeredAlerts.removeIf(alert -> alert.getPatientId() == patientId && alert.getCondition().equals(condition));
    }
}
