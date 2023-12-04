//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.utils;


import java.lang.reflect.Field;
import java.util.*;

import iskallia.vault.config.Config;
import iskallia.vault.init.ModConfigs;


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



// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * List that holds all config files to an actual config file.
     */
    public static final Map<String, String> CONFIG_MAP = new HashMap<>();
}
