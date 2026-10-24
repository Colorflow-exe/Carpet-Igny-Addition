package com.liuyue.igny.helper.structureBlockNoBlockUpdate;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class StructureNoUpdateSession {
    private static final Set<BlockPos> POSITIONS = ConcurrentHashMap.newKeySet();

    private static volatile ResourceKey<Level> dimension;

    private StructureNoUpdateSession() {
    }

    public static void begin(Level level) {
        dimension = level.dimension();
    }

    public static void end() {
        dimension = null;
        POSITIONS.clear();
    }

    public static void record(BlockPos pos) {
        POSITIONS.add(pos);
    }

    public static boolean covers(LevelAccessor level, BlockPos pos) {
        ResourceKey<Level> current = dimension;

        if (current == null || POSITIONS.isEmpty()) {
            return false;
        }

        if (level instanceof Level actual && !current.equals(actual.dimension())) {
            return false;
        }

        return POSITIONS.contains(pos);
    }

    public static boolean covers(BlockPos pos) {
        return dimension != null && POSITIONS.contains(pos);
    }
}
