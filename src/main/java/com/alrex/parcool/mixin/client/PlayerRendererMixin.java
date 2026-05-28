package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
		super(p_174289_, p_174290_, p_174291_);
	}

	@Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupRotationsHead(AbstractClientPlayer player, PoseStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		if (player instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;

			var bodyTransformation = transform.transformation().transforms().get(AnimatableModelPart.BODY);
			if (bodyTransformation == null) return;

			bodyTransformation = Transform.NO_TRANSFORMATION.morph(bodyTransformation, transform.blendFactor());
			var translation = bodyTransformation.translation();
			stack.translate(translation.x(), translation.y(), translation.z());
			stack.translate(0, 0.9, 0);
			stack.mulPose(bodyTransformation.rotation());
			stack.translate(0, -0.9, 0);
			ci.cancel();
		}
	}

	/*
	@Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("RETURN"))
	protected void onSetupRotationsTail(AbstractClientPlayer player, PoseStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
	}
	 */
}
