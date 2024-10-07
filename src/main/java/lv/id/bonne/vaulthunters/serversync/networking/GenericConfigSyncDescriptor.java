//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.networking;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
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
        (message, buffer) -> buffer.writeByteArray(
            Compress.compressString(message.configContent)).
            writeUtf(message.configName).
            writeUtf(message.parameter);

    /**
     * The decoder for this packet.
     */
    public static final Function<FriendlyByteBuf, GenericConfigSyncDescriptor> DECODER =
        buffer -> new GenericConfigSyncDescriptor(
            Compress.decompress(buffer.readByteArray()),
            buffer.readUtf(),
            buffer.readUtf());

    /**
     * The consumer for this packet that handles it.
     */
    public static final BiConsumer<GenericConfigSyncDescriptor, Supplier<NetworkEvent.Context>> CONSUMER =
        (message, context) ->
        {
            ServerConfigSyncMod.LOGGER.info("Pocket received. Start handling.");
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
     * The item name.
     */
    private final String parameter;


    /**
     * The constructor for this packet.
     */
    public GenericConfigSyncDescriptor(String configContent, String configName, String parameter)
    {
        this.configContent = configContent;
        this.configName = configName;
        this.parameter = parameter;
    }


    /**
     * Handles this packet. This registers the dimension on the client.
     * @param context The context of this packet.
     */
    public void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() ->
        {
            ServerConfigSyncMod.LOGGER.info("Start processing config: " + this.configName);

            try
            {
                Field field = ModConfigs.class.getField(this.configName);
                Object object = field.get(ModConfigs.class);

                if (object instanceof Config config)
                {
                    // Remove config from config set.
                    ModConfigs.CONFIGS.remove(config);
                    config.onUnload();

                    // Read config from JSON
                    ((IConfigReadFromString) config).decodeFromJson(this.configContent);
                    config = config.readConfig();

                    // Replace field value with new config.
                    field.set(ModConfigs.class, config);

                    ServerConfigSyncMod.LOGGER.info("Config Updated!");
                }
                else if (object instanceof Map)
                {
                    // I think there is no work around object casting.
                    Map<Object, Config> map = (Map<Object, Config>) object;

                    Optional<Object> optionalKey = map.keySet().stream().
                        filter(key -> key.toString().equals(this.parameter)).
                        findFirst();

                    optionalKey.ifPresent(key -> {
                        Config configValue = map.get(key);
                        // Remove config from config set.
                        ModConfigs.CONFIGS.remove(configValue);
                        configValue.onUnload();

                        // Read config from JSON
                        ((IConfigReadFromString) configValue).decodeFromJson(this.configContent);

                        configValue = configValue.readConfig();

                        // Replace config in map
                        map.replace(key, configValue);

                        ServerConfigSyncMod.LOGGER.info("Config Updated!");
                    });
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
