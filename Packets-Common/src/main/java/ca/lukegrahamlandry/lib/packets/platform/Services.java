package ca.lukegrahamlandry.lib.packets.platform;

import ca.lukegrahamlandry.lib.packets.platform.services.INetworkHelper;

import java.util.ServiceLoader;

public class Services {

    public static final INetworkHelper NETWORK = load(INetworkHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
