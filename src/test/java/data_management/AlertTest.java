package data_management;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;

import static org.junit.jupiter.api.Assertions.*;

class AlertTest {

    @Test
    void testAlertInequality() {
        Alert alert1 = new Alert("1", "High Heart Rate", 1714376789050L);
        Alert alert2 = new Alert("2", "Low Blood Pressure", 1714376789051L);

        assertNotEquals(alert1, alert2);
    }
    
    @Test
    void testAlertConstructorAndGetters() {
        Alert alert = new Alert("1", "High Heart Rate", 1714376789050L);

        assertEquals("1", alert.getPatientId());
        assertEquals("High Heart Rate", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
    }
}
