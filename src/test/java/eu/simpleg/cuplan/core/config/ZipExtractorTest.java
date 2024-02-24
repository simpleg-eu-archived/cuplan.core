package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class ZipExtractorTest {
    private ZipExtractor zipExtractor;

    @BeforeEach
    public void initialize() {
        zipExtractor = new ZipExtractor();
    }

    @Test
    public void ExtractDummyZipExtractsExpectedFiles() {
        String uuid = UUID.randomUUID().toString();
        URL resourceUrl = getClass().getClassLoader().getResource("config/ZipExtractorTest/dummy.zip");

        if (resourceUrl == null) {
            Assertions.fail("Failed to get resourceUrl for 'dummy.zip'.");
        }

        try (InputStream stream = resourceUrl.openStream()) {
            byte[] bytes = stream.readAllBytes();

            Result<Void, Error> result = zipExtractor.extract(bytes, uuid);

            boolean expectedDirectoryExists = Files.exists(Paths.get(uuid));
            boolean expectedExecutableWithinDirectoryExists = Files.exists(Paths.get(uuid, "cp-config"));
            boolean expectedConfigFileWithinDirectoryExists =
                    Files.exists(Paths.get(uuid, "config", "config.yaml"));
            boolean expectedLogConfigFileWithinDirectoryExists =
                    Files.exists(Paths.get(uuid, "config", "log4rs.yaml"));
            FileUtils.deleteDirectory(new File(uuid));
            Assertions.assertTrue(result.isOk());
            Assertions.assertTrue(expectedDirectoryExists);
            Assertions.assertTrue(expectedExecutableWithinDirectoryExists);
            Assertions.assertTrue(expectedConfigFileWithinDirectoryExists);
            Assertions.assertTrue(expectedLogConfigFileWithinDirectoryExists);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
