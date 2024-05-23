package com.data_management;

/**
 * Represents a single record of patient data at a specific point in time.
 * This class stores all necessary details for a single observation or
 * measurement
 * taken from a patient, including the type of record (such as ECG, blood
 * pressure),
 * the measurement value, and the exact timestamp when the measurement was
 * taken.
 */
public class PatientRecord {
    private int patientId;
    private String recordType; // Example: ECG, blood pressure, etc.
    private double measurementValue; // Example: heart rate
    private long timestamp;
    private double systolicBloodPressure;
    private double diastolicBloodPressure;

    /**
     * Constructs a new patient record with specified details.
     * 
     * @param patientId        the unique identifier for the patient
     * @param measurementValue the numerical value of the recorded measurement
     * @param recordType       the type of measurement (e.g., "ECG", "Blood
     *                         Pressure")
     * @param timestamp        the time at which the measurement was recorded, in
     *                         milliseconds since epoch
     * @param systolicBloodPressure the systolic blood pressure value
     * @param diastolicBloodPressure the diastolic blood pressure value
     */
    public PatientRecord(int patientId, double measurementValue, String recordType, long timestamp,
            double systolicBloodPressure, double diastolicBloodPressure) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
        this.systolicBloodPressure = systolicBloodPressure;
        this.diastolicBloodPressure = diastolicBloodPressure;
    }

    // Getter methods for systolic and diastolic blood pressure
    public double getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public double getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    /**
     * Returns the patient ID associated with this record.
     * 
     * @return the patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Returns the measurement value of this record.
     * 
     * @return the measurement value
     */
    public double getMeasurementValue() {
        return measurementValue;
    }

    /**
     * Returns the timestamp when this record was taken.
     * 
     * @return the timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the type of record (e.g., "ECG", "Blood Pressure").
     * 
     * @return the record type
     */
    public String getRecordType() {
        return recordType;
    }

        /**
     * Retrieves the blood oxygen saturation level from the patient record.
     *
     * @return The blood oxygen saturation level as a double value.
     * @throws UnsupportedOperationException if the record does not represent blood saturation.
     */
    public double getBloodSaturation() {
        if ("BloodSaturation".equals(recordType)) {
            return measurementValue;
        } else {
            throw new UnsupportedOperationException("This record does not represent blood saturation.");
        }
    }

    /**
     * Retrieves the heart rate from the patient record.
     *
     * @return The heart rate as a double value.
     * @throws UnsupportedOperationException if the record does not represent heart rate.
     */
    public double getHeartRate() {
        if ("HeartRate".equals(recordType)) {
            return measurementValue;
        } else {
            throw new UnsupportedOperationException("This record does not represent heart rate.");
        }
    }

    /**
     * Checks if the patient record represents an irregular beat pattern in an ECG reading.
     *
     * @return true if the record represents an irregular beat pattern, false otherwise.
     * @throws UnsupportedOperationException if the record does not represent an ECG reading.
     */
    public boolean hasIrregularBeat() {
        if ("ECG".equals(recordType)) {
            return measurementValue > 100; // Assuming heart rate greater than 100 bpm indicates irregular beat
    } else {
        throw new UnsupportedOperationException("This record does not represent ECG.");
    }
    }

    
}
