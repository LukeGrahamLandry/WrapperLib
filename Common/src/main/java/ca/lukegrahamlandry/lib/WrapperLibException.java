package ca.lukegrahamlandry.lib;

import ca.lukegrahamlandry.lib.base.InternalUseOnly;

@InternalUseOnly
public class WrapperLibException extends RuntimeException {
    public WrapperLibException(String msg) {
        super(msg);
    }

    /**
     * This gets called when something goes wrong. You should probably set a break point here.
     */
    public static void maybeThrow(String msg) {
        // if (Available.PLATFORM_HELPER.get() && PlatformHelper.isDevelopmentEnvironment())
            throw new WrapperLibException(msg);
    }
}
