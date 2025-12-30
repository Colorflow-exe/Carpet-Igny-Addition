package com.liuyue.igny.client;

import com.liuyue.igny.client.renderer.highlightBlocks.HighlightBlocksRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

public class IGNYClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IGNYClientRegister.register();
        HighlightBlocksRenderer.init();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            HighlightBlocksRenderer.clearHighlight();
        });
    }
}
