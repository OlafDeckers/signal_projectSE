package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClientCode extends WebSocketClient {

    private DataStorage dataStorage;
    private boolean connectionSuccessful;

    public WebSocketClientCode(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
        this.connectionSuccessful = false;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
        connectionSuccessful = true;
    }

    @Override
    public void onMessage(String message) {

        // Parse the incoming message and store the parsed information using DataStorage
        String[] parts = message.split(",");

        if (parts.length == 4) {
            try {

                int patientId = Integer.parseInt(parts[0]);

                double measurementValue = Double.parseDouble(parts[1]);
                String recordType = parts[2];

                long timestamp = Long.parseLong(parts[3]);
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

            } catch (NumberFormatException e) {
                System.err.println("Error parsing message: " + message);
            }
        } else {
            System.err.println("Invalid message format: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server");
        dataStorage.clear();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }
}
