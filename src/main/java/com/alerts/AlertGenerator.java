package com.alerts;

import com.alerts.Strategy.AlertStrategy;
import com.alerts.Strategy.BloodPressureStrategy;
import com.alerts.Strategy.ECGStrategy;
import com.alerts.Strategy.HeartRateStrategy;
import com.alerts.Strategy.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    @SuppressWarnings("unused")
    private DataStorage dataStorage;
    private List<Alert> alerts;
    private List<AlertStrategy> strategies;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
        this.strategies = new ArrayList<>();
        // Add strategies
        this.strategies.add(new BloodPressureStrategy());
        this.strategies.add(new HeartRateStrategy());
        this.strategies.add(new OxygenSaturationStrategy());
        this.strategies.add(new ECGStrategy());
    }

    public void evaluateData(Patient patient) {
        long currentTime = System.currentTimeMillis();
        List<PatientRecord> records = patient.getRecords(0, currentTime);

        for (PatientRecord record : records) {
            for (AlertStrategy strategy : strategies) {
                strategy.checkAlert(patient, record, alerts);
            }
        }
    }

    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }
}
