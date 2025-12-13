package com.liuyue.igny.mixins.rule.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.accessors.optimizations.IPiglin;
import com.liuyue.igny.accessors.optimizations.IWarden;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Brain.class,priority = 1500)
public class BrainMixin<E extends LivingEntity> {
    @Inject(method = "tickSensors",at=@At(value = "HEAD"),cancellable = true)
    private void tickSensors(ServerLevel serverLevel, E livingEntity, CallbackInfo ci){
        if (livingEntity instanceof Piglin piglin){
            int count = ((IPiglin)piglin).carpet_Igny_Addition$getPiglinCrammingCount();
            if (count > IGNYSettings.optimizedPiglinLimit && IGNYSettings.optimizedPiglin && Math.random() * count >= 1){
                ci.cancel();
                return;
            }
        }
        if (livingEntity instanceof Warden warden){
            int count = ((IWarden)warden).carpet_Igny_Addition$getWardenCrammingCount();
            if (count > IGNYSettings.optimizedWardenLimit && IGNYSettings.optimizedWarden && Math.random() * count >= 1){
                ci.cancel();
            }
        }
    }

}
