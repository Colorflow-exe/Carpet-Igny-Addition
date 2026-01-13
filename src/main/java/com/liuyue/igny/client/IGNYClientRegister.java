package com.liuyue.igny.client;

import com.liuyue.igny.client.renderer.highlightBlocks.HighlightBlocksRenderer;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.network.packet.block.HighlightPayload;
import com.liuyue.igny.network.packet.block.RemoveHighlightPayload;
import com.liuyue.igny.network.packet.config.SyncCustomStackSizePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//#if MC < 12005
//$$ import com.liuyue.igny.IGNYServer;
//$$ import net.minecraft.core.BlockPos;
//$$ import java.util.Map;
//#else
//$$ import com.liuyue.igny.network.packet.block.HighlightPayload;
//#endif

public class IGNYClientRegister {
    public static void register() {
        registerNetworkPackReceiver();
    }

    private static void registerNetworkPackReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.HIGHLIGHT_PACKET_ID,
                //#else
                HighlightPayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     BlockPos pos = buf.readBlockPos();
                //$$     int color = buf.readInt();
                //$$     int duration = buf.readInt();
                //$$     boolean permanent = buf.readBoolean();
                //$$     client.execute(() -> HighlightBlocksRenderer.addHighlight(pos, color, duration, permanent));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        HighlightBlocksRenderer.addHighlight(payload.pos(), payload.color(), payload.durationTicks(), payload.permanent())
                )
                //#endif
        );
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.REMOVE_HIGHLIGHT_PACKET_ID,
                //#else
                RemoveHighlightPayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     BlockPos pos = buf.readBlockPos();
                //$$     client.execute(() -> HighlightBlocksRenderer.removeHighlight(pos));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        HighlightBlocksRenderer.removeHighlight(payload.pos())
                )
                //#endif
        );
        //#if MC >= 12006
        ClientPlayNetworking.registerGlobalReceiver(
                //#if MC < 12005
                //$$ IGNYServer.SYNC_STACK_SIZE_PACKET_ID,
                //#else
                SyncCustomStackSizePayload.TYPE,
                //#endif
                //#if MC < 12005
                //$$ (client, handler, buf, responseSender) -> {
                //$$     int size = buf.readVarInt();
                //$$     Map<String, Integer> map = new java.util.HashMap<>(size);
                //$$     for (int i = 0; i < size; i++) {
                //$$         map.put(buf.readUtf(), buf.readVarInt());
                //$$     }
                //$$     client.execute(() -> CustomItemMaxStackSizeDataManager.clientUpdateData(map));
                //$$ }
                //#else
                (payload, context) -> context.client().execute(() ->
                        CustomItemMaxStackSizeDataManager.clientUpdateData(payload.customStacks())
                )
                //#endif
        );
        //#endif
    }
}
