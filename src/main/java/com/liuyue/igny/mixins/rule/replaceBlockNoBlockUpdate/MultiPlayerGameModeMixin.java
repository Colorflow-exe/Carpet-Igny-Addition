package com.liuyue.igny.mixins.rule.replaceBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if >= 1.20.5
import com.liuyue.igny.network.packet.replaceBlockNoBlockUpdate.StartReplacePayload;
//#else
/*$$import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import com.liuyue.igny.IGNYServer;
import net.minecraft.network.FriendlyByteBuf;$$*/
//#endif

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"))
    private void igny$notifyStartReplace(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockState newState = igny$simulate(minecraft, pos);
        if (newState == null) {
            return;
        }
        //#if >= 1.20.5
        if (!ClientPlayNetworking.canSend(StartReplacePayload.TYPE)) {
            return;
        }
        ClientPlayNetworking.send(new StartReplacePayload(pos));
        //#else
        /*$$if (!ClientPlayNetworking.canSend(IGNYServer.START_REPLACE_PACKET_ID)) {
            return;
        }
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(IGNYServer.START_REPLACE_PACKET_ID, buf);$$*/
        //#endif
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
