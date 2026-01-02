package com.liuyue.igny.utils.compat;

import com.liuyue.igny.utils.RuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class CarpetTISCompat {
    private static final MethodHandle TRY_REPLACE_METHOD;
    private static final boolean AVAILABLE;

    static {
        MethodHandle method = null;
        boolean avail = false;
        try {
            Class<?> CLASS = Class.forName("carpettisaddition.helpers.rule.yeetUpdateSuppressionCrash.UpdateSuppressionYeeter");
            method = MethodHandles.lookup().findStatic(
                    CLASS,
                    "tryReplaceWithWrapper",
                    MethodType.methodType(Throwable.class, Throwable.class, Level.class, BlockPos.class)
            );
            avail = true;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException ignored) {}
        TRY_REPLACE_METHOD = method;
        AVAILABLE = avail;
    }
    public static Throwable wrapIfNeeded(Throwable throwable, @Nullable Level world, @Nullable BlockPos pos) {
        if (!Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-tis-addition", "yeetUpdateSuppressionCrash")) && !AVAILABLE) return throwable;
        try {
            return (Throwable) TRY_REPLACE_METHOD.invokeExact(throwable, world, pos);
        } catch (Throwable ignored) {
            return throwable;
        }
    }
}