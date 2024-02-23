package eu.simpleg.cuplan.core.secrets;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.ErrorKind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BitwardenSecretsManagerTest {

    private BitwardenSecretsManager secretsManager;

    @BeforeEach
    public void BeforeEach() {
        String accessToken = System.getenv("SECRETS_MANAGER_ACCESS_TOKEN");
        secretsManager = new BitwardenSecretsManager(accessToken);
    }

    @Test
    public void GetValidSecretReturnsExpectedValue() {
        String expectedSecret = "le_secret :)";
        String value = secretsManager.get("7c1d5dfd-a58b-47cf-bee5-b0a600fe50c9").unwrap();

        Assertions.assertEquals(expectedSecret, value);
    }

    @Test
    public void GetNonExistingSecretReturnsError() {
        Error error = secretsManager.get("1234-not-existing").unwrapErr();

        Assertions.assertEquals(ErrorKind.SECRETS_MANAGER_FAILURE, error.getErrorKind());
    }
}
