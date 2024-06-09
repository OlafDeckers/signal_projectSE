package com.alerts.Decorator;

public class RepeatedAlertDecorator implements AlertInterface {
    private final AlertInterface decoratedAlert;
    private final int repeatCount;

    public RepeatedAlertDecorator(AlertInterface decoratedAlert, int repeatCount) {
        this.decoratedAlert = decoratedAlert;
        this.repeatCount = repeatCount;
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
        return decoratedAlert.getPriority();
    }

    @Override
    public String toString() {
        return String.format("%s (Repeated %d times)", decoratedAlert.toString(), repeatCount);
    }
}
