package com.liuyue.igny.mixins.commands.fly;

import com.liuyue.igny.utils.interfaces.flyCommand.FlyState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @WrapOperation(
            method = "setGameModeForPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"
            )
    )
    private void igny$keepFlyAbility(GameType instance, Abilities abilities, Operation<Void> original) {
        boolean wasFlying = abilities.flying;
        original.call(instance, abilities);
        if (instance == GameType.CREATIVE || instance == GameType.SPECTATOR) {
            return;
        }
        if (((FlyState) this.player).igny$isFlyCommandOn()) {
            abilities.mayfly = true;
            abilities.flying = wasFlying;
        }
    }
}
