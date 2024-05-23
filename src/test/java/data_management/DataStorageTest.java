package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // Create a DataStorage instance
        DataStorage storage = new DataStorage();

        // Add patient data
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L, 0, 0);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L, 0, 0);

        // Retrieve records
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);

        // Check if records are retrieved correctly
        assertEquals(2, records.size(), "Incorrect number of records retrieved");
        assertEquals(100.0, records.get(0).getMeasurementValue(), "Incorrect measurement value for the first record");
    }

    @Test
    void testAddAndGetRecordsForNonExistingPatient() {
        // Create a DataStorage instance
        DataStorage storage = new DataStorage();

        // Retrieve records for a non-existing patient
        List<PatientRecord> records = storage.getRecords(2, 1714376789050L, 1714376789051L);

        // Check if empty list is returned for non-existing patient
        assertTrue(records.isEmpty(), "Records should be empty for non-existing patient");
    }

    @Test
    void testAddAndGetRecordsWithEmptyStorage() {
        // Create a DataStorage instance
        DataStorage storage = new DataStorage();

        // Retrieve records from an empty storage
        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);

        // Check if empty list is returned for empty storage
        assertTrue(records.isEmpty(), "Records should be empty for empty storage");
    }


}
