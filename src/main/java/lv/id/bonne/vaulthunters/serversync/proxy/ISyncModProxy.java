//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.proxy;


import lv.id.bonne.vaulthunters.serversync.networking.GenericConfigSyncDescriptor;
import lv.id.bonne.vaulthunters.serversync.networking.GenericCustomConfigSyncDescriptor;
import lv.id.bonne.vaulthunters.serversync.networking.ServerConfigSyncNetwork;
import net.minecraftforge.network.NetworkDirection;


/**
 * This interface is used to mark proxy classes.
 */
public interface ISyncModProxy
{
    /**
     * This method is used to initialize Common Proxy code.
     */
    default void init()
    {
        ServerConfigSyncNetwork.CONFIG_CHANNEL.messageBuilder(GenericConfigSyncDescriptor.class,
                0,
                NetworkDirection.PLAY_TO_CLIENT).
            encoder(GenericConfigSyncDescriptor.ENCODER).
            decoder(GenericConfigSyncDescriptor.DECODER).
            consumer(GenericConfigSyncDescriptor.CONSUMER).
            noResponse().
            add();

        ServerConfigSyncNetwork.CONFIG_CHANNEL.messageBuilder(GenericCustomConfigSyncDescriptor.class,
                        1,
                        NetworkDirection.PLAY_TO_CLIENT).
                encoder(GenericCustomConfigSyncDescriptor.ENCODER).
                decoder(GenericCustomConfigSyncDescriptor.DECODER).
                consumer(GenericCustomConfigSyncDescriptor.CONSUMER).
                noResponse().
                add();
    };
}
