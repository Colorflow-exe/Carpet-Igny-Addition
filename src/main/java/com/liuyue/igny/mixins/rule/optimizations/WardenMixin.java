package com.liuyue.igny.mixins.rule.optimizations;

import com.liuyue.igny.accessors.optimizations.IWarden;
//#if MC>=12104
//$$ import net.minecraft.server.level.ServerLevel;
//#endif
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Warden.class)
public abstract class WardenMixin extends Mob implements IWarden {

    protected WardenMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    public int crammingCount = 0;

    @Override
    public int carpet_Igny_Addition$getWardenCrammingCount(){return crammingCount;}

    @Inject(method = "customServerAiStep",at=@At(value = "HEAD"))
    private void onTick(
            //#if MC>=12104
            //$$ ServerLevel level,
            //#endif
            CallbackInfo ci
    ){
        //#if MC<12104
        var level = this.level();
        //#endif
        if ((this.tickCount + this.getId() % 801) % 400 == 0){
            crammingCount = level.getEntities(EntityType.WARDEN,new AABB(this.position().add(0.5,0.5,0.5),this.position().add(-0.5,-0.5,-0.5)),warden -> true).size();
        }
    }
}
