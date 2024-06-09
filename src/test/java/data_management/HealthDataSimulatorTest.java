package data_management;

import com.cardio_generator.HealthDataSimulator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HealthDataSimulatorTest {
    private ScheduledExecutorService mockScheduler;
    private OutputStrategy mockOutputStrategy;
    private HealthDataSimulator simulator;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        resetSingleton();
        mockScheduler = mock(ScheduledExecutorService.class);
        mockOutputStrategy = mock(OutputStrategy.class);
        simulator = HealthDataSimulator.getInstance();

        // Set the scheduler and output strategy fields via reflection to avoid modifying the singleton class directly
        Field schedulerField = HealthDataSimulator.class.getDeclaredField("scheduler");
        schedulerField.setAccessible(true);
        schedulerField.set(simulator, mockScheduler);

        Field outputStrategyField = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        outputStrategyField.setAccessible(true);
        outputStrategyField.set(simulator, mockOutputStrategy);
    }

    @Test
    void shouldSetPatientCountFromArguments() throws IOException, NoSuchFieldException, IllegalAccessException {
        String[] args = {"--patient-count", "200"};
        simulator.parseArguments(args);

        Field patientCountField = HealthDataSimulator.class.getDeclaredField("patientCount");
        patientCountField.setAccessible(true);
        int patientCount = (int) patientCountField.get(simulator);
        assertEquals(200, patientCount);
    }

    @Test
    void shouldSetConsoleOutputStrategy() throws IOException, NoSuchFieldException, IllegalAccessException {
        String[] args = {"--output", "console"};
        simulator.parseArguments(args);

        Field outputStrategyField = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        outputStrategyField.setAccessible(true);
        OutputStrategy outputStrategy = (OutputStrategy) outputStrategyField.get(simulator);
        assertTrue(outputStrategy instanceof ConsoleOutputStrategy);
    }

    @Test
    void shouldSetFileOutputStrategyAndCreateDirectory() throws IOException, NoSuchFieldException, IllegalAccessException {
        String[] args = {"--output", "file:data/output"};
        simulator.parseArguments(args);

        Field outputStrategyField = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        outputStrategyField.setAccessible(true);
        OutputStrategy outputStrategy = (OutputStrategy) outputStrategyField.get(simulator);
        assertTrue(outputStrategy instanceof com.cardio_generator.outputs.FileOutputStrategy);
        Path outputPath = Paths.get("data/output");
        assertTrue(Files.exists(outputPath));
    }

    @Test
    void shouldInitializePatientIdsCorrectly() throws Exception {
        Method initializePatientIds = HealthDataSimulator.class.getDeclaredMethod("initializePatientIds", int.class);
        initializePatientIds.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Integer> patientIds = (List<Integer>) initializePatientIds.invoke(simulator, 10);
        assertEquals(10, patientIds.size());
        assertTrue(patientIds.contains(1));
        assertTrue(patientIds.contains(10));
    }

    /**
     * Reset the singleton instance for testing purposes.
     */
    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instanceField = HealthDataSimulator.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
