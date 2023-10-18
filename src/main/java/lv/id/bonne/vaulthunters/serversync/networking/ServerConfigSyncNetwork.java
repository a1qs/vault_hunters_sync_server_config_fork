package lv.id.bonne.vaulthunters.serversync.networking;



import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


/**
 * This class manages the networking for ShardBound mod.
 */
@Mod.EventBusSubscriber(modid = ServerConfigSyncMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfigSyncNetwork
{
    /**
     * The protocol version for the mod. We start with 1.
     */
    private static final String PROTOCOL_VERSION = "1";

    /**
     * The index of the next packet to register.
     */
    private static int index = 0;

    /**
     * The network channel for the mod.
     */
    private static final SimpleChannel CONFIG_CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(ServerConfigSyncMod.MOD_ID, "sync_config"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );


    /**
     * Main method that registers all the packets to the forge.
     * @param event The main setup event.
     */
    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event)
    {
        // Register dimension to the client
        ServerConfigSyncNetwork.CONFIG_CHANNEL.messageBuilder(GenericConfigSyncDescriptor.class,
                ServerConfigSyncNetwork.index++,
                NetworkDirection.PLAY_TO_CLIENT).
            encoder(GenericConfigSyncDescriptor.ENCODER).
            decoder(GenericConfigSyncDescriptor.DECODER).
            consumer(GenericConfigSyncDescriptor.CONSUMER).
            add();
    }


    /**
     * This method sends a given server message to the given player.
     * @param message Packet Message that need to be sent to everyone.
     * @param player  The player that receives the message.
     * @param <T> The message class.
     */
    public static <T> void syncServerConfig(T message, ServerPlayer player)
    {
        ServerConfigSyncNetwork.CONFIG_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
