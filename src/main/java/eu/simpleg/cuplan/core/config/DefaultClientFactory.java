package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Cache;
import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;

import java.net.http.HttpClient;
import java.nio.file.Path;

public class DefaultClientFactory implements ClientFactory {
    @Override
    public Result<Client, Error> build(
            String accessToken,
            String host,
            String stage,
            String environment,
            String component,
            Path workingPath,
            long downloadTimeoutInMilliseconds) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        Downloader downloader = new HttpDownloader(httpClient, accessToken, downloadTimeoutInMilliseconds);
        Extractor extractor = new ZipExtractor();
        Getter getter = new FileGetter(workingPath, new Cache(36000000), 36000000);

        return Result.ok(new Client(host, stage, environment, component, workingPath, downloader, extractor, getter));
    }
}
