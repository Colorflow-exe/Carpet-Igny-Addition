package com.liuyue.igny.mixins.rule.replaceBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.replaceBlockNoBlockUpdate.ReplaceBlockNoBlockUpdateHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "place", at = @At(value = "HEAD"))
    private void igny$markReplace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player == null || !IGNYSettings.REPLACE_BLOCK_NO_BLOCK_UPDATE.value()) {
            return;
        }
        if (ReplaceBlockNoBlockUpdateHelper.withinWindow(player.getUUID(), context.getClickedPos(),
                context.getLevel().getGameTime())) {
            ReplaceBlockNoBlockUpdateHelper.markActive();
        }
    }

    @ModifyArg(
            method = "placeBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            ),
            index = 2
    )
    private int igny$noBlockUpdate(int flags) {
        if (ReplaceBlockNoBlockUpdateHelper.isActive()) {
            ReplaceBlockNoBlockUpdateHelper.clearActive();
            return Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE;
        }
        return flags;
    }
}
