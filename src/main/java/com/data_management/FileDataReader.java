package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FileDataReader implements DataReader {
    private BufferedReader reader;

    /**
     * Constructor
     *
     * @param filePath the path to the file containing data to read
     */
    public FileDataReader(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
    }

    /**
     * Constructor for testing
     *
     * @param reader a BufferedReader for reading data
     */
    public FileDataReader(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Reads data from the specified file and stores it in the given DataStorage instance.
     *
     * @param dataStorage the data storage where data will be stored
     * @throws IOException if there is an error reading the data from the file
     */
    
    public void readData(DataStorage dataStorage) throws IOException {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 4) {
                    int patientId = Integer.parseInt(fields[0]);
                    double measurementValue = Double.parseDouble(fields[1]);
                    String recordType = fields[2];
                    long timestamp = Long.parseLong(fields[3]);
                    dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                } else {
                    System.err.println("Ignored: " + line);
                }
            }
        } finally {
            reader.close();
        }
    }

    @Override
    public void readData(DataStorage dataStorage, String websocketUrl) throws IOException {
        try {
            URI url = new URI(websocketUrl);
            WebSocketClientCode client = new WebSocketClientCode(url, dataStorage);
            client.connectBlocking();
            if (!client.isConnectionSuccessful()) {
                throw new IOException("Failed to connect to WebSocket");
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException("Failed to connect to WebSocket", e);
        }
    }
}
