// MIT License
//
// Copyright (c) 2024 fcsailboat
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.liuyue.igny.mixins.features.commands.customItemMaxStackSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.utils.InventoryUtils;
import com.liuyue.igny.utils.RuleUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 1100)
public class ItemStackMixin {
    @Unique
    private final ItemStack thisStack = (ItemStack) (Object) this;

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void getMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        if (IGNYSettings.itemStackCountChanged.get()) {
            int originalMaxStackSize = cir.getReturnValue();
            if (CustomItemMaxStackSizeDataManager.hasCustomStack(thisStack.getItem())) {
                cir.setReturnValue(CustomItemMaxStackSizeDataManager.getCustomStackSize(thisStack.getItem()));
            }else if (ShulkerBoxStackableRuleEnabled()) {
                cir.setReturnValue(64);
            }else{
                cir.setReturnValue(originalMaxStackSize);
            }
        }
    }

    //#if MC >= 12005
    @Inject(method = "limitSize", at = @At("HEAD"), cancellable = true)
    private void limitSize(int maxCount, CallbackInfo ci) {
        if ((CustomItemMaxStackSizeDataManager.hasCustomStack(thisStack.getItem()) || ShulkerBoxStackableRuleEnabled()) && !IGNYSettings.itemStackCountChanged.get()) {
            ci.cancel();
        }
    }
    //#endif

    @Unique
    private boolean ShulkerBoxStackableRuleEnabled() {
        boolean nbtStackable = CustomItemMaxStackSizeDataManager.hasCustomStack(thisStack.getItem());
        return Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-org-addition", "shulkerBoxStackable"))
                && InventoryUtils.isShulkerBoxItem(thisStack)
                && (nbtStackable || InventoryUtils.isEmptyShulkerBox(thisStack));
    }
}
