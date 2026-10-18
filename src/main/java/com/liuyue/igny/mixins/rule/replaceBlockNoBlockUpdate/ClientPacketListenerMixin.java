package com.liuyue.igny.mixins.rule.replaceBlockNoBlockUpdate;

import com.liuyue.igny.helper.replaceBlockNoBlockUpdate.ReplaceBlockNoBlockUpdateHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @WrapOperation(
            method = "handleBlockUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;setServerVerifiedBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)V"
            )
    )
    private void igny$noNeighborUpdate(ClientLevel level, BlockPos pos, BlockState state, int flags,
                                       Operation<Void> original) {
        original.call(level, pos, state,
                ReplaceBlockNoBlockUpdateHelper.consumeSyncing(pos) ? flags & ~Block.UPDATE_NEIGHBORS : flags);
    }
}
