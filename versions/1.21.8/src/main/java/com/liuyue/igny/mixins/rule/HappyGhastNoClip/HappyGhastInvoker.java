package com.liuyue.igny.mixins.rule.HappyGhastNoClip;

import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HappyGhast.class)
public interface HappyGhastInvoker {
    @Invoker("scanPlayerAboveGhast")
    boolean invokeScanPlayerAboveGhast();
}
