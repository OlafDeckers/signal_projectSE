package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStorageTest {
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() {
        dataStorage = new DataStorage();
    }

    @Test
    void shouldAddMultiplePatientDataAndRetrieveCorrectly() {
        dataStorage.addPatientData(3, 150.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(3, 170.0, "BloodPressure", 1624376789051L);
        dataStorage.addPatientData(5, 180.0, "HeartRate", 1624376789052L);

        List<PatientRecord> records3 = dataStorage.getRecords(3, 1624376789040L, 1624376789060L);
        List<PatientRecord> records5 = dataStorage.getRecords(5, 1624376789040L, 1624376789060L);

        assertEquals(2, records3.size());
        assertEquals(1, records5.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoPatientFound() {
        List<PatientRecord> records = dataStorage.getRecords(2, 1624376789040L, 1624376789060L);
        assertTrue(records.isEmpty());
    }

    @Test
    void shouldRetrieveAllPatientsCorrectly() {
        dataStorage.addPatientData(1, 110.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(2, 140.0, "HeartRate", 1624376789052L);

        List<Patient> patients = dataStorage.getAllPatients();
        assertEquals(2, patients.size());
    }

    @Test
    void shouldAddDataToExistingPatient() {
        dataStorage.addPatientData(4, 120.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(4, 130.0, "HeartRate", 1624376789051L);

        List<PatientRecord> records = dataStorage.getRecords(4, 1624376789040L, 1624376789060L);
        assertEquals(2, records.size());
    }

    @Test
    void shouldReturnEmptyListForPatientWithNoData() {
        dataStorage.addPatientData(6, 140.0, "HeartRate", 1624376789050L);
        List<PatientRecord> records = dataStorage.getRecords(7, 1624376789040L, 1624376789060L);
        assertTrue(records.isEmpty());
    }

    @Test
    void shouldRetrieveRecordsWithinOverlappingTimeRange() {
        dataStorage.addPatientData(8, 150.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(8, 160.0, "BloodPressure", 1624376789055L);
        dataStorage.addPatientData(8, 170.0, "HeartRate", 1624376789060L);

        List<PatientRecord> records = dataStorage.getRecords(8, 1624376789050L, 1624376789060L);
        assertEquals(3, records.size());
    }

    @Test
    void shouldAddAndRetrieveMultiplePatientsCorrectly() {
        dataStorage.addPatientData(9, 180.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(10, 190.0, "BloodPressure", 1624376789051L);
        dataStorage.addPatientData(11, 200.0, "HeartRate", 1624376789052L);
        dataStorage.addPatientData(12, 210.0, "BloodPressure", 1624376789053L);

        List<Patient> patients = dataStorage.getAllPatients();
        assertEquals(4, patients.size());
    }

    @Test
    void shouldRetrieveRecordsWithExactTimeMatch() {
        dataStorage.addPatientData(13, 220.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(13, 230.0, "BloodPressure", 1624376789051L);

        List<PatientRecord> records = dataStorage.getRecords(13, 1624376789050L, 1624376789051L);
        assertEquals(2, records.size());
    }

    @Test
    void shouldReturnEmptyListForFutureTimeRange() {
        dataStorage.addPatientData(14, 240.0, "HeartRate", 1624376789050L);

        List<PatientRecord> records = dataStorage.getRecords(14, 1624376789060L, 1624376789070L);
        assertTrue(records.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForPastTimeRange() {
        dataStorage.addPatientData(15, 250.0, "HeartRate", 1624376789050L);

        List<PatientRecord> records = dataStorage.getRecords(15, 1624376789040L, 1624376789049L);
        assertTrue(records.isEmpty());
    }

    @Test
    void shouldHandleConcurrencyCorrectly() throws InterruptedException {
        Runnable addTask = () -> {
            for (int i = 0; i < 100; i++) {
                dataStorage.addPatientData(16, 260.0 + i, "HeartRate", 1624376789050L + i);
            }
        };

        Thread thread1 = new Thread(addTask);
        Thread thread2 = new Thread(addTask);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        List<PatientRecord> records = dataStorage.getRecords(16, 1624376789050L, 1624376789150L);
        assertEquals(200, records.size());
    }

    @Test
    void shouldHandleNegativeMeasurementValues() {
        dataStorage.addPatientData(17, -270.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(17, -280.0, "BloodPressure", 1624376789051L);

        List<PatientRecord> records = dataStorage.getRecords(17, 1624376789050L, 1624376789060L);
        assertEquals(2, records.size());
        assertEquals(-270.0, records.get(0).getMeasurementValue());
        assertEquals(-280.0, records.get(1).getMeasurementValue());
    }

    @Test
    void shouldRetrieveRecordWithZeroTimestamp() {
        dataStorage.addPatientData(18, 290.0, "HeartRate", 0L);

        List<PatientRecord> records = dataStorage.getRecords(18, 0L, 1L);
        assertEquals(1, records.size());
        assertEquals(290.0, records.get(0).getMeasurementValue());
    }

    @Test
    void shouldHandleDuplicateRecords() {
        dataStorage.addPatientData(19, 300.0, "HeartRate", 1624376789050L);
        dataStorage.addPatientData(19, 300.0, "HeartRate", 1624376789050L);

        List<PatientRecord> records = dataStorage.getRecords(19, 1624376789050L, 1624376789060L);
        assertEquals(2, records.size());
    }

    @Test
    void shouldThrowExceptionForNullRecordType() {
        assertThrows(NullPointerException.class, () -> {
            dataStorage.addPatientData(20, 310.0, null, 1624376789050L);
        });
    }

    @Test
    void shouldReturnEmptyListForInvalidPatientId() {
        dataStorage.addPatientData(21, 320.0, "HeartRate", 1624376789050L);
        List<PatientRecord> records = dataStorage.getRecords(999, 1624376789040L, 1624376789060L);
        assertTrue(records.isEmpty());
    }

    @Test
    void shouldPerformWellUnderHeavyLoad() {
        for (int i = 0; i < 5000; i++) {
            dataStorage.addPatientData(i, 330.0 + i, "HeartRate", 1624376789050L + i);
        }

        for (int i = 0; i < 5000; i++) {
            List<PatientRecord> records = dataStorage.getRecords(i, 1624376789040L, 1624376789060L + 5000);
            assertEquals(1, records.size());
        }
    }

    @Test
    void shouldRetrieveRecordsWithBoundaryTimestamps() {
        dataStorage.addPatientData(22, 340.0, "HeartRate", Long.MIN_VALUE);
        dataStorage.addPatientData(22, 350.0, "BloodPressure", Long.MAX_VALUE);

        List<PatientRecord> records = dataStorage.getRecords(22, Long.MIN_VALUE, Long.MAX_VALUE);
        assertEquals(2, records.size());
    }
}
