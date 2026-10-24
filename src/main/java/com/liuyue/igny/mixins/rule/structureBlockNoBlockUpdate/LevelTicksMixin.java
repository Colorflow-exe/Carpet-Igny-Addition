package com.liuyue.igny.mixins.rule.structureBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.structureBlockNoBlockUpdate.StructureNoUpdateSession;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelTicks.class)
public class LevelTicksMixin {
    @Inject(method = "schedule", at = @At(value = "HEAD"), cancellable = true)
    private void schedule(ScheduledTick<?> scheduledTick, CallbackInfo ci) {
        if (IGNYSettings.STRUCTURE_BLOCK_NO_BLOCK_UPDATE.value()
                && StructureNoUpdateSession.covers(scheduledTick.pos())) {
            ci.cancel();
        }
    }
}
