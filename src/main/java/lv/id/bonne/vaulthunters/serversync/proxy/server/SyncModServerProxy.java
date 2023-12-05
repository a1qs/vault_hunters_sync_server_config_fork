//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.proxy.server;


import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import iskallia.vault.config.Config;
import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import lv.id.bonne.vaulthunters.serversync.networking.GenericConfigSyncDescriptor;
import lv.id.bonne.vaulthunters.serversync.networking.ServerConfigSyncNetwork;
import lv.id.bonne.vaulthunters.serversync.proxy.ISyncModProxy;
import lv.id.bonne.vaulthunters.serversync.utils.ConfigHelper;
import lv.id.bonne.vaulthunters.serversync.utils.IConfigReadFromString;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/**
 * This class is used to handle server side proxy.
 */
public class SyncModServerProxy implements ISyncModProxy
{
    /**
     * Init server side proxy.
     */
    @Override
    public void init()
    {
        ISyncModProxy.super.init();
        MinecraftForge.EVENT_BUS.register(this);
    }


    /**
     * This method is used to handle player login event to send new config files to player.
     * @param event player login event.
     */
    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            ConfigHelper.registerConfigs();

            ServerConfigSyncMod.LOGGER.info("Preparing to send configs to player: " + player.getName().getString());

            ServerConfigSyncMod.CONFIGURATION.getListSyncConfigs().get().forEach(configLocation ->
            {
                try
                {
                    // Over-complicated? Yes. Does it work? Yes.
                    final String corrected = (
                        File.separator.equals("\\") ?
                            configLocation.replaceAll("/", "\\\\") :
                            configLocation.replaceAll("\\\\", "/")).
                        replace(".json", "");

                    String configField = ConfigHelper.CONFIG_MAP.get(corrected);

                    if (configField == null)
                    {
                        // Field does not exist. Skipping.
                        return;
                    }

                    Field field = ModConfigs.class.getField(configField);
                    Object fieldObject = field.get(ModConfigs.class);

                    if (fieldObject instanceof Config config)
                    {
                        ServerConfigSyncMod.LOGGER.info("Sending updated config file: " + configLocation);
                        ServerConfigSyncNetwork.syncServerConfig(
                            new GenericConfigSyncDescriptor(((IConfigReadFromString) config).encodeToJson(config),
                                configField,
                                ""),
                            player);
                    }
                    else if (fieldObject instanceof Map)
                    {
                        // I think there is no way around object casting.
                        Map<Object, Config> map = (Map<Object, Config>) fieldObject;

                        map.entrySet().stream().
                            filter(entry -> entry.getValue().getName().equals(corrected)).
                            findFirst().
                            ifPresent(entry ->
                            {
                                ServerConfigSyncMod.LOGGER.info("Sending updated config file: " + configLocation);

                                ServerConfigSyncNetwork.syncServerConfig(
                                    new GenericConfigSyncDescriptor(((IConfigReadFromString) entry.getValue()).
                                        encodeToJson(entry.getValue()),
                                        configField,
                                        entry.getKey().toString()),
                                    player);
                            });
                    }
                }
                catch (NoSuchFieldException | IllegalAccessException e)
                {
                    ServerConfigSyncMod.LOGGER.error("Error while preparing to send config: " + configLocation, e);
                }
            });
        }
    }
}
