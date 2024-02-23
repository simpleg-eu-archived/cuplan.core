package eu.simpleg.cuplan.core.secrets;

import com.google.gson.Gson;
import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.ErrorKind;
import eu.simpleg.cuplan.core.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BitwardenSecretsManager implements SecretsManager {
    private final String accessToken;
    private final Gson gson;

    public BitwardenSecretsManager(String accessToken) {
        this.accessToken = accessToken;
        gson = new Gson();
    }

    @Override
    public Result<String, Error> get(String secretId) {
        try {
            String secretJson = getSecretJson(secretId);

            Secret secret = gson.fromJson(secretJson, Secret.class);

            if (secret == null) {
                return Result.err(new Error(ErrorKind.SECRETS_MANAGER_FAILURE, String.format(
                        Locale.ROOT, "failed to deserialize JSON reply as a secret: %s", secretJson)));
            }

            return Result.ok(secret.value);
        } catch (Exception e) {
            return Result.err(new Error(
                    ErrorKind.SECRETS_MANAGER_FAILURE,
                    String.format("failed to get secret: %s", e)));
        }
    }

    private String getSecretJson(String secretId) throws IOException {
        List<String> command = Arrays.asList("bws", "secret", "get", secretId, "--access-token", accessToken);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder secretJson = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            secretJson.append(line);
            secretJson.append(System.lineSeparator());
        }

        return secretJson.toString();
    }

    private static class Secret {
        public String id;
        public String organizationId;
        public String projectId;
        public String key;
        public String value;
        public String creationDate;
        public String revisionDate;
    }
}
