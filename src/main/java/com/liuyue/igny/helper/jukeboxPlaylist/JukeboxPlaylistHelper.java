package com.liuyue.igny.helper.jukeboxPlaylist;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public final class JukeboxPlaylistHelper {
    public static final Component PLAYING_NEXT = Component.translatable("record.playingNext");

    public static BlockPos lastInteracted;

    private JukeboxPlaylistHelper() {
    }

    public static Container containerAbove(Level level, BlockPos pos) {
        return level.getBlockEntity(pos.above()) instanceof Container container ? container : null;
    }

    public static int findNext(Container container, int fromSlot) {
        int size = container.getContainerSize();
        for (int offset = 1; offset <= size; offset++) {
            int slot = (fromSlot + offset) % size;
            if (slot < 0) {
                slot += size;
            }
            if (!container.getItem(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }
}
