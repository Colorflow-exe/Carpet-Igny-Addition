package com.liuyue.igny.mixins.rule.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.accessors.optimizations.IPiglin;
import com.liuyue.igny.accessors.optimizations.IWarden;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "travel",at=@At(value = "HEAD"),cancellable = true)
    private void travel(Vec3 vec3, CallbackInfo ci){
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Piglin){
            int count = ((IPiglin)entity).carpet_Igny_Addition$getPiglinCrammingCount();
            if (count > IGNYSettings.optimizedPiglinLimit && IGNYSettings.optimizedPiglin && Math.random() * count >= 1){
                ci.cancel();
                return;
            }
        }
        if (entity instanceof Warden){
            int count = ((IWarden)entity).carpet_Igny_Addition$getWardenCrammingCount();
            if (count > IGNYSettings.optimizedWardenLimit && IGNYSettings.optimizedWarden && Math.random() * count >= 1){
                ci.cancel();
            }
        }
    }

    @Inject(method = "pushEntities",at= @At(value = "HEAD"), cancellable = true)
    private void pushEntities(CallbackInfo ci){
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Piglin){
            int count = ((IPiglin)self).carpet_Igny_Addition$getPiglinCrammingCount();
            if (count > IGNYSettings.optimizedPiglinLimit && IGNYSettings.optimizedPiglin && Math.random() * count >= 1){
                ci.cancel();
                return;
            }
        }
        if (self instanceof Warden){
            int count = ((IWarden)self).carpet_Igny_Addition$getWardenCrammingCount();
            if (count > IGNYSettings.optimizedWardenLimit && IGNYSettings.optimizedWarden && Math.random() * count >= 1){
                ci.cancel();
            }
        }
    }
}
