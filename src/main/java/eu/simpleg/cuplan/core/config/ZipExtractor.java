package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Empty;
import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.ErrorKind;
import eu.simpleg.cuplan.core.Result;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor implements Extractor {
    @Override
    public Result<Empty, Error> extract(byte[] packageData, String targetPath) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(packageData))) {
            ZipEntry entry;

            while ((entry = zipStream.getNextEntry()) != null) {
                Path filePath = Paths.get(targetPath, entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipStream, filePath);
                }

                zipStream.closeEntry();
            }

            return Result.ok(new Empty());
        } catch (Exception e) {
            return Result.err(new Error(
                    ErrorKind.EXTRACTOR_FAILURE,
                    String.format(Locale.ROOT, "failed to extract package data: %s", e)));
        }
    }
}
