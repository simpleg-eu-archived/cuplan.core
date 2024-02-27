package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;

import java.nio.file.Path;

public interface ClientFactory {
    Result<Client, Error> build(
            String accessToken,
            String host,
            String stage,
            String environment,
            String component,
            Path workingPath,
            long downloadTimeoutInMilliseconds);
}
