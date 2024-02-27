package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;

import java.util.concurrent.CompletableFuture;

/**
 * Facility to download configuration packages.
 */
public interface Downloader {
    /**
     * Downloads the latest configuration from a specific configuration provider.
     *
     * @param host        where the configuration is being downloaded from.
     * @param stage       of the configuration package to be downloaded.
     * @param environment from which the configuration package is being downloaded from.
     * @param component   which represents the configuration entity to be downloaded.
     * @return An array of bytes containing the configuration package, or an {@link Error} if there was a failure.
     */
    CompletableFuture<Result<byte[], Error>> download(
            String host,
            String stage,
            String environment,
            String component);
}
