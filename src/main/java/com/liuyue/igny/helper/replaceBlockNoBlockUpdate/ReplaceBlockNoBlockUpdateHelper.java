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

    /**
     * 客户端侧待同步的替换位置，值为还能抑制几次邻居更新（破坏、放置各一次）。
     */
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
    }

    public static void markPending(UUID playerId, BlockPos pos, BlockState original) {
        PENDING.put(playerId, new Pending(pos, original));
    }

    /**
     * 破坏方块时消费启动包留下的意向。此处方块尚未被清除，把原状态一并记下来，
     * 后面的放置就靠「目标位置已经不是这个方块」来确认破坏确实由本次替换完成。
     */
    public static boolean consumeIntent(UUID playerId, BlockPos pos, BlockState original) {
        BlockPos intent = INTENT.get(playerId);
        INTENT.remove(playerId);
        if (intent == null || !intent.equals(pos)) {
            return false;
        }
        markPending(playerId, pos, original);
        return true;
    }

    /**
     * 判定放置是否属于同一次替换：位置必须对得上，且原方块已经被清掉。
     * 手上方块与原方块相同时同样成立——那时位置早已变成空气，不会被误判成「没变化」。
     */
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

    /**
     * 记下本次放置对应的位置，供客户端在原版放置走完后确认替换是否成立。
     */
    public static void markReplacing(BlockPos pos) {
        REPLACING.set(pos);
    }

    public static BlockPos takeReplacing() {
        BlockPos pos = REPLACING.get();
        REPLACING.remove();
        return pos;
    }

    /**
     * 服务端同步替换结果时下发的方块更新带着 {@code UPDATE_NEIGHBORS}，
     * 会让客户端重算一遍红石和观察者。登记下来后在接收侧剥掉这个位，状态照常同步。
     */
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
