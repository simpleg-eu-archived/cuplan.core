package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface Getter extends AutoCloseable {
    /**
     * Tries to get a configuration by its file path and key.
     *
     * @param filePath File from which the configuration will be extracted.
     * @param key      Key of the configuration, levels being separated by an ':'.
     * @param type     Type instance of the configuration's value.
     * @param <T>      Type of the configuration's value.
     * @return A result containing the value of the specified configuration key or an {@link Error}.
     */
    <T> CompletableFuture<Result<T, Error>> get(Path filePath, String key, Class<T> type);

    /**
     * Clean any underlying caching mechanism within the getter.
     */
    void cleanCache();
}
