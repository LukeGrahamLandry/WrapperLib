package ca.lukegrahamlandry.lib.data.type;

import ca.lukegrahamlandry.lib.data.DataWrapper;

import java.util.function.Supplier;

public class GlobalDataWrapper<T> extends DataWrapper<T> implements Supplier<T> {
    public GlobalDataWrapper(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T get() {
        return null;
    }
}
