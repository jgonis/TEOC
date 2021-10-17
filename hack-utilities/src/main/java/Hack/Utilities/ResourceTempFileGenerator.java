package Hack.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ResourceTempFileGenerator {
    public static File createTempFileFromResource(Class<?> resourceLocator,
                                                  String resourceName,
                                                  String resourceSuffix) throws IOException {
        File resourceFile;
        InputStream resourceStream = resourceLocator.getResourceAsStream("/" + resourceName + resourceSuffix);
        resourceFile = File.createTempFile(resourceName, resourceSuffix);
        resourceFile.deleteOnExit();
        if (resourceStream != null) {
            Files.copy(resourceStream, resourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            resourceFile = null;
        }

        return resourceFile;
    }

}
