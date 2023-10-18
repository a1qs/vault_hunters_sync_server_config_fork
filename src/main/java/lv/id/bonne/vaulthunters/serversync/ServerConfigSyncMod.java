package lv.id.bonne.vaulthunters.serversync;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import iskallia.vault.config.Config;
import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.serversync.config.Configuration;
import lv.id.bonne.vaulthunters.serversync.networking.GenericConfigSyncDescriptor;
import lv.id.bonne.vaulthunters.serversync.networking.ServerConfigSyncNetwork;
import lv.id.bonne.vaulthunters.serversync.utils.ConfigHelper;
import lv.id.bonne.vaulthunters.serversync.utils.IConfigReadFromString;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


/**
 * The main class for Vault Hunters Sync Server Config mod.
 */
@Mod(ServerConfigSyncMod.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfigSyncMod
{
    /**
     * The main class initialization.
     */
    public ServerConfigSyncMod()
    {
        MinecraftForge.EVENT_BUS.register(this);
        ServerConfigSyncMod.CONFIGURATION = new Configuration();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
            Configuration.GENERAL_SPEC,
            "vault_hunters_sync_server_config.toml");

        ConfigHelper.registerConfigs();
    }


    @SubscribeEvent
    public void onJoinServerEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer)
        {
            ConfigHelper.registerConfigs();

            CONFIGURATION.getListSyncConfigs().get().forEach(configLocation ->
            {
                try
                {
                    String configField = ConfigHelper.CONFIG_MAP.get(configLocation);

                    Field field = ModConfigs.class.getField(configField);
                    Object fieldObject = field.get(ModConfigs.class);

                    if (fieldObject instanceof Config config)
                    {
                        ServerConfigSyncNetwork.syncServerConfig(
                            new GenericConfigSyncDescriptor(((IConfigReadFromString) config).encodeToJson(config),
                                configField),
                            (ServerPlayer) event.getEntity());
                    }
                }
                catch (NoSuchFieldException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }


    @SubscribeEvent
    public void onJoinLocalEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer().isControlledByLocalInstance())
        {
            ModConfigs.register();
        }
    }


    @SubscribeEvent
    public void onJoinLocalEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getPlayer().isControlledByLocalInstance())
        {
            ModConfigs.register();
        }
    }


    /**
     * The main configuration file.
     */
    public static Configuration CONFIGURATION;

    /**
     * The logger for this mod.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The MOD_ID
     */
    public static final String MOD_ID = "vault_hunters_sync_server_config";
}
