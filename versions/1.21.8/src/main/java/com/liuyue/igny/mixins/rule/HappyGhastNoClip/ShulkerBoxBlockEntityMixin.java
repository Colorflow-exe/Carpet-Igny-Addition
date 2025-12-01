package com.liuyue.igny.mixins.rule.HappyGhastNoClip;

import carpet.CarpetSettings;
import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
    @WrapOperation(method = "moveCollidedEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"
    ))
    private PushReaction getPistonBehaviourOfNoClipPlayers(Entity instance, Operation<PushReaction> original)
    {
        if (instance instanceof Player && instance.getRootVehicle() instanceof HappyGhast&& IGNYSettings.HappyGhastNoClip)
            return PushReaction.IGNORE;
        return original.call(instance);
    }
}
