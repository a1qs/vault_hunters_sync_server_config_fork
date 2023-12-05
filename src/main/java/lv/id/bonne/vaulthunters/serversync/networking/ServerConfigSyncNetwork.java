package lv.id.bonne.vaulthunters.serversync.networking;


import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


/**
 * This class manages the networking for ShardBound mod.
 */
public class ServerConfigSyncNetwork
{
    /**
     * The protocol version for the mod. We start with 1.
     */
    private static final String PROTOCOL_VERSION = "1";

    /**
     * The network channel for the mod.
     */
    public static final SimpleChannel CONFIG_CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(ServerConfigSyncMod.MOD_ID, "sync_config"),
        () -> PROTOCOL_VERSION,
        version -> true,
        PROTOCOL_VERSION::equals
    );


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
