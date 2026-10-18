package com.liuyue.igny.mixins.rule.honeyBlockSupportsBlocks;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("deprecation")
@Mixin(MovingPistonBlock.class)
public abstract class MovingPistonBlockMixin extends BaseEntityBlock {
    @Shadow
    @Nullable
    protected abstract PistonMovingBlockEntity getBlockEntity(BlockGetter blockReader, BlockPos pos);

    protected MovingPistonBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        PistonMovingBlockEntity pistonBlockEntity = this.getBlockEntity(level, pos);
        return pistonBlockEntity != null && IGNYSettings.HONEY_BLOCK_SUPPORTS_BLOCKS.value()
                ? pistonBlockEntity.getMovedState().getBlockSupportShape(level, pos)
                : super.getBlockSupportShape(state, level, pos);
    }
}
