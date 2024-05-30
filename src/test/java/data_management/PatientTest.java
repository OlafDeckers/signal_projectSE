package data_management;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {
    private Patient patient;

    @BeforeEach
    void initializePatient() {
        patient = new Patient(1);
    }

    @Test
    void testAddAndRetrieveRecordsWithinRange() {
        long now = System.currentTimeMillis();
        patient.addRecord(100.0, "HeartRate", now);
        patient.addRecord(120.0, "HeartRate", now + 1000);

        List<PatientRecord> records = patient.getRecords(now, now + 2000);
        assertEquals(2, records.size(), "Should return two records within the time range");
        assertEquals(100.0, records.get(0).getMeasurementValue(), "First record should have a measurement value of 100.0");
        assertEquals(120.0, records.get(1).getMeasurementValue(), "Second record should have a measurement value of 120.0");
    }

    @Test
    void testRetrieveRecordsWithNoMatches() {
        long now = System.currentTimeMillis();
        patient.addRecord(100.0, "HeartRate", now);

        List<PatientRecord> records = patient.getRecords(now + 1000, now + 2000);
        assertTrue(records.isEmpty(), "No records should be returned for the specified time range");
    }

    @Test
    void testRetrieveRecordsAtBoundaryTimes() {
        long now = System.currentTimeMillis();
        patient.addRecord(100.0, "HeartRate", now);
        patient.addRecord(120.0, "HeartRate", now + 1000);

        // Test with exact start time match
        List<PatientRecord> records = patient.getRecords(now, now + 500);
        assertEquals(1, records.size(), "Should return one record at the start boundary");
        assertEquals(100.0, records.get(0).getMeasurementValue(), "Record should have a measurement value of 100.0");

        // Test with exact end time match
        records = patient.getRecords(now + 500, now + 1000);
        assertEquals(1, records.size(), "Should return one record at the end boundary");
        assertEquals(120.0, records.get(0).getMeasurementValue(), "Record should have a measurement value of 120.0");
    }
}
