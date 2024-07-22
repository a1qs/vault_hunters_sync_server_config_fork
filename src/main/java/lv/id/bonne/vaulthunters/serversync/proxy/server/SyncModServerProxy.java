//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.proxy.server;


import lv.id.bonne.vaulthunters.serversync.proxy.ISyncModProxy;
import lv.id.bonne.vaulthunters.serversync.utils.ConfigHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
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
            ConfigHelper.syncServerConfigs(player);
        }
    }


    /**
     * Register configs once server is started.
     * @param event Server started event
     */
    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event)
    {
        ConfigHelper.registerConfigs();
    }
}
