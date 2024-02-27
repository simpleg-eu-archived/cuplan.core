package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.ErrorKind;
import eu.simpleg.cuplan.core.Result;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class Client implements AutoCloseable {
    private final String host;
    private final String stage;
    private final String environment;
    private final String component;
    private final Path workingPath;
    private final Downloader downloader;
    private final Extractor extractor;
    private final Getter getter;

    public Client(
            String host,
            String stage,
            String environment,
            String component,
            Path workingPath,
            Downloader downloader,
            Extractor extractor,
            Getter getter) {
        this.host = host;
        this.stage = stage;
        this.environment = environment;
        this.component = component;
        this.workingPath = workingPath;
        this.downloader = downloader;
        this.extractor = extractor;
        this.getter = getter;
    }

    @Override
    public void close() throws Exception {
        FileUtils.deleteDirectory(workingPath.toFile());
    }

    public <T> CompletableFuture<Result<T, Error>> get(Path filePath, String key, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!Files.exists(workingPath)) {
                    var initResult = initializeConfigurationWithinWorkingPath().get();

                    if (!initResult.isOk()) {
                        return Result.err(initResult.unwrapErr());
                    }
                }

                return getter.get(filePath, key, type).get();
            } catch (Exception e) {
                return Result.err(new Error(
                        ErrorKind.CLIENT_FAILURE,
                        String.format(Locale.ROOT, "failed to get configuration: %s", e)));
            }
        });
    }

    private CompletableFuture<Result<Void, Error>> initializeConfigurationWithinWorkingPath() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(workingPath);

                var downloadResult = downloader.download(host, stage, environment, component).get();

                if (!downloadResult.isOk()) {
                    return Result.err(downloadResult.unwrapErr());
                }

                getter.cleanCache();
                return extractor.extract(downloadResult.unwrap(), workingPath.toString());
            } catch (Exception e) {
                return Result.err(new Error(
                        ErrorKind.CLIENT_FAILURE,
                        String.format("failed to initialize configuration: %s", e)));
            }
        });
    }
}