package lv.id.bonne.vaulthunters.serversync;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import lv.id.bonne.vaulthunters.serversync.config.Configuration;
import lv.id.bonne.vaulthunters.serversync.proxy.ISyncModProxy;
import lv.id.bonne.vaulthunters.serversync.proxy.client.SyncModClientProxy;
import lv.id.bonne.vaulthunters.serversync.proxy.server.SyncModServerProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


/**
 * The main class for Vault Hunters Sync Server Config mod.
 */
@Mod(ServerConfigSyncMod.MOD_ID)
public class ServerConfigSyncMod
{
    /**
     * The main class initialization.
     */
    public ServerConfigSyncMod()
    {
        ServerConfigSyncMod.CONFIGURATION = new Configuration();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,
            Configuration.GENERAL_SPEC,
            "vault_hunters_sync_server_config.toml");
        PROXY.init();
    }


    /**
     * A proxy separation between client and server.
     */
    public static final ISyncModProxy PROXY = DistExecutor.safeRunForDist(
        () -> SyncModClientProxy::new,
        () -> SyncModServerProxy::new);

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
