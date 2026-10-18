package com.liuyue.igny.helper.replaceBlockNoBlockUpdate;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ReplaceBlockNoBlockUpdateHelper {
    private static final Map<UUID, BlockPos> INTENT = new ConcurrentHashMap<>();

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    public static void recordIntent(UUID playerId, BlockPos pos) {
        INTENT.put(playerId, pos);
    }

    public static void clearIntent(UUID playerId) {
        INTENT.remove(playerId);
    }

    public static boolean matchesIntent(UUID playerId, BlockPos pos) {
        return pos.equals(INTENT.get(playerId));
    }

    public static void markActive() {
        ACTIVE.set(true);
    }

    public static void clearActive() {
        ACTIVE.remove();
    }

    public static boolean isActive() {
        return ACTIVE.get();
    }
}
