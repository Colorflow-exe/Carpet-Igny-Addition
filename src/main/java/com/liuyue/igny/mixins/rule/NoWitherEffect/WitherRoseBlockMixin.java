package com.liuyue.igny.mixins.rule.NoWitherEffect;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherRoseBlock.class)
public class WitherRoseBlockMixin {
    @Inject(method="entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), cancellable = true)
    private void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        if (IGNYSettings.NoWitherEffect) {
            ci.cancel();
        }
    }
}
