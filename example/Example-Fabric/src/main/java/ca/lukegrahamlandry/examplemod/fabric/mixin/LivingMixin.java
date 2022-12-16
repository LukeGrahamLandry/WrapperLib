/*
 * This file is part of WrapperLib
 * Copyright 2022 LukeGrahamLandry
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ca.lukegrahamlandry.examplemod.fabric.mixin;

import ca.lukegrahamlandry.examplemod.ExampleEventHandlers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingMixin {
    @Shadow
    protected boolean dead;

    @Inject(at = @At("HEAD"), method = "die")
    private void fireDieEvent(DamageSource damageSource, CallbackInfo ci){
        if (!((LivingEntity)(Object)this).isAlive()) ExampleEventHandlers.onDeath((LivingEntity)(Object)this, damageSource);
    }

    @Inject(at = @At("HEAD"), method = "jumpFromGround")
    private void fireJumpEvent(CallbackInfo ci){
         ExampleEventHandlers.onJump((LivingEntity) (Object)this);
    }
}
