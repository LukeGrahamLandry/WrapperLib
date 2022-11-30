package ca.lukegrahamlandry.lib.base;

import java.util.ServiceLoader;

public class Services {

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
