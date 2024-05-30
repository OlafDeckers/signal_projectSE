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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class HealthDataSimulatorTest {
    private ScheduledExecutorService mockScheduler;
    private OutputStrategy mockOutputStrategy;
    private HealthDataSimulator simulator;

    @BeforeEach
    void setUp() {
        mockScheduler = mock(ScheduledExecutorService.class);
        mockOutputStrategy = mock(OutputStrategy.class);
        simulator = new HealthDataSimulator(mockScheduler, mockOutputStrategy);
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
    void shouldUseDefaultPatientCountForInvalidArgument() throws IOException, NoSuchFieldException, IllegalAccessException {
        String[] args = {"--patient-count", "invalid"};
        simulator.parseArguments(args);

        Field patientCountField = HealthDataSimulator.class.getDeclaredField("patientCount");
        patientCountField.setAccessible(true);
        int patientCount = (int) patientCountField.get(simulator);
        assertEquals(50, patientCount);
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

    @Test
    void shouldScheduleTasksForAllPatients() throws Exception {
        List<Integer> patientIds = List.of(1, 2, 3, 4, 5);

        Method scheduleTasksForPatients = HealthDataSimulator.class.getDeclaredMethod("scheduleTasksForPatients", List.class);
        scheduleTasksForPatients.setAccessible(true);

        scheduleTasksForPatients.invoke(simulator, patientIds);
        verify(mockScheduler, times(patientIds.size() * 5)).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

}
