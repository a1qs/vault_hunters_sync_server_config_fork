package lv.id.bonne.vaulthunters.serversync.networking;

import iskallia.vault.config.Config;
import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import lv.id.bonne.vaulthunters.serversync.utils.Compress;
import lv.id.bonne.vaulthunters.serversync.utils.IConfigReadFromString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericCustomConfigSyncDescriptor extends GenericConfigSyncDescriptor {
    private final String classPath;

    /**
     * The encoder for this packet.
     */
    public static final BiConsumer<GenericCustomConfigSyncDescriptor, FriendlyByteBuf> ENCODER =
            (message, buffer) -> buffer.writeByteArray(
                            Compress.compressString(message.configContent)).
                    writeUtf(message.configName).
                    writeUtf(message.parameter).
                    writeUtf(message.classPath);

    /**
     * The decoder for this packet.
     */
    public static final Function<FriendlyByteBuf, GenericCustomConfigSyncDescriptor> DECODER =
            buffer -> new GenericCustomConfigSyncDescriptor(
                    Compress.decompress(buffer.readByteArray()),
                    buffer.readUtf(),
                    buffer.readUtf(),
                    buffer.readUtf());

    /**
     * The consumer for this packet that handles it.
     */
    public static final BiConsumer<GenericCustomConfigSyncDescriptor, Supplier<NetworkEvent.Context>> CONSUMER =
            (message, context) ->
            {
                ServerConfigSyncMod.LOGGER.info("Pocket received. Start handling.");
                NetworkEvent.Context cont = context.get();
                message.handle(cont);
            };

    /**
     * The constructor for this packet.
     */
    public GenericCustomConfigSyncDescriptor(String configContent, String configName, String parameter, String classPath) {
        super(configContent, configName, parameter);
        this.classPath = classPath;

    }

    /**
     * Handles this packet. This processed the config files.
     * @param context The context of this packet.
     */
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerConfigSyncMod.LOGGER.info("Start processing custom config: " + this.configName);

            try
            {
                Class clazz = Class.forName(classPath);
                Field field = clazz.getField(this.configName);
                Object object = field.get(clazz);


                if (object instanceof Config config) {
                    // Remove config from config set.
                    ModConfigs.CONFIGS.remove(config);
                    config.onUnload();

                    // Read config from JSON

                    ((IConfigReadFromString) config).decodeFromJson(this.configContent);
                    config = config.readConfig();

                    // Replace field value with new config.
                    field.set(clazz, config);

                    ServerConfigSyncMod.LOGGER.info("Custom Config Updated!");
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

                        ServerConfigSyncMod.LOGGER.info("Custom Config Map Updated!");
                    });
                }
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                ServerConfigSyncMod.LOGGER.error("Error while reading custom config: " + this.configName, e);
            }
        });

        context.setPacketHandled(true);
    }
}
