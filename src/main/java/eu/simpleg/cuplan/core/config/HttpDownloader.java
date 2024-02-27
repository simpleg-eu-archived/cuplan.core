package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.ErrorKind;
import eu.simpleg.cuplan.core.Result;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class HttpDownloader implements Downloader {
    private final HttpClient client;
    private final long downloadTimeoutInMilliseconds;

    public HttpDownloader(HttpClient client, long downloadTimeoutInMilliseconds) {
        this.client = client;
        this.downloadTimeoutInMilliseconds = downloadTimeoutInMilliseconds;
    }

    @Override
    public CompletableFuture<Result<byte[], Error>> download(
            String host,
            String stage,
            String environment,
            String component) {
        try {
            String url = String.format(
                    Locale.ROOT,
                    "%s/config?stage=%s&environment%s&component=%s",
                    host,
                    stage,
                    environment,
                    component);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofMillis(downloadTimeoutInMilliseconds))
                    .GET()
                    .build();

            return client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                    .thenApply(response -> {
                        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                            return Result.<byte[], Error>ok(response.body());
                        } else {
                            String error = new String(response.body());
                            return Result.<byte[], Error>err(new Error(
                                    ErrorKind.DOWNLOADER_FAILURE,
                                    String.format(Locale.ROOT, "failed to download configuration: %s", error)));
                        }
                    }).exceptionally(e -> {
                        if (e.getCause() instanceof HttpTimeoutException) {
                            return Result.err(new Error(
                                    ErrorKind.DOWNLOADER_FAILURE,
                                    "timed out downloading configuration"));
                        }

                        return Result.err(new Error(
                                ErrorKind.DOWNLOADER_FAILURE,
                                String.format("failed to download configuration: %s", e)));
                    });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.err(
                    new Error(ErrorKind.DOWNLOADER_FAILURE, String.format("failed to download configuration: %s", e))));
        }
    }
}
