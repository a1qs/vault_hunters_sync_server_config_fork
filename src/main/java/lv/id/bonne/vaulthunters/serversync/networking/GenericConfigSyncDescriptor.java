//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.networking;


import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import iskallia.vault.config.Config;
import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import lv.id.bonne.vaulthunters.serversync.utils.Compress;
import lv.id.bonne.vaulthunters.serversync.utils.IConfigReadFromString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


/**
 * The class that handles the packet that syncs config.
 */
public class GenericConfigSyncDescriptor
{
    /**
     * The encoder for this packet.
     */
    public static final BiConsumer<GenericConfigSyncDescriptor, FriendlyByteBuf> ENCODER =
        (message, buffer) -> buffer.writeByteArray(Compress.compressString(message.configContent)).writeUtf(message.configName);

    /**
     * The decoder for this packet.
     */
    public static final Function<FriendlyByteBuf, GenericConfigSyncDescriptor> DECODER =
        buffer -> new GenericConfigSyncDescriptor(Compress.decompress(buffer.readByteArray()), buffer.readUtf());

    /**
     * The consumer for this packet that handles it.
     */
    public static final BiConsumer<GenericConfigSyncDescriptor, Supplier<NetworkEvent.Context>> CONSUMER =
        (message, context) ->
        {
            NetworkEvent.Context cont = context.get();
            message.handle(cont);
        };

    /**
     * The config content.
     */
    private final String configContent;

    /**
     * The config name.
     */
    private final String configName;


    /**
     * The constructor for this packet.
     */
    public GenericConfigSyncDescriptor(String configContent, String configName)
    {
        this.configContent = configContent;
        this.configName = configName;
    }


    /**
     * Handles this packet. This registers the dimension on the client.
     * @param context The context of this packet.
     */
    public void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() ->
        {
            try
            {
                Field field = ModConfigs.class.getField(this.configName);
                Object object = field.get(ModConfigs.class);

                if (object instanceof Config config)
                {
                    ModConfigs.CONFIGS.remove(config);
                    config = ((IConfigReadFromString) config).decodeFromJson(this.configContent);
                    field.set(ModConfigs.class, config);
                    ModConfigs.CONFIGS.add(config);
                }
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                ServerConfigSyncMod.LOGGER.error("Error while reading config: " + this.configName, e);
            }

            // do things in packet
        });

        context.setPacketHandled(true);
    }
}
