package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Error;
import eu.simpleg.cuplan.core.Result;

/**
 * Interface which provides a facility to extract a configuration package.
 */
public interface Extractor {
    /**
     * Extract's the configuration package's content into the targetPath.
     *
     * @param packageData Package's raw data.
     * @param targetPath  Path where the configuration will be extracted into.
     * @return Empty if successful, an error otherwise.
     */
    Result<Void, Error> extract(byte[] packageData, String targetPath);
}
