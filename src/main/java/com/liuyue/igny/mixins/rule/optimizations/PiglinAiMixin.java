package com.liuyue.igny.mixins.rule.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.accessors.optimizations.IPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @Inject(method = "wantsToPickup",at=@At(value = "HEAD"),cancellable = true)
    private static void cancelPickup(Piglin piglin, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir){
        if (itemStack.getItem() == Items.GOLD_INGOT){
            return;
        }
        int count = ((IPiglin)piglin).carpet_Igny_Addition$getPiglinCrammingCount();
        if (count > IGNYSettings.optimizedPiglinLimit && IGNYSettings.optimizedPiglin && Math.random() * count >= 1){
            cir.setReturnValue(false);
        }
    }
}
