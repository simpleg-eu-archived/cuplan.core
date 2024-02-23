package eu.simpleg.cuplan.core.secrets;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;

/**
 * Secrets manager which provides a compact way of retrieving secrets from a specific source.
 */
public interface SecretsManager {
    /**
     * Retrieves a secret by its id.
     *
     * @param secretId Id of the secret.
     * @return The secret or an error if the operation failed.
     */
    Result<String, Error> get(String secretId);
}
