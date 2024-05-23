package data_management;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

public class BloodPressureAlertTest {

        @Test
        void testIncreasingSystolicBloodPressureTrend() {
                // Create a DataStorage instance
                DataStorage dataStorage = new DataStorage();
                long startTime = System.currentTimeMillis();
                // Add patient data with increasing systolic blood pressure trend
                dataStorage.addPatientData(1, 120.0, "BloodPressure", startTime, 80, 100); // Initial reading
                dataStorage.addPatientData(1, 130.0, "BloodPressure", startTime + 1, 90, 110); // Increasing by more
                                                                                               // than
                // 10 mmHg
                dataStorage.addPatientData(1, 140.0, "BloodPressure", startTime + 2, 100, 120); // Increasing by more
                                                                                                // than
                                                                                                // 10 mmHg

                // Initialize AlertGenerator with the DataStorage
                AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

                // Evaluate data for alerts
                alertGenerator.evaluateData(dataStorage.getPatient(1));

                // Get the list of triggered alerts
                List<Alert> triggeredAlerts = alertGenerator.getTriggeredAlerts();

                // Check if any alert matches the condition for increasing systolic blood
                // pressure trend
                boolean hasPatient1Alert = triggeredAlerts.stream().anyMatch(alert -> alert.getPatientId() == 1);
                boolean hasIncreasingSystolicTrendAlert = triggeredAlerts.stream()
                                .anyMatch(alert -> alert.getPatientId() == 1
                                                && alert.getCondition().equals("Increasing Blood Pressure Trend"));

                // Assert that an alert is triggered for increasing systolic blood pressure
                // trend
                assertTrue(hasPatient1Alert);
                assertTrue(hasIncreasingSystolicTrendAlert);
        }

        @Test
        void testDecreasingTrendAlert() {
                // Create a DataStorage instance
                DataStorage dataStorage = new DataStorage();

                // Add patient data with decreasing trend
                dataStorage.addPatientData(1, 140.0, "BloodPressure", 1L, 100, 120); // Initial reading
                dataStorage.addPatientData(1, 130.0, "BloodPressure", 2L, 90, 110); // Decreasing by more than 10 mmHg
                dataStorage.addPatientData(1, 120.0, "BloodPressure", 3L, 80, 100); // Decreasing by more than 10 mmHg

                // Initialize AlertGenerator with the DataStorage
                AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

                // Evaluate data for alerts
                alertGenerator.evaluateData(dataStorage.getPatient(1));

                // Verify that an alert is triggered for decreasing trend
                // Assert the presence of an alert in the triggeredAlerts list
                assertTrue(alertGenerator.getTriggeredAlerts().stream()
                                .anyMatch(alert -> alert.getPatientId() == 1
                                                && alert.getCondition().equals("Decreasing Blood Pressure Trend")));
        }

        @Test
        void testCriticalThresholdAlert() {
                // Create a DataStorage instance
                DataStorage dataStorage = new DataStorage();

                // Add patient data with readings above 180/120 mmHg and below 90/60 mmHg
                dataStorage.addPatientData(1, 190.0, "BloodPressure", 1L, 130, 190); // Systolic above 180 mmHg
                dataStorage.addPatientData(1, 140.0, "BloodPressure", 2L, 80, 120); // Diastolic within normal range
                dataStorage.addPatientData(1, 135.0, "BloodPressure", 3L, 70, 110); // Diastolic below 60 mmHg

                // Initialize AlertGenerator with the DataStorage
                AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

                // Evaluate data for alerts
                alertGenerator.evaluateData(dataStorage.getPatient(1));

                // Verify that alerts are triggered for critical thresholds
                // Assert the presence of alerts in the triggeredAlerts list
                assertTrue(alertGenerator.getTriggeredAlerts().stream()
                                .anyMatch(alert -> alert.getPatientId() == 1
                                                && alert.getCondition().equals("Critical Systolic Blood Pressure")));
                assertTrue(alertGenerator.getTriggeredAlerts().stream()
                                .anyMatch(alert -> alert.getPatientId() == 1
                                                && alert.getCondition().equals("Critical Diastolic Blood Pressure")));
        }
}
