package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
	public PlayerModelMixin(ModelPart p_i1148_1_) {
		super(p_i1148_1_);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;
			if (transform.isOverwriting()) {
				transform.transformation().transforms().get(AnimatableModelPart.HEAD).apply(head);
				transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM).apply(rightArm);
				transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG).apply(rightLeg);
				transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM).apply(leftArm);
				transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG).apply(leftLeg);
				info.cancel();
			}
		}
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;
			var blendingFactor = transform.blendFactor();
			transform.transformation().transforms().get(AnimatableModelPart.HEAD).apply(head, blendingFactor);
			transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM).apply(rightArm, blendingFactor);
			transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG).apply(rightLeg, blendingFactor);
			transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM).apply(leftArm, blendingFactor);
			transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG).apply(leftLeg, blendingFactor);
		}
	}
}
