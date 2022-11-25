package ca.lukegrahamlandry.lib.config;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.reflect.TypeToken;

public class ListConfigWrapper<T> extends ConfigWrapper<List<T>>{
    private final Class<T> clazz;
    public ListConfigWrapper(Class<T> clazz, Supplier<List<T>> defaultConfig, String name, Side side, boolean reloadable, boolean verbose) {
        super(defaultConfig, name, side, reloadable, verbose);
        this.clazz = clazz;
    }

    public ListConfigWrapper(Class<T> clazz, Supplier<List<T>> defaultConfig, String name, Side side) {
        this(clazz, defaultConfig, name, side, true, true);
    }

    public ListConfigWrapper(Class<T> clazz, String name, Side side) {
        this(clazz, ArrayList::new, name, side);
    }

    @Override
    protected void parse(Reader reader) {
        this.value = Arrays.asList((T[]) this.getGson().fromJson(reader, this.clazz.arrayType()));
    }
}
