package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;

public class AlertGeneratorTest {

    @Test
    void testDetectRapidDropAlerts() {
        // Create a DataStorage instance
        DataStorage dataStorage = new DataStorage();

        // Add patient data with rapid drop in blood saturation
        dataStorage.addPatientData(1, 98.0, "BloodSaturation", 1L, 120, 80); // Normal saturation
        dataStorage.addPatientData(1, 96.0, "BloodSaturation", 2L, 120, 80); // Slight drop
        dataStorage.addPatientData(1, 88.0, "BloodSaturation", 3L, 120, 80); // Rapid drop

        // Initialize AlertGenerator with the DataStorage
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data for rapid drop alerts
        alertGenerator.evaluateData(dataStorage.getPatient(1));

        // Verify that alerts are triggered for rapid drop
        // Assert the presence of alerts in the triggeredAlerts list
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(alert -> alert.getPatientId() == 1
                        && alert.getCondition().equals("Rapid Drop in Blood Saturation")));
    }
}
