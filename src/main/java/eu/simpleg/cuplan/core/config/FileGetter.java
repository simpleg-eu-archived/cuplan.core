package eu.simpleg.cuplan.core.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.*;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FileGetter implements Getter {
    private static final String KEY_SEPARATOR = ":";
    private final Path targetDirectory;
    private final Cache cache;
    private final long expireCacheItemAfterMilliseconds;
    private final Yaml yaml;
    private final Gson gson;

    public FileGetter(Path targetDirectory, Cache cache, long expireCacheItemAfterMilliseconds) {
        this.targetDirectory = targetDirectory;
        this.cache = cache;
        this.expireCacheItemAfterMilliseconds = expireCacheItemAfterMilliseconds;
        yaml = new Yaml();
        gson = new Gson();
    }

    @Override
    public void close() throws Exception {
        if (Files.exists(targetDirectory)) {
            FileUtils.deleteDirectory(targetDirectory.toFile());
        }

        cache.close();
    }

    @Override
    public <T> CompletableFuture<Result<T, Error>> get(Path filePath, String key, Class<T> type) {
        try {
            filePath = targetDirectory.resolve(filePath);
            Option<Object> cacheResult = cache.tryGetValue(filePath.toString());

            if (cacheResult.isSome()) {
                Object yamlObject = cacheResult.unwrap();
                JsonElement jsonElement = gson.toJsonTree(yamlObject);

                return CompletableFuture.completedFuture(getByKey(jsonElement, key, type));
            }

            if (!filePath.toFile().exists()) {
                return CompletableFuture.completedFuture(Result.err(
                        new Error(ErrorKind.GETTER_FAILURE, String.format("failed to find file: %s", filePath))));
            }

            final Path finalFilePath = filePath;
            return CompletableFuture.supplyAsync(() -> {
                try {
                    String configYaml = Files.readString(finalFilePath, StandardCharsets.UTF_8);

                    Object yamlObject = yaml.load(configYaml);

                    cache.set(finalFilePath.toString(), yamlObject, expireCacheItemAfterMilliseconds);
                    JsonElement jsonElement = gson.toJsonTree(yamlObject);

                    return getByKey(jsonElement, key, type);
                } catch (Exception e) {
                    return Result.err(new Error(
                            ErrorKind.GETTER_FAILURE,
                            String.format("failed to execute 'get configuration value': %s", e)));
                }
            });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.err(
                    new Error(ErrorKind.GETTER_FAILURE, String.format("failed to get configuration value: %s", e))));
        }
    }

    @Override
    public void cleanCache() {
        cache.clear();
    }

    private <T> Result<T, Error> getByKey(JsonElement jsonElement, String key, Class<T> type) {
        try {
            String[] keys = key.split(KEY_SEPARATOR);

            JsonElement nextJsonElement = jsonElement.getAsJsonObject().get(keys[0]);

            if (keys.length > 1) {
                return getByKey(nextJsonElement, key.substring(key.indexOf(KEY_SEPARATOR) + 1), type);
            }

            return Result.ok(gson.fromJson(nextJsonElement, type));
        } catch (Exception e) {
            return Result.err(new Error(
                    ErrorKind.GETTER_FAILURE,
                    String.format("failed to get configuration value by key: %s", e)));
        }
    }
}