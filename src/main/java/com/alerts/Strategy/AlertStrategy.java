package com.alerts.Strategy;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public interface AlertStrategy {
    void checkAlert(Patient patient, PatientRecord record, List<Alert> alerts);
}
