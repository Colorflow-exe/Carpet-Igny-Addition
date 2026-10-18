package com.liuyue.igny.mixins.rule.replaceBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.replaceBlockNoBlockUpdate.ReplaceBlockNoBlockUpdateHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if >= 1.20.5
import com.liuyue.igny.network.packet.replaceBlockNoBlockUpdate.StartReplacePayload;
//#else
/*$$import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import com.liuyue.igny.IGNYServer;
import net.minecraft.network.FriendlyByteBuf;$$*/
//#endif

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Unique
    private static final int igny$replaceFlags =
            Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE;

    @Invoker("startPrediction")
    protected abstract void igny$startPrediction(ClientLevel level, PredictiveAction action);

    @ModifyArg(
            method = "destroyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            ),
            index = 2
    )
    private int igny$noUpdateBreak(int flags) {
        if (ReplaceBlockNoBlockUpdateHelper.isActive()) {
            ReplaceBlockNoBlockUpdateHelper.clearActive();
            return igny$replaceFlags;
        }
        return flags;
    }

    @Inject(method = "startDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V", ordinal = 0), cancellable = true)
    private void igny$replace(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (igny$simulate(minecraft, pos) == null || minecraft.level == null || minecraft.player == null) {
            return;
        }
        BlockState original = minecraft.level.getBlockState(pos);
        ReplaceBlockNoBlockUpdateHelper.markActive();
        if (!((MultiPlayerGameMode) (Object) this).destroyBlock(pos)) {
            ReplaceBlockNoBlockUpdateHelper.clearActive();
            return;
        }
        ReplaceBlockNoBlockUpdateHelper.markPending(minecraft.player.getUUID(), pos, original);
        ReplaceBlockNoBlockUpdateHelper.markReplacing(pos);
        ReplaceBlockNoBlockUpdateHelper.markSyncing(pos);
        //#if >= 1.20.5
        ClientPlayNetworking.send(new StartReplacePayload(pos));
        //#else
        /*$$FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(IGNYServer.START_REPLACE_PACKET_ID, buf);$$*/
        //#endif
        cir.setReturnValue(true);
    }

    @Inject(method = "performUseItemOn", at = @At(value = "RETURN"))
    private void igny$sendBreakPacket(LocalPlayer player, InteractionHand hand, BlockHitResult result,
                                      CallbackInfoReturnable<InteractionResult> cir) {
        BlockPos pos = ReplaceBlockNoBlockUpdateHelper.takeReplacing();
        if (pos == null || !(player.getItemInHand(hand).getItem() instanceof BlockItem blockItem)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.level.getBlockState(pos).getBlock() != blockItem.getBlock()) {
            return;
        }
        igny$startPrediction(minecraft.level, sequence -> new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, result.getDirection(), sequence));
    }

    @Unique
    private static BlockState igny$simulate(Minecraft minecraft, BlockPos pos) {
        if (!IGNYSettings.REPLACE_BLOCK_NO_BLOCK_UPDATE.value()
                || minecraft.player == null
                || minecraft.level == null
                || !minecraft.player.isCreative()
                || !minecraft.options.keyAttack.isDown()
                || !minecraft.options.keyUse.isDown()) {
            return null;
        }
        ItemStack held = minecraft.player.getMainHandItem();
        if (!(held.getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        if (minecraft.level.getBlockState(pos).isAir()) {
            return null;
        }
        BlockState newState = blockItem.getBlock().defaultBlockState();
        if (!newState.canSurvive(minecraft.level, pos)) {
            return null;
        }
        return newState;
    }
}
