package com.liuyue.igny.helper.replaceBlockNoBlockUpdate;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ReplaceBlockNoBlockUpdateHelper {
    private static final long TIMEOUT_TICKS = 2L;

    private static final Map<UUID, Intent> INTENT = new ConcurrentHashMap<>();

    private static final Map<UUID, Window> WINDOW = new ConcurrentHashMap<>();

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    private record Intent(BlockPos pos, long gameTime) {
    }

    private record Window(BlockPos pos, long deadline) {
    }

    private static boolean expired(long gameTime, long since) {
        return gameTime - since > TIMEOUT_TICKS;
    }

    private static void openWindow(UUID playerId, BlockPos pos, long gameTime) {
        WINDOW.put(playerId, new Window(pos, gameTime + TIMEOUT_TICKS));
    }

    public static void recordIntent(UUID playerId, BlockPos pos, long gameTime) {
        INTENT.put(playerId, new Intent(pos, gameTime));
    }

    public static void clearIntent(UUID playerId) {
        INTENT.remove(playerId);
        WINDOW.remove(playerId);
    }

    public static void openClientWindow(UUID playerId, BlockPos pos, long gameTime) {
        openWindow(playerId, pos, gameTime);
    }

    public static boolean consumeIntent(UUID playerId, BlockPos pos, long gameTime) {
        Intent intent = INTENT.get(playerId);
        if (intent == null) {
            return false;
        }
        INTENT.remove(playerId);
        if (!intent.pos().equals(pos) || expired(gameTime, intent.gameTime())) {
            return false;
        }
        openWindow(playerId, pos, gameTime);
        return true;
    }

    public static boolean withinWindow(UUID playerId, BlockPos pos, long gameTime) {
        Window window = WINDOW.get(playerId);
        if (window == null) {
            return false;
        }
        WINDOW.remove(playerId);
        return gameTime <= window.deadline() && window.pos().equals(pos);
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
