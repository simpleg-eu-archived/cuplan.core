package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class ClientTest {

    private static final String ExpectedValue = "1234abcd";
    private static final String Host = "https://simpleg.eu";
    private static final String Stage = "dummy";
    private static final String Environment = "development";
    private static final String Component = "dummy";
    private static final Path FilePath = Paths.get("application.yaml");
    private static final String ConfigKey = "Parent:Child";
    private final byte[] packageData = new byte[0];
    private Client client;
    @Mock
    private Downloader downloaderMock;
    @Mock
    private Extractor extractorMock;
    @Mock
    private Getter providerMock;
    private Path workingPath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workingPath = Path.of(UUID.randomUUID().toString());

        when(downloaderMock.download(Host, Stage, Environment, Component))
                .thenReturn(CompletableFuture.completedFuture(Result.ok(packageData)));
        when(extractorMock.extract(packageData, workingPath.toString()))
                .thenReturn(Result.ok(null));
        when(providerMock.get(FilePath, ConfigKey, String.class))
                .thenReturn(CompletableFuture.completedFuture(Result.ok(ExpectedValue)));
        doNothing().when(providerMock).cleanCache();

        client = new Client(
                Host,
                Stage,
                Environment,
                Component,
                workingPath,
                downloaderMock,
                extractorMock,
                providerMock);
    }

    @Test
    void getEmptyWorkingPathDownloadsConfiguration() throws Exception {
        Result<String, Error> result = client.get(FilePath, ConfigKey, String.class).join();

        client.close();
        assertExpectedValue(result);
        assertCompleteFlowExecutedTimes(1);
    }

    @Test
    void closeRemovesWorkingPath() throws Exception {
        Files.createDirectory(workingPath);

        client.close();

        assertFalse(Files.exists(workingPath));
    }

    private void assertExpectedValue(Result<String, Error> result) {
        Assertions.assertTrue(result.isOk(), "Expected result to be ok in order to extract value.");
        Assertions.assertEquals(ExpectedValue, result.unwrap());
    }

    private void assertCompleteFlowExecutedTimes(int times) {
        verify(downloaderMock, times(times))
                .download(Host, Stage, Environment, Component);
        verify(extractorMock, times(times))
                .extract(packageData, workingPath.toString());
        verify(providerMock, times(times)).cleanCache();
        verify(providerMock, times(times)).get(FilePath, ConfigKey, String.class);
    }
}