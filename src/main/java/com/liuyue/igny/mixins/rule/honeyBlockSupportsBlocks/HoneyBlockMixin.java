package com.liuyue.igny.mixins.rule.honeyBlockSupportsBlocks;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoneyBlock.class)
public abstract class HoneyBlockMixin extends HalfTransparentBlock {
    protected HoneyBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    protected @NotNull VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return IGNYSettings.HONEY_BLOCK_SUPPORTS_BLOCKS.value() ?
                Shapes.block() : super.getBlockSupportShape(state, level, pos);
    }
}
