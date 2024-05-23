package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

public class BloodSaturationAlertTest {

    @Test
    void testLowSaturationAlert() {
        // Create a DataStorage instance
        DataStorage dataStorage = new DataStorage();

        // Add patient data with low saturation level
        dataStorage.addPatientData(1, 90.0, "BloodSaturation", 1L, 0, 0); // Low saturation level

        // Initialize AlertGenerator with the DataStorage
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data for alerts
        alertGenerator.evaluateData(dataStorage.getPatient(1));

        // Verify that an alert is triggered for low saturation
        // Assert the presence of an alert in the triggeredAlerts list
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(alert -> alert.getPatientId() == 1 && alert.getCondition().equals("Low Blood Saturation")));
    }

    @Test
    void testRapidDropAlert() {
        // Create a DataStorage instance
        DataStorage dataStorage = new DataStorage();

        // Add patient data with rapid drop in saturation level
        dataStorage.addPatientData(1, 95.0, "BloodSaturation", 1L, 0, 0); // Initial reading
        dataStorage.addPatientData(1, 88.0, "BloodSaturation", 2L, 0, 0); // Drop by more than 5% within 10 minutes

        // Initialize AlertGenerator with the DataStorage
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data for alerts
        alertGenerator.evaluateData(dataStorage.getPatient(1));

        // Verify that an alert is triggered for rapid drop in saturation
        // Assert the presence of an alert in the triggeredAlerts list
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(alert -> alert.getPatientId() == 1
                        && alert.getCondition().equals("Rapid Drop in Blood Saturation")));
    }
}
