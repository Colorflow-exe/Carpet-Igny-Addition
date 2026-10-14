package com.liuyue.igny.mixins.rule.jukeboxPlaylist;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlock.class)
public abstract class JukeboxBlockMixin extends BaseEntityBlock {
    protected JukeboxBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getTicker", at = @At(value = "HEAD"), cancellable = true)
    private <T extends BlockEntity> void igny$alwaysTickForPlaylist(Level level, BlockState state, BlockEntityType<T> type, CallbackInfoReturnable<BlockEntityTicker<T>> cir) {
        cir.setReturnValue(createTickerHelper(type, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick)); //#replace >= 26.2 ? cir.setReturnValue(createTickerHelper(type, net.minecraft.world.level.block.entity.BlockEntityTypes.JUKEBOX, JukeboxBlockEntity::tick));
    }
}
