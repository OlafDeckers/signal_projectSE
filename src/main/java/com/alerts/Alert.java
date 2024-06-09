package com.alerts;

import com.alerts.Decorator.AlertInterface;

public class Alert implements AlertInterface {
    private int patientId;
    private String condition;
    private long timestamp;
    private String priority;

    public Alert(int patientId, String condition, long timestamp, String priority) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
        this.priority = priority;
    }

    @Override
    public String getPatientId() {
        return String.valueOf(patientId);
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return condition; // Simplified for decorators to add their string
    }
}
