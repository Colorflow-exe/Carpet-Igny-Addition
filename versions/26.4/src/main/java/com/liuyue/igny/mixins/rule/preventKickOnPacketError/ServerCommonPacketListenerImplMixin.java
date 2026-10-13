package com.liuyue.igny.mixins.rule.preventKickOnPacketError;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
    @WrapWithCondition(method = "onPacketError", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private boolean disconnect(ServerCommonPacketListenerImpl instance, Component component) {
        return !IGNYSettings.PREVENT_KICK_ON_PACKET_ERROR.value();
    }
}
