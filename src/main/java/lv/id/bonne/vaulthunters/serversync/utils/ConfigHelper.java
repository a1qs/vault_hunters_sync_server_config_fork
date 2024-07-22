//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.utils;


import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import iskallia.vault.config.Config;
import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import lv.id.bonne.vaulthunters.serversync.networking.GenericConfigSyncDescriptor;
import lv.id.bonne.vaulthunters.serversync.networking.ServerConfigSyncNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;


/**
 * The class that helps to deal with Vault Hunters Gear Attributes.
 */
public class ConfigHelper
{
    /**
     * This method populates all VaultGearAttributes into custom lists.
     */
    public static void registerConfigs()
    {
        CONFIG_MAP.clear();

        for (Field field : ModConfigs.class.getDeclaredFields())
        {
            try
            {
                Object fieldObject = field.get(ModConfigs.class);

                if (fieldObject instanceof Config config)
                {
                    CONFIG_MAP.put(config.getName(), field.getName());
                }
                else if (fieldObject instanceof Map map)
                {
                    for (Object key : map.keySet())
                    {
                        Config configValue = (Config) map.get(key);
                        CONFIG_MAP.put(configValue.getName(), field.getName());
                    }
                }
            }
            catch (IllegalAccessException ignored)
            {
            }
        }
    }


    /**
     * This method syncs server config with requested player.
     * @param player The player with whom config must be sync.
     */
    public static void syncServerConfigs(ServerPlayer player)
    {
        ServerConfigSyncMod.LOGGER.info("Preparing to send configs to player: " + player.getName().getString());

        ServerConfigSyncMod.CONFIGURATION.getListSyncConfigs().get().forEach(configLocation ->
        {
            try
            {
                // Over-complicated? Yes. Does it work? Yes.
                final String corrected = (
                    File.separator.equals("\\") ?
                        configLocation.replaceAll("/", "\\\\") :
                        configLocation).
                    replaceAll("\\\\", "/").
                    replace(".json", "");

                String configField = ConfigHelper.CONFIG_MAP.get(corrected);

                if (configField == null)
                {
                    ServerConfigSyncMod.LOGGER.error("Error, could not find config with name: " + configLocation);
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

        player.sendMessage(new TextComponent("Client synchronized with server configs.").
            withStyle(ChatFormatting.GRAY), ChatType.SYSTEM, net.minecraft.Util.NIL_UUID);
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * List that holds all config files to an actual config file.
     */
    public static final Map<String, String> CONFIG_MAP = new HashMap<>();
}
