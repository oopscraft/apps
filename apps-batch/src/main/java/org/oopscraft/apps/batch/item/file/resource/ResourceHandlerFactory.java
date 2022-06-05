package org.oopscraft.apps.batch.item.file.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceHandlerFactory {

    // resourceHandlerChains
    private final static List<ResourceHandler> RESOURCE_HANDLER_REGISTRY = new ArrayList<ResourceHandler>(){{
        add(new AwsS3ResourceHandler());
        add(new FileSystemResourceHandler());
    }};

    /**
     * getInstance
     * @param filePath filePath
     * @return ResourceHandler ResourceHandler
     */
    public static ResourceHandler getInstance(String filePath) {
        for(ResourceHandler resourceHandler : RESOURCE_HANDLER_REGISTRY){
            if(resourceHandler.supports(filePath)){
                return resourceHandler;
            }
        }
        throw new RuntimeException("No valid resource handler");
    }

}
