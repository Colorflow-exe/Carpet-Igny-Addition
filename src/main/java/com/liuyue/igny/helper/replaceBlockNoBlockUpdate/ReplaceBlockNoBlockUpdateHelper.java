package com.liuyue.igny.helper.replaceBlockNoBlockUpdate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ReplaceBlockNoBlockUpdateHelper {
    private static final Map<UUID, BlockPos> INTENT = new ConcurrentHashMap<>();

    private static final Map<UUID, Pending> PENDING = new ConcurrentHashMap<>();

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<BlockPos> REPLACING = new ThreadLocal<>();

    private static final ThreadLocal<BlockState> PLACED = new ThreadLocal<>();

    private static final Map<BlockPos, Integer> SYNCING = new ConcurrentHashMap<>();

    private record Pending(BlockPos pos, BlockState original) {
    }

    public static void recordIntent(UUID playerId, BlockPos pos) {
        INTENT.put(playerId, pos);
    }

    public static void clearIntent(UUID playerId) {
        INTENT.remove(playerId);
        PENDING.remove(playerId);
        REPLACING.remove();
        PLACED.remove();
        ACTIVE.remove();
    }

    public static void markPending(UUID playerId, BlockPos pos, BlockState original) {
        PENDING.put(playerId, new Pending(pos, original));
    }

    public static boolean consumeIntent(UUID playerId, BlockPos pos, BlockState original) {
        BlockPos intent = INTENT.get(playerId);
        INTENT.remove(playerId);
        if (intent == null || !intent.equals(pos)) {
            return false;
        }
        markPending(playerId, pos, original);
        return true;
    }

    public static boolean matchesPending(UUID playerId, BlockPos pos, BlockState current) {
        Pending pending = PENDING.get(playerId);
        if (pending == null) {
            return false;
        }
        PENDING.remove(playerId);
        return pending.pos().equals(pos) && current.getBlock() != pending.original().getBlock();
    }

    public static void markActive() {
        ACTIVE.set(true);
    }

    public static void markReplacing(BlockPos pos) {
        REPLACING.set(pos);
    }

    public static BlockPos takeReplacing() {
        BlockPos pos = REPLACING.get();
        REPLACING.remove();
        return pos;
    }

    public static void markPlaced(BlockState state) {
        PLACED.set(state);
    }

    public static BlockState takePlaced() {
        BlockState state = PLACED.get();
        PLACED.remove();
        return state;
    }

    public static void markSyncing(BlockPos pos) {
        SYNCING.put(pos, 2);
    }

    public static boolean consumeSyncing(BlockPos pos) {
        Integer left = SYNCING.get(pos);
        if (left == null) {
            return false;
        }
        if (left <= 1) {
            SYNCING.remove(pos);
        } else {
            SYNCING.put(pos, left - 1);
        }
        return true;
    }

    public static void clearActive() {
        ACTIVE.remove();
    }

    public static boolean isActive() {
        return ACTIVE.get();
    }
}
