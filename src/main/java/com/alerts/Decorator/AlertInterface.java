package com.alerts.Decorator;

public interface AlertInterface {
    String getPatientId();
    String getCondition();
    long getTimestamp();
    String getPriority();
}

