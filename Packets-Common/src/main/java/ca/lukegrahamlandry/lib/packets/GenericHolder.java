package ca.lukegrahamlandry.lib.packets;

import java.util.function.Supplier;

public class GenericHolder<T> implements Supplier<T> {
    public final String clazz;
    public final T value;

    public GenericHolder(T value){
        this.value = value;
        this.clazz = value.getClass().getName();
    }

    @Override
    public T get() {
        return this.value;
    }
}

/*
Packets.register(PacketHolder.class)

Packets.register(MyMessage.class, Handlers::onRevieveMyMessage);
Packets.register(MyMessage.class);
Packets.toClient(player, new MyMessage())
Packets.toAllClients(new MyMessage())
Packets.toServer(new MyMessage())

if (clazz not in PacketRegistry and obj implemnets HasHandler){
    send new PacketHolder(obj)
}

PacketHolder extends GenericHolder implemnets HasHandler
handle: get().handle()
 */

