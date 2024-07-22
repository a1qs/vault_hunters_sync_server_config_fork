//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.serversync.vaulthunters.mixin;


import com.mojang.brigadier.context.CommandContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import iskallia.vault.command.ReloadConfigsCommand;
import lv.id.bonne.vaulthunters.serversync.utils.ConfigHelper;
import net.minecraft.commands.CommandSourceStack;


@Mixin(value = ReloadConfigsCommand.class, remap = false)
public class MixinReloadConfigsCommand
{
    @Inject(method = "reloadConfigs", at = @At("RETURN"))
    private void reload(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir)
    {
        // Just in case register all configs once again. Should not be necessary
        ConfigHelper.registerConfigs();

        // Sync config with all players.
        context.getSource().getServer().getPlayerList().getPlayers().forEach(ConfigHelper::syncServerConfigs);
    }
}
