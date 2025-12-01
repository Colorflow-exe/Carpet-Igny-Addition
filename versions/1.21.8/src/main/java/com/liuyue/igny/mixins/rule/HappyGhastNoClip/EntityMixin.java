package com.liuyue.igny.mixins.rule.HappyGhastNoClip;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin{

   @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    private void musicSoulNoClipBoundingBox(CallbackInfoReturnable<AABB> cir) {
       Entity self = (Entity)(Object)this;
       if(self instanceof HappyGhast&&self.isVehicle()&&IGNYSettings.HappyGhastNoClip){
           cir.setReturnValue(new AABB(0, 0, 0, 0, 0, 0));
       }

   }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    private void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (self instanceof HappyGhast && self.isVehicle()&&IGNYSettings.HappyGhastNoClip||(self instanceof Player && self.getRootVehicle() instanceof HappyGhast&&IGNYSettings.HappyGhastNoClip)) {
            cir.setReturnValue(false);
        }
        if (self instanceof HappyGhast&& IGNYSettings.HappyGhastNoClip) {
            if(((HappyGhastInvoker)self).invokeScanPlayerAboveGhast()){
                cir.setReturnValue(false);
            }
        }

    }

}