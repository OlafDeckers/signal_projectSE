package data_management;

import com.data_management.*;
import com.alerts.AlertGenerator;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.mock;

public class WebSocketClientCodeTest {

    private WebSocketClientCode client;

    private DataStorage storage;

    @Before
    public void setUp() {

        storage = DataStorage.getInstance();
        client = new WebSocketClientCode(URI.create("ws://localhost:8080"), storage);
    }

    @Test
    public void testOnOpen() {
        ServerHandshake mockedHandshake = mock(ServerHandshake.class);
        client.onOpen(mockedHandshake);
    }

    @Test
    public void testOnMessage() {

        String message = "1,200,WhiteBloodCells,1639874235"; // Sample message
        client.onMessage(message);

        List<PatientRecord> records = storage.getRecords(1, 1639874235L, 1639874235L);

        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals(200, records.get(0).getMeasurementValue(), 0.01);
    }

    @Test
    public void testOnClose() {
        client.onClose(1000, "Normal Closure", true);
    }

    @Test
    public void testOnError() { client.onError(new Exception("Test Exception")); }

    @Test
    public void testWebSocketClientOnMessage() throws Exception {

        client.connectBlocking();

        // Simulate receiving messages from the WebSocket server
        client.onMessage("1,100,WhiteBloodCells,1714376789050");
        client.onMessage("1,200,WhiteBloodCells,1714376789051");

        List<PatientRecord> records = storage.getRecords(1, 1714376789040L, 1714376789061L);
        Assertions.assertEquals(2, records.size());
        Assertions.assertEquals(100.0, records.get(0).getMeasurementValue(), 0.01);
        Assertions.assertEquals(200.0, records.get(1).getMeasurementValue(), 0.01);

        client.close();
    }

    @Test
    public void testInvalidMessage() {
        String message = "1,invalid,HeartRate,1714376789050";
        client.onMessage(message);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        Assertions.assertEquals(0, records.size());
    }


    @Test
    public void testRealTimeDataProcessing() throws InterruptedException {

        client.connectBlocking();

        client.onMessage("1,100.0,HeartRate,1714376789050");
        client.onMessage("1,110.0,HeartRate,1714376789051");
        client.onMessage("1,95.0,BloodPressure,1714376789052");

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789053L);
        Assertions.assertEquals(3, records.size());

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        alertGenerator.evaluateData(new Patient(1));

    }

    @Test
    public void testConnectionLoss() throws InterruptedException {
        client.connectBlocking();
        client.close(); // Simulate connection loss
        Assertions.assertFalse(client.isOpen());
    }

    @Test
    public void testReconnect() throws InterruptedException {
        client.connectBlocking();
        client.close(); // Simulate connection loss
        Assertions.assertFalse(client.isOpen());

        client.reconnectBlocking(); // Reconnect
        Assertions.assertFalse(client.isOpen());
    }

    @Test
    public void testDataTransmissionFailure() {
        client.onMessage("invalid message format");

        // Check if no data is added to the storage due to invalid message format
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        Assertions.assertEquals(0, records.size());
    }


}
