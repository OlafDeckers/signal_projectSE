package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

public class ECGDataAlertTest {

    @Test
    void testAbnormalHeartRateAlert() {
        // Create a DataStorage instance
        DataStorage dataStorage = new DataStorage();

        // Add patient data with abnormal heart rate
        dataStorage.addPatientData(1, 45.0, "HeartRate", 1L, 0, 0); // Below 50 bpm
        dataStorage.addPatientData(1, 105.0, "HeartRate", 2L, 0, 0); // Above 100 bpm

        // Initialize AlertGenerator with the DataStorage
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data for alerts
        alertGenerator.evaluateData(dataStorage.getPatient(1));

        // Verify that alerts are triggered for abnormal heart rate
        // Assert the presence of alerts in the triggeredAlerts list
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(alert -> alert.getPatientId() == 1
                        && alert.getCondition().equals("Abnormal Heart Rate Alert")));
    }

    @Test
    void testIrregularBeatAlert() {
        // Create a DataStorage instance
        DataStorage dataStorage = new DataStorage();

        // Add patient data with abnormal heart rate
        dataStorage.addPatientData(1, 45.0, "ECG", 1L, 0, 0); // Below 50 bpm
        dataStorage.addPatientData(1, 105.0, "ECG", 2L, 0, 0); // Above 100 bpm

        // Initialize AlertGenerator with the DataStorage
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data for alerts
        alertGenerator.evaluateData(dataStorage.getPatient(1));

        // Verify that an alert is triggered for irregular beat patterns
        // Assert the presence of an alert in the triggeredAlerts list
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(alert -> alert.getPatientId() == 1 && alert.getCondition().equals("Irregular Beat Alert")));
    }
}
