package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.jukeboxPlaylist.JukeboxPlaylistHelper;
import com.liuyue.igny.utils.interfaces.jukeboxPlaylist.JukeboxPlaylistAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements JukeboxPlaylistAccess {
    @Unique
    private int igny$playlistPosition = -1;

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "tick", at = @At(value = "RETURN")) //#replace < 1.21.1 ? @Inject(method = "playRecordTick", at = @At(value = "RETURN"))
    private static void igny$jukeboxPlaylistTick(Level level, BlockPos pos, BlockState state, JukeboxBlockEntity jukebox, CallbackInfo ci) {
        if (level.isClientSide() || !IGNYSettings.JUKEBOX_PLAYLIST.value()) {
            return;
        }
        if (!jukebox.getSongPlayer().isPlaying()) { //#replace < 1.21.1 ? if (!jukebox.isRecordPlaying()) {
            igny$advancePlaylist(level, pos, jukebox);
        }
    }

    //#if >= 1.21.8
    /*$$@Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void igny$savePlaylistPosition(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo ci) {
        if (this.igny$playlistPosition >= 0) {
            output.putInt("playlist_position", this.igny$playlistPosition);
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN"))
    private void igny$loadPlaylistPosition(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) {
        input.getInt("playlist_position").ifPresent(value -> this.igny$playlistPosition = value);
    }$$*/
    //#elseif >= 1.21.5
    /*$$@Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void igny$savePlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.igny$playlistPosition >= 0) {
            tag.putInt("playlist_position", this.igny$playlistPosition);
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN"))
    private void igny$loadPlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {
        tag.getInt("playlist_position").ifPresent(value -> this.igny$playlistPosition = value);
    }$$*/
    //#else
    @Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void igny$savePlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) { //#replace < 1.20.6 ? private void igny$savePlaylistPosition(CompoundTag tag, CallbackInfo ci) {
        if (this.igny$playlistPosition >= 0) {
            tag.putInt("playlist_position", this.igny$playlistPosition);
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "RETURN")) //#replace < 1.20.6 ? @Inject(method = "load", at = @At(value = "RETURN"))
    private void igny$loadPlaylistPosition(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) { //#replace < 1.20.6 ? private void igny$loadPlaylistPosition(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("playlist_position")) {
            this.igny$playlistPosition = tag.getInt("playlist_position");
        }
    }
    //#endif

    @Override
    public int igny$getPlaylistPosition() {
        return this.igny$playlistPosition;
    }

    @Override
    public void igny$setPlaylistPosition(int position) {
        this.igny$playlistPosition = position;
    }

    //#if >= 1.20.6
    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("playlist_position", this.igny$playlistPosition);
        return tag;
    }
    //#else
    /*$$@Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("playlist_position", this.igny$playlistPosition);
        return tag;
    }$$*/
    //#endif

    @Unique
    private static void igny$advancePlaylist(Level level, BlockPos pos, JukeboxBlockEntity jukebox) {
        Container container = JukeboxPlaylistHelper.containerAbove(level, pos);
        if (container == null) {
            return;
        }
        JukeboxBlockEntityMixin self = (JukeboxBlockEntityMixin) (Object) jukebox;
        int slot = JukeboxPlaylistHelper.findNext(container, self.igny$playlistPosition);
        if (slot < 0) {
            return;
        }
        ItemStack current = jukebox.getTheItem(); //#replace < 1.20.4 ? ItemStack current = jukebox.getItem(0);
        ItemStack taken = container.getItem(slot).copy();
        container.setItem(slot, current.copy());
        jukebox.setTheItem(taken); //#replace < 1.20.4 ? jukebox.setItem(0, taken);
        self.igny$setPlaylistPosition(slot);
        BlockState blockState = jukebox.getBlockState();
        level.sendBlockUpdated(pos, blockState, blockState, Block.UPDATE_CLIENTS);
    }
}
