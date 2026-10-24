package com.liuyue.igny.mixins.rule.structureBlockNoBlockUpdate;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.structureBlockNoBlockUpdate.StructureNoUpdateSession;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @WrapMethod(method = "placeInWorld")
    private boolean igny$sessionScope(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2,
                                      StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i,
                                      Operation<Boolean> original) {
        StructureNoUpdateSession.begin(serverLevelAccessor.getLevel());
        try {
            return original.call(serverLevelAccessor, blockPos, blockPos2, structurePlaceSettings, randomSource, i);
        } finally {
            StructureNoUpdateSession.end();
        }
    }

    @Inject(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;isInside(Lnet/minecraft/core/Vec3i;)Z"))
    private void setBlock(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, CallbackInfoReturnable<Boolean> cir, @Local StructureTemplate.StructureBlockInfo info) {
        if (IGNYSettings.STRUCTURE_BLOCK_NO_BLOCK_UPDATE.value()) {
            StructureNoUpdateSession.record(info.pos()); //#replace <= 1.19.4 ? StructureNoUpdateSession.record(info.pos);
        }
    }
}
