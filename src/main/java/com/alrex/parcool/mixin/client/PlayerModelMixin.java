package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
	@Shadow
	@Final
	public ModelPart leftPants;

	@Shadow
	@Final
	public ModelPart rightPants;

	@Shadow
	@Final
	public ModelPart leftSleeve;

	@Shadow
	@Final
	public ModelPart rightSleeve;

	@Shadow
	@Final
	public ModelPart jacket;

	@Shadow
	@Final
	private List<ModelPart> parts;

	public PlayerModelMixin(ModelPart p_i1148_1_) {
		super(p_i1148_1_);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (entity.isFallFlying()) return;
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			parcool$resetModel();
			if (transform == null) return;
			if (transform.isOverwriting()) {
				var headTransform = transform.transformation().transforms().get(AnimatableModelPart.HEAD);
				if (headTransform != null) headTransform.apply(head);
				var rATransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM);
				if (rATransform != null) rATransform.apply(rightArm);
				var rLTransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG);
				if (rLTransform != null) rLTransform.apply(rightLeg);
				var lATransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM);
				if (lATransform != null) lATransform.apply(leftArm);
				var lLTransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG);
				if (lLTransform != null) lLTransform.apply(leftLeg);
				info.cancel();
			}
		}
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (entity.isFallFlying()) return;
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;
			var blendingFactor = transform.blendFactor();
			var headTransform = transform.transformation().transforms().get(AnimatableModelPart.HEAD);
			if (headTransform != null) headTransform.apply(head, blendingFactor);
			var rATransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM);
			if (rATransform != null) rATransform.apply(rightArm, blendingFactor);
			var rLTransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG);
			if (rLTransform != null) rLTransform.apply(rightLeg, blendingFactor);
			var lATransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM);
			if (lATransform != null) lATransform.apply(leftArm, blendingFactor);
			var lLTransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG);
			if (lLTransform != null) lLTransform.apply(leftLeg, blendingFactor);
			Transform.NO_TRANSFORMATION.apply(body, blendingFactor);
		}
	}

	@Unique
	private void parCool$copyWearTransformation() {
		leftPants.copyFrom(this.leftLeg);
		rightPants.copyFrom(this.rightLeg);
		leftSleeve.copyFrom(this.leftArm);
		rightSleeve.copyFrom(this.rightArm);
		jacket.copyFrom(this.body);
	}

	@Unique
	public void parcool$resetModel() {
		for (var part : this.bodyParts()) {
			part.resetPose();
		}
		for (var part : this.headParts()) {
			part.resetPose();
		}
	}
}
