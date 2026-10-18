package com.liuyue.igny.network.packet.replaceBlockNoBlockUpdate;

import net.minecraft.core.BlockPos;
//#if >= 1.20.5
import com.liuyue.igny.network.PacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
//#endif

public record StartReplacePayload(BlockPos pos)
        implements CustomPacketPayload //?>= 1.20.5
{
    //#if >= 1.20.5
    public static final Type<StartReplacePayload> TYPE = PacketUtil.createId("start_replace");

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, StartReplacePayload> CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull StartReplacePayload decode(RegistryFriendlyByteBuf buf) {
                    return new StartReplacePayload(buf.readBlockPos());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, StartReplacePayload value) {
                    buf.writeBlockPos(value.pos());
                }
            };
    //#endif
}
