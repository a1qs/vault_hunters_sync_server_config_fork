//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.serversync.vaulthunters.mixin;


import com.google.gson.Gson;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.io.FileWriter;

import iskallia.vault.config.Config;
import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import lv.id.bonne.vaulthunters.serversync.utils.IConfigReadFromString;


@Mixin(value = Config.class, remap = false)
public abstract class ClientMixinConfig implements IConfigReadFromString
{
    @Shadow
    @Final
    public static Gson GSON;

    @Shadow
    protected String root;

    @Shadow
    public abstract String getName();

    @Shadow
    protected String extension;


    @Redirect(method = "readConfig",
        at = @At(value = "INVOKE", target = "Liskallia/vault/config/Config;getConfigFile()Ljava/io/File;",
        ordinal = 0))
    private File redirectToServerConfig(Config instance)
    {
        String localRoot = this.root + "server%s".formatted(File.separator);
        File serverConfig = new File(localRoot + this.getName() + this.extension);

        if (serverConfig.exists())
        {
            return serverConfig;
        }
        else
        {
            return new File(this.root + this.getName() + this.extension);
        }
    }


    /**
     * This method decodes Config from JSON.
     * @param string the string
     */
    @Override
    public void decodeFromJson(String string)
    {
        String localRoot = this.root + "server%s".formatted(File.separator);
        File serverConfig = new File(localRoot + this.getName() + this.extension);

        Config config = GSON.fromJson(string, ((Config) ((Object) this)).getClass());

        File dir = serverConfig.getParentFile();

        if (dir.exists() || dir.mkdirs()) {

            try
            {
                if (serverConfig.exists() || serverConfig.createNewFile())
                {
                    try (FileWriter writer = new FileWriter(serverConfig))
                    {
                        GSON.toJson(config, writer);
                        writer.flush();
                    }
                    catch (Exception e)
                    {
                        ServerConfigSyncMod.LOGGER.error("Failed to save file: " + e.getMessage());
                    }
                }
            }
            catch (Exception e)
            {
                ServerConfigSyncMod.LOGGER.error("Failed to create file: " + e.getMessage());
            }
        }
    }
}
