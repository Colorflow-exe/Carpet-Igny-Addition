package com.liuyue.igny.mixins.rule.allowBrittleIceGenerated;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @WrapOperation(method = "shouldSpreadLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean setBlockAndUpdate(Level instance, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return shouldGenerate(instance, pos) ? original.call(instance, pos, state) : false;
    }

    @Unique
    private boolean shouldGenerate(Level level, BlockPos pos) {
        if (IGNYSettings.ALLOW_BRITTLE_ICE_GENERATED.value().equals("exposed")) {
            return level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos) == pos.getY();
        }
        return IGNYSettings.ALLOW_BRITTLE_ICE_GENERATED.value().equals("true");
    }
}
