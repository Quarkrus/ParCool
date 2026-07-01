package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements IPlayerAnimatorHolder {

    @Shadow
    public int sprintTime;

    @Shadow
    public abstract void setSprinting(boolean p_108751_);

    @Unique
    private final PlayerAnimator parcool$animator = new PlayerAnimator();

    @Override
    public PlayerAnimator getParCoolPlayerAnimator() {
        return parcool$animator;
    }

	public LocalPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
		super(p_234112_, p_234113_, p_234114_);
	}

	@Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
	public void onIsShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
		Parkourability parkourability = Parkourability.get((Player) (Object) this);

		if (parkourability == null) return;
        if (parkourability.getBehaviorEnforcer().cancelSneak()) {
			cir.setReturnValue(false);
		}
	}

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MoverType moverType, Vec3 movement, CallbackInfo ci) {
        var player = (LocalPlayer) (Object) this;
        var parkourability = Parkourability.get(player);
        if (moverType != MoverType.SELF) return;

        var enforcedMovePos = parkourability.getBehaviorEnforcer().getEnforcedMovePoint();
        if (enforcedMovePos != null) {
            ci.cancel();
            var dMove = enforcedMovePos.subtract(player.position());
            player.setDeltaMovement(dMove);
            super.move(moverType, dMove);
        }
        var enforcedMovement = parkourability.getBehaviorEnforcer().getEnforcedDeltaMovement();
        if (enforcedMovement != null) {
            ci.cancel();
            player.setDeltaMovement(enforcedMovement);
            super.move(moverType, enforcedMovement);
        }
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        Parkourability parkourability = Parkourability.get((LocalPlayer) (Object) this);
        if (parkourability.getBehaviorEnforcer().cancelSprint()) {
            super.setSprinting(false);
            sprintTime = 0;
            ci.cancel();
        } else if (parkourability.getBehaviorEnforcer().enforceSprint()) {
            super.setSprinting(true);
            sprintTime = 0;
            ci.cancel();
        }
	}

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void onAiStep(CallbackInfo ci) {
        setSprinting(isSprinting());
    }
}
