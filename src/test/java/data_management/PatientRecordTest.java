package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.PatientRecord;

import static org.junit.jupiter.api.Assertions.*;

class PatientRecordTest {
    
    private long currentTime;
    private PatientRecord record;

    @BeforeEach
    void setUp() {
        currentTime = System.currentTimeMillis();
        record = new PatientRecord(1, 100.0, "HeartRate", currentTime);
    }

    @Test
    void shouldInitializePatientRecordCorrectly() {
        assertEquals(1, record.getPatientId());
        assertEquals(100.0, record.getMeasurementValue());
        assertEquals("HeartRate", record.getRecordType());
        assertEquals(currentTime, record.getTimestamp());
    }

    @Test
    void shouldHandleBoundaryValueForMeasurement() {
        PatientRecord boundaryRecord = new PatientRecord(2, 0.0, "BloodPressure", currentTime);
        assertEquals(2, boundaryRecord.getPatientId());
        assertEquals(0.0, boundaryRecord.getMeasurementValue());
        assertEquals("BloodPressure", boundaryRecord.getRecordType());
        assertEquals(currentTime, boundaryRecord.getTimestamp());
    }

    @Test
    void shouldHandleLargeMeasurementValues() {
        double largeValue = Double.MAX_VALUE;
        PatientRecord largeRecord = new PatientRecord(3, largeValue, "Temperature", currentTime);
        assertEquals(3, largeRecord.getPatientId());
        assertEquals(largeValue, largeRecord.getMeasurementValue());
        assertEquals("Temperature", largeRecord.getRecordType());
        assertEquals(currentTime, largeRecord.getTimestamp());
    }

    @Test
    void shouldHandleNegativeMeasurementValues() {
        double negativeValue = -100.0;
        PatientRecord negativeRecord = new PatientRecord(4, negativeValue, "GlucoseLevel", currentTime);
        assertEquals(4, negativeRecord.getPatientId());
        assertEquals(negativeValue, negativeRecord.getMeasurementValue());
        assertEquals("GlucoseLevel", negativeRecord.getRecordType());
        assertEquals(currentTime, negativeRecord.getTimestamp());
    }

    @Test
    void shouldHandleFutureTimestamps() {
        long futureTime = currentTime + 1000000L;
        PatientRecord futureRecord = new PatientRecord(5, 75.0, "OxygenSaturation", futureTime);
        assertEquals(5, futureRecord.getPatientId());
        assertEquals(75.0, futureRecord.getMeasurementValue());
        assertEquals("OxygenSaturation", futureRecord.getRecordType());
        assertEquals(futureTime, futureRecord.getTimestamp());
    }

    @Test
    void shouldHandlePastTimestamps() {
        long pastTime = currentTime - 1000000L;
        PatientRecord pastRecord = new PatientRecord(6, 90.0, "RespiratoryRate", pastTime);
        assertEquals(6, pastRecord.getPatientId());
        assertEquals(90.0, pastRecord.getMeasurementValue());
        assertEquals("RespiratoryRate", pastRecord.getRecordType());
        assertEquals(pastTime, pastRecord.getTimestamp());
    }
}
