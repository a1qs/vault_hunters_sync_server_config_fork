//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.utils;


import iskallia.vault.config.Config;


/**
 * This interface allows to encode and decode Configs from String.
 */
public interface IConfigReadFromString
{
    /**
     * Read from string t.
     *
     * @param string the string
     * @return the t
     */
    default void decodeFromJson(String string)
    {
    }


    /**
     * To gson string string.
     *
     * @param <T> the type parameter
     * @param config the config
     * @return the string
     */
    default <T extends Config> String encodeToJson(T config)
    {
        return "";
    }
}
