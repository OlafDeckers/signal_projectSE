package com.alerts.Decorator;

public class PriorityAlertDecorator implements AlertInterface {
    private final AlertInterface decoratedAlert;

    public PriorityAlertDecorator(AlertInterface decoratedAlert) {
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public String getPatientId() {
        return decoratedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }

    @Override
    public String getPriority() {
        return "High";
    }

    @Override
    public String toString() {
        return String.format("%s (Priority: %s)", decoratedAlert.toString(), getPriority());
    }
}
