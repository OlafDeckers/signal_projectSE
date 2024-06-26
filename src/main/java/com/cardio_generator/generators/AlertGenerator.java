package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * A generator for simulating alert data for patients.
 * 
 * <p>This generator simulates alert states for patients, with the ability to trigger and resolve alerts based on random probabilities.
 * 
 */
public class AlertGenerator implements PatientDataGenerator {

    /** The random number generator used for generating random probabilities. */
    public static final Random randomGenerator = new Random();
    
    /** An array to store the alert states for each patient. */
    private boolean[] alertStates;

    /**
     * Constructs a new AlertGenerator with the specified number of patients.
     * 
     * @param patientCount The number of patients for which to generate alert data.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alert data for the specified patient and outputs it using the provided output strategy.
     * 
     * <p>This method generates alert data for the specified patient ID and outputs it using the provided output strategy.
     * It simulates alert triggering and resolution based on random probabilities.
     * 
     * @param patientId The ID of the patient for which to generate alert data.
     * @param outputStrategy The output strategy to use for outputting the generated data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
