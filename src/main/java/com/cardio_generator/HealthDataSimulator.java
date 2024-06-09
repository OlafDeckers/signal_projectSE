package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The main class for the Health Data Simulator application.
 * This class orchestrates the generation of simulated health data for multiple patients.
 * It schedules tasks for data generation and handles command-line arguments for configuring the simulation.
 * 
 */

public class HealthDataSimulator {

    /** Singleton instance */
    private static HealthDataSimulator instance;

    /** Default number of patients */
    private static int patientCount = 50;

    /** Executor service for scheduling tasks */
    private ScheduledExecutorService scheduler;

    /** Default output strategy */
    private OutputStrategy outputStrategy = new ConsoleOutputStrategy();

    /** Random number generator */
    private static final Random random = new Random();

    /**
     * Private constructor to prevent instantiation
     */
    private HealthDataSimulator() {}

    /**
     * Get the singleton instance of HealthDataSimulator
     * 
     * @return the singleton instance
     */
    public static HealthDataSimulator getInstance() {
        if (instance == null) {
            synchronized (HealthDataSimulator.class) {
                if (instance == null) {
                    instance = new HealthDataSimulator();
                }
            }
        }
        return instance;
    }

    /**
     * The main entry point for the Health Data Simulator application.
     * Parses command-line arguments and starts the simulation.
     * 
     * @param args Command-line arguments passed to the application.
     * @throws IOException if an I/O error occurs while parsing arguments or initializing output strategies.
     */
    public static void main(String[] args) throws IOException {
        HealthDataSimulator simulator = HealthDataSimulator.getInstance();
        simulator.parseArguments(args);

        simulator.scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = simulator.initializePatientIds(patientCount);
        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        simulator.scheduleTasksForPatients(patientIds);
    }

    /**
     * Parses the command-line arguments and configures the simulation accordingly.
     * 
     * <p>Supported options:
     * <ul>
     * <li>{@code -h}: Displays the help message and exits.
     * <li>{@code --patient-count <count>}: Specifies the number of patients for data simulation (default: 50).
     * <li>{@code --output <type>}: Defines the output method. Options are:
     *     <ul>
     *     <li>{@code console}: For console output.
     *     <li>{@code file:<directory>}: For file output. The directory must be specified after the colon.
     *     <li>{@code websocket:<port>}: For WebSocket output. The port number must be specified after the colon.
     *     <li>{@code tcp:<port>}: For TCP socket output. The port number must be specified after the colon.
     *     </ul>
     * </ul>
     * 
     * <p>If invalid options are provided, an error message is printed, and the help message is displayed.
     * 
     * @param args Command-line arguments passed to the application.
     * @throws IOException if an I/O error occurs while parsing arguments or initializing output strategies.
     */
    public void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for WebSocket output. Please specify a valid port number.");
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
                            }
                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Prints the help message with usage instructions for the Health Data Simulator application.
     * 
     * <p>The help message provides information about available options and their usage.
     * 
     * @see #main(String[])
     */
    private void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println(
                "  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println(
                "  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
    }

    /**
     * Initializes a list of patient IDs based on the specified number of patients.
     * 
     * <p>This method generates a sequential list of patient IDs starting from 1 up to the specified count.
     * 
     * @param patientCount The number of patients for which to generate IDs.
     * @return A list containing patient IDs from 1 to the specified count.
     */
    private List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    /**
     * Schedules data generation tasks for each patient.
     * 
     * <p>This method schedules tasks for generating ECG, blood saturation, blood pressure, blood levels, and alert data
     * for each patient. It uses the provided output strategy to output the generated data.
     * 
     * @param patientIds The list of patient IDs for which to schedule data generation tasks.
     */
    private void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Schedules a task for execution at a fixed rate with a random initial delay.
     * 
     * <p>This method schedules a task for execution at a fixed rate, with the specified initial delay and period.
     * The initial delay is randomly generated to stagger the execution of tasks.
     * 
     * @param task The task to be executed.
     * @param period The time between successive task executions.
     * @param timeUnit The time unit for the initial delay and period.
     */
    private void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }
}
