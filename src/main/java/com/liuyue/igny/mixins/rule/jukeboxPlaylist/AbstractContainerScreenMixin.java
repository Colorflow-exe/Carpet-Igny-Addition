package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import com.liuyue.igny.utils.interfaces.jukeboxPlaylist.JukeboxPlaylistAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Shadow
    protected Slot hoveredSlot;

    @Inject(method = "getTooltipFromContainerItem", at = @At(value = "RETURN"))
    private void igny$appendPlayingNext(ItemStack stack, CallbackInfoReturnable<List<Component>> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockPos pos = JukeboxPlaylistHelper.lastInteracted;
        if (minecraft.level == null || minecraft.player == null || this.hoveredSlot == null || pos == null) {
            return;
        }
        if (!(minecraft.level.getBlockEntity(pos) instanceof Container target)
                || !(minecraft.level.getBlockEntity(pos.below()) instanceof JukeboxBlockEntity jukebox)) {
            return;
        }
        AbstractContainerMenu menu = minecraft.player.containerMenu;
        if (menu.slots.isEmpty()) {
            return;
        }
        Container container = menu.slots.get(0).container;
        if (container.getContainerSize() < target.getContainerSize()) {
            return;
        }
        int position = ((JukeboxPlaylistAccess) jukebox).igny$getPlaylistPosition();
        int next = JukeboxPlaylistHelper.findNext(container, position);
        if (next >= 0 && this.hoveredSlot.index == next) {
            cir.getReturnValue().add(JukeboxPlaylistHelper.PLAYING_NEXT);
        }
    }
}
