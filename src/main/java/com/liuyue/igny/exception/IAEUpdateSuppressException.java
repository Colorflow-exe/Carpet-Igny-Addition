package com.liuyue.igny.exception;

import com.liuyue.igny.IGNYServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;

public class IAEUpdateSuppressException extends IllegalArgumentException{
    private final BlockPos suppressBlockPos;

    public IAEUpdateSuppressException(BlockPos blockPos, String message) {
        super(message);
        this.suppressBlockPos = blockPos;
    }

    public void onCatch(ServerPlayer player, Packet<ServerGamePacketListener> packet) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getGameProfile()
                //#if MC >= 12110
                //$$ .name()
                //#else
                .getName()
                //#endif
        ).append("在");
        if (packet instanceof ServerboundPlayerActionPacket actionC2SPacket) {
            switch (actionC2SPacket.getAction()) {
                case START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> builder.append("破坏方块");
                case DROP_ALL_ITEMS, DROP_ITEM -> builder.append("丢弃物品");
                case RELEASE_USE_ITEM -> builder.append("使用物品");
                case SWAP_ITEM_WITH_OFFHAND -> builder.append("交换主副手物品");
                default -> throw new IllegalStateException();
            }
        } else if (packet instanceof ServerboundUseItemOnPacket) {
            builder.append("放置或交互方块");
        } else {
            builder.append("发送").append(packet.getClass().getSimpleName()).append("数据包");
        }
        String levelPos = player.level().dimension().location() + "[" + suppressBlockPos.getX() + "," + suppressBlockPos.getY() + "," + suppressBlockPos.getZ() + "]";
        builder.append("时触发了IAE更新抑制，在").append(levelPos);
        IGNYServer.LOGGER.info(builder.toString());
    }
}
