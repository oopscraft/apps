package org.oopscraft.apps.batch.item.file.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

@Slf4j
public class FileSystemResourceHandler extends ResourceHandler {

    @Override
    public boolean supports(String filePath) {
        return true;
    }

    @Override
    public Resource createReadableResource(String filePath) {
        log.info("FileSystemResourceHandler.createReadableResource[{}]", filePath);
        File file = new File(filePath);
        return new FileSystemResource(filePath);
    }

    @Override
    public Resource createWritableResource(String filePath) {
        log.info("FileSystemResourceHandler.createWritableResource[{}]", filePath);
        File file = new File(filePath);
        return new FileSystemResource(filePath);
    }

    @Override
    public void flushWritableResource(Resource resource, String filePath) {
        log.info("FileSystemResourceHandler.flushWritableResource[{},{}]", resource, filePath);
    }

}
