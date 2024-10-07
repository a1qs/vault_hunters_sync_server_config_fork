//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.vaulthunters.mixin;


import com.google.gson.Gson;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import iskallia.vault.config.Config;
import lv.id.bonne.vaulthunters.serversync.utils.IConfigReadFromString;


/**
 * The type Mixin config.
 */
@Mixin(value = Config.class, remap = false)
public abstract class ServerMixinConfig implements IConfigReadFromString
{
    @Shadow
    @Final
    public static Gson GSON;


    /**
     * This method encodes config  to JSON.
     * @param config the config
     * @return JSON string of config
     * @param <T>
     */
    @Override
    public <T extends Config> String encodeToJson(T config)
    {
        return GSON.toJson(config);
    }
}
