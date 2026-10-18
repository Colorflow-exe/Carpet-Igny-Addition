package com.liuyue.igny.mixins.rule.replaceBlockNoBlockUpdate;

import com.liuyue.igny.helper.replaceBlockNoBlockUpdate.ReplaceBlockNoBlockUpdateHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @WrapMethod(method = "handleUseItemOn")
    private void igny$clearReplaceBlock(ServerboundUseItemOnPacket packet, Operation<Void> original) {
        try {
            original.call(packet);
        } finally {
            ReplaceBlockNoBlockUpdateHelper.clearActive();
            ReplaceBlockNoBlockUpdateHelper.clearIntent(this.player.getUUID());
        }
    }
}
