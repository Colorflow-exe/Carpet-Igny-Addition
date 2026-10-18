package com.liuyue.igny.mixins.rule.replaceBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.replaceBlockNoBlockUpdate.ReplaceBlockNoBlockUpdateHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean igny$replaceWithoutUpdate(ServerLevel instance, BlockPos pos, boolean b, Operation<Boolean> original) {
        if (!IGNYSettings.REPLACE_BLOCK_NO_BLOCK_UPDATE.value()
                || !ReplaceBlockNoBlockUpdateHelper.consumeIntent(this.player.getUUID(), pos, instance.getGameTime())) {
            return original.call(instance, pos, b);
        }
        try {
            FluidState fluidState = instance.getFluidState(pos);
            return instance.setBlock(pos, IGNYSettings.CREATIVE_DESTROY_WATERLOGGED_BLOCK_NO_WATER.value()
                    ? Blocks.AIR.defaultBlockState()
                    : fluidState.createLegacyBlock(),
                    Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE | (b ? Block.UPDATE_MOVE_BY_PISTON : 0));
        } finally {
            ReplaceBlockNoBlockUpdateHelper.markActive();
        }
    }
}
