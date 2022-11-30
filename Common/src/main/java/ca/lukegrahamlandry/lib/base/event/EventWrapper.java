package ca.lukegrahamlandry.lib.base.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EventWrapper {
    private static List<IEventCallbacks> HANDLERS = new ArrayList<>();

    public static void add(IEventCallbacks handler){
        HANDLERS.add(handler);
    }

    public static void add(String handlerClassName){
        try {
            Class<?> clazz = Class.forName(handlerClassName);
            add((IEventCallbacks) clazz.getConstructor().newInstance());
        } catch (ClassNotFoundException ignored) {
            // dev used shadow exclude, that's fine
        }
        catch (ClassCastException e) {
            throw new RuntimeException(handlerClassName + " must implement IEventCallbacks", e);
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(handlerClassName + " does not have a public parameterless constructor", e);
        }
    }

    static {
        add("ca.lukegrahamlandry.lib.base.EventCallbacks");
        add("ca.lukegrahamlandry.lib.packets.ForgeEventCallbacks");
        add("ca.lukegrahamlandry.lib.packets.FabricEventCallbacks");
        add("ca.lukegrahamlandry.lib.config.EventCallbacks");
        add("ca.lukegrahamlandry.lib.data.EventCallbacks");
    }

    public static List<IEventCallbacks> get(){
        return HANDLERS;
    }
}
