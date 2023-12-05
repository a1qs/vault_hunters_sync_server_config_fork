//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.proxy.client;


import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.serversync.ServerConfigSyncMod;
import lv.id.bonne.vaulthunters.serversync.proxy.ISyncModProxy;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/**
 * This class is used to handle client side proxy.
 */
public class SyncModClientProxy implements ISyncModProxy
{
    /**
     * Init client side proxy.
     */
    @Override
    public void init()
    {
        ISyncModProxy.super.init();
        MinecraftForge.EVENT_BUS.register(this);
    }


    /**
     * This method is used to handle player logout event to reload config files.
     * @param event player logout event.
     */
    @SubscribeEvent
    public void onLogout(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        if (event.getPlayer() != null && event.getConnection() != null)
        {
            ServerConfigSyncMod.LOGGER.info("Reloading config files");
            ModConfigs.register();
        }
    }
}