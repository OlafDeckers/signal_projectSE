package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

public class HypotensiveHypoxemiaAlertTest {

    @Test
    void testHypotensiveHypoxemiaAlert() {
        // Create a DataStorage instance
        DataStorage dataStorage = new DataStorage();

        // Add patient data with hypotensive hypoxemia condition
        dataStorage.addPatientData(1, 91.0, "BloodSaturation", 1L, 80, 120); // Low saturation

        // Initialize AlertGenerator with the DataStorage
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data for alerts
        alertGenerator.evaluateData(dataStorage.getPatient(1));

        // Verify that an alert is triggered for hypotensive hypoxemia
        // Assert the presence of an alert in the triggeredAlerts list
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(alert -> alert.getPatientId() == 1
                        && alert.getCondition().equals("Hypotensive Hypoxemia Alert")));
    }
}
