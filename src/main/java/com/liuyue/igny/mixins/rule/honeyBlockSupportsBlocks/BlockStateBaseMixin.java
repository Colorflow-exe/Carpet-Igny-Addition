package com.liuyue.igny.mixins.rule.honeyBlockSupportsBlocks;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/SupportType;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void igny$honeyBlockSupport(BlockGetter level, BlockPos pos, Direction direction, SupportType supportType, CallbackInfoReturnable<Boolean> cir) {
        BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
        if (state instanceof BlockState blockState && state.is(Blocks.HONEY_BLOCK) && IGNYSettings.HONEY_BLOCK_SUPPORTS_BLOCKS.value()) {
            cir.setReturnValue(supportType.isSupporting(blockState, level, pos, direction));
        }
    }
}
