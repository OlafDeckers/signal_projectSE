package com.alerts.Decorator;

public abstract class AlertDecorator implements AlertInterface {
    protected AlertInterface decoratedAlert;

    public AlertDecorator(AlertInterface decoratedAlert) {
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
        return decoratedAlert.getPriority();
    }
}
