package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.MockWebSocketServer;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientCode;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static MockWebSocketServer mockServer;
    private static WebSocketClientCode client;
    private static DataStorage dataStorage;

    @BeforeAll
    static void setUp() throws Exception {
        // Start the mock WebSocket server
        mockServer = new MockWebSocketServer(new InetSocketAddress("localhost", 8887));
        mockServer.start();

        // Initialize DataStorage and WebSocketClientCode
        dataStorage = new DataStorage();
        client = new WebSocketClientCode(new URI("ws://localhost:8887"), dataStorage);
        client.connectBlocking();
    }

    @AfterAll
    static void tearDown() throws Exception {
        client.close();
        mockServer.stop();
    }

    @Test
    void testIntegration() throws InterruptedException {
        // Send test messages to the WebSocket client
        mockServer.broadcast("1,98.6,Temperature,1700000000000");
        mockServer.broadcast("2,120.0,BloodPressure,1700000000001");

        // Allow some time for messages to be processed
        Thread.sleep(1000);

        // Verify data in DataStorage
        List<PatientRecord> records1 = dataStorage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records1.size());
        assertEquals(98.6, records1.get(0).getMeasurementValue());
        assertEquals("Temperature", records1.get(0).getRecordType());

        List<PatientRecord> records2 = dataStorage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(1, records2.size());
        assertEquals(120.0, records2.get(0).getMeasurementValue());
        assertEquals("BloodPressure", records2.get(0).getRecordType());
    }

    @Test
    void testAlertGeneration() throws InterruptedException {
        // Initialize the AlertGenerator
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Send messages that should trigger alerts
        mockServer.broadcast("3,200.0,HeartRate,1700000000002");
        mockServer.broadcast("4,190.0,Systolic,1700000000003");
        mockServer.broadcast("4,130.0,Diastolic,1700000000004");
        mockServer.broadcast("5,85.0,Saturation,1700000000005");

        // Allow some time for messages to be processed
        Thread.sleep(1000);

        // Evaluate patient data for alerts
        for (Patient patient : dataStorage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        // Verify that alerts were generated correctly
        List<Alert> alerts = alertGenerator.getAlerts();
        assertEquals(4, alerts.size());
        assertTrue(alerts.stream().anyMatch(alert -> alert.getCondition().equals("High Heart Rate")));
        assertTrue(alerts.stream().anyMatch(alert -> alert.getCondition().equals("Critical Blood Pressure")));
        assertTrue(alerts.stream().anyMatch(alert -> alert.getCondition().equals("Critical Blood Pressure")));
        assertTrue(alerts.stream().anyMatch(alert -> alert.getCondition().equals("Low Blood Saturation")));
    }
}
