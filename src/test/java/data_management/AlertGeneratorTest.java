package data_management;

import com.alerts.*;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AlertGeneratorTest {
    private DataStorage mockDataStorage;
    private AlertGenerator alertGenerator;
    private Patient mockPatient;

    @BeforeEach
    void setUp() {
        mockDataStorage = mock(DataStorage.class);
        alertGenerator = new AlertGenerator(mockDataStorage);
        mockPatient = mock(Patient.class);
    }

    @Test
    void testHighHeartRateAlert() {
        List<PatientRecord> records = Collections.singletonList(
                new PatientRecord(1, 101.0, "HeartRate", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("High Heart Rate", alerts.get(0).getCondition());
    }

    @Test
    void testNormalHeartRateNoAlert() {
        List<PatientRecord> records = Collections.singletonList(
                new PatientRecord(1, 80.0, "HeartRate", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertTrue(alerts.isEmpty());
    }

    @Test
    void testCriticalBloodPressureAlerts() {
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 185.0, "Systolic", System.currentTimeMillis()),
                new PatientRecord(1, 125.0, "Diastolic", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(2, alerts.size());
        alerts.forEach(alert -> assertEquals("Critical Blood Pressure", alert.getCondition()));
    }

    @Test
    void testNoRecordsNoAlert() {
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertTrue(alerts.isEmpty());
    }

    @Test
    void testLowBloodPressureAlert() {
        List<PatientRecord> records = Collections.singletonList(
                new PatientRecord(1, 70.0, "Systolic", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Critical Blood Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testLowBloodSaturationAlert() {
        List<PatientRecord> records = Collections.singletonList(
                new PatientRecord(1, 89.0, "Saturation", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Low Blood Saturation", alerts.get(0).getCondition());
    }

    @Test
    void testAbnormalECGAlert() {
        List<PatientRecord> records = Collections.singletonList(
                new PatientRecord(1, 2.0, "ECG", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(1, alerts.size()); // Ensure the list is not empty
        assertEquals("Abnormal ECG", alerts.get(0).getCondition());
    }


    @Test
    void testMultipleAlerts() {
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 101.0, "HeartRate", System.currentTimeMillis()),
                new PatientRecord(1, 185.0, "Systolic", System.currentTimeMillis()),
                new PatientRecord(1, 125.0, "Diastolic", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(3, alerts.size());
        assertEquals("High Heart Rate", alerts.get(0).getCondition());
        assertEquals("Critical Blood Pressure", alerts.get(1).getCondition());
        assertEquals("Critical Blood Pressure", alerts.get(2).getCondition());
    }


    @Test
    void testRapidBloodSaturationDropAlert() {
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 97.0, "Saturation", System.currentTimeMillis() - 600000),
                new PatientRecord(1, 91.0, "Saturation", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals("Rapid Blood Saturation Drop", alerts.get(0).getCondition());
    }

    @Test
    void testEvaluateDataWithDifferentPatients() {
        Patient mockPatient1 = mock(Patient.class);
        Patient mockPatient2 = mock(Patient.class);
        List<PatientRecord> records1 = Collections.singletonList(
                new PatientRecord(1, 105.0, "HeartRate", System.currentTimeMillis())
        );
        List<PatientRecord> records2 = Collections.singletonList(
                new PatientRecord(2, 125.0, "Diastolic", System.currentTimeMillis())
        );
        when(mockPatient1.getRecords(anyLong(), anyLong())).thenReturn(records1);
        when(mockPatient2.getRecords(anyLong(), anyLong())).thenReturn(records2);

        alertGenerator.evaluateData(mockPatient1);
        alertGenerator.evaluateData(mockPatient2);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(2, alerts.size());
        assertEquals("High Heart Rate", alerts.get(0).getCondition());
        assertEquals("Critical Blood Pressure", alerts.get(1).getCondition());
    }

    @Test
    void testEvaluateDataWithHypotensiveHypoxemia() {
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 85.0, "Systolic", System.currentTimeMillis()),
                new PatientRecord(1, 89.0, "Saturation", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(2, alerts.size());
        assertEquals("Critical Blood Pressure", alerts.get(0).getCondition());
        assertEquals("Low Blood Saturation", alerts.get(1).getCondition());
    }

    @Test
    void testIncreasingBloodPressureTrendAlert() {
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 110.0, "Systolic", System.currentTimeMillis() - 1200000),
                new PatientRecord(1, 122.0, "Systolic", System.currentTimeMillis() - 600000),
                new PatientRecord(1, 135.0, "Systolic", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals("Increasing Blood Pressure Trend", alerts.get(0).getCondition());
    }

    @Test
    void testDecreasingBloodPressureTrendAlert() {
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 140.0, "Systolic", System.currentTimeMillis() - 1200000),
                new PatientRecord(1, 127.0, "Systolic", System.currentTimeMillis() - 600000),
                new PatientRecord(1, 113.0, "Systolic", System.currentTimeMillis())
        );
        when(mockPatient.getRecords(anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(mockPatient);

        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals("Decreasing Blood Pressure Trend", alerts.get(0).getCondition());
    }

}



