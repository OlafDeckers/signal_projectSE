package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {

    private final String outputDir;

    public FileDataReader(String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File directory = new File(outputDir);
        if (!directory.isDirectory()) {
            throw new IOException("Specified output directory is not valid.");
        }

        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                parseFile(file, dataStorage);
            }
        }
    }

    private void parseFile(File file, DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) { // Ensure there are enough parts to include systolic and diastolic blood pressure
                    int patientId = Integer.parseInt(parts[0]);
                    double measurementValue = Double.parseDouble(parts[1]);
                    String recordType = parts[2];
                    long timestamp = Long.parseLong(parts[3]);
                    double systolicBloodPressure = Double.parseDouble(parts[4]); // Extract systolic blood pressure value
                    double diastolicBloodPressure = Double.parseDouble(parts[5]); // Extract diastolic blood pressure value
    
                    dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp, systolicBloodPressure, diastolicBloodPressure);
                } else {
                    System.err.println("Invalid data format: " + line);
                }
            }
        }
    }
    
}
