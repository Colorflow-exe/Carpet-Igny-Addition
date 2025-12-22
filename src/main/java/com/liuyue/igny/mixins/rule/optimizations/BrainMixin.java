package com.liuyue.igny.mixins.rule.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.accessors.optimizations.IEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Brain.class,priority = 1500)
public class BrainMixin<E extends LivingEntity> {
    @Inject(method = "tickSensors",at = @At(value = "HEAD"),cancellable = true)
    private void tickSensors(ServerLevel serverLevel, E livingEntity, CallbackInfo ci){
        int count = ((IEntity)livingEntity).carpet_Igny_Addition$getCrammingCount();
        if (count > IGNYSettings.optimizedEntityLimit){
            ci.cancel();
        }
    }
}
