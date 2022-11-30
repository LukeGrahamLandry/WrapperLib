package ca.lukegrahamlandry.lib.packets;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgePacketRegistry {
    public static SimpleChannel channel;
    static int i = 0;

    public static void registerPacketChannel(){
        channel = NetworkRegistry.newSimpleChannel(new ResourceLocation("wrapperlib", ForgePacketRegistry.class.getName().toLowerCase(Locale.US)), () -> "1.0", s -> true, s -> true);
        ForgePacketRegistry packet = new ForgePacketRegistry();
        channel.registerMessage(i++, GenericHolder.class, packet::encode, packet::decode, packet::handle);
    }

    public void encode(GenericHolder<?> message, FriendlyByteBuf buffer){
        JsonElement data = GsonHelper.GSON.toJsonTree(message);
        buffer.writeUtf(data.toString());
    }

    public GenericHolder<?> decode(FriendlyByteBuf buffer){
        String data = buffer.readUtf();
        return GsonHelper.GSON.fromJson(data, GenericHolder.class);
    }

    public void handle(GenericHolder<?> message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            if (context.get().getSender() == null) {
                Consumer action = PacketWrapper.getClientHandler(message.clazz);
                if (action != null) action.accept(message.value);
            }
            else {
                BiConsumer action = PacketWrapper.getServerHandler(message.clazz);
                if (action != null) action.accept(context.get().getSender(), message.value);
            }
        });
        context.get().setPacketHandled(true);
    }
}
