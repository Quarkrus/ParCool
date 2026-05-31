package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
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
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			parcool$resetModel();
			if (transform == null) return;
			if (transform.isOverwriting()) {
				transform.transformation().transforms().get(AnimatableModelPart.HEAD).apply(head);
				transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM).apply(rightArm);
				transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG).apply(rightLeg);
				transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM).apply(leftArm);
				transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG).apply(leftLeg);
				parCool$copyWearTransformation();
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
			parCool$copyWearTransformation();
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
		parcool$resetPart(this.head);
		parcool$resetPart(this.hat);
		parcool$resetPart(this.jacket);
		parcool$resetPart(this.body);
		{
			parcool$resetPart(this.rightArm);
			this.rightArm.x = -5.0F;
			this.rightArm.y = 2.0F;
			this.rightArm.z = 0.0F;
			this.rightSleeve.copyFrom(this.rightArm);
		}
		{
			parcool$resetPart(this.leftArm);
			this.leftArm.x = 5.0F;
			this.leftArm.y = 2.0F;
			this.leftArm.z = 0.0F;
			this.leftSleeve.copyFrom(this.leftArm);
		}
		{
			parcool$resetPart(this.leftLeg);
			this.leftLeg.x = 1.9F;
			this.leftLeg.y = 12.0F;
			this.leftLeg.z = 0.0F;

			this.leftPants.copyFrom(this.leftLeg);
		}
		{
			parcool$resetPart(this.rightLeg);
			this.rightLeg.x = -1.9F;
			this.rightLeg.y = 12.0F;
			this.rightLeg.z = 0.0F;

			this.rightPants.copyFrom(this.rightLeg);
		}
	}

	@Unique
	public void parcool$resetPart(ModelPart model) {
		model.xRot = 0;
		model.yRot = 0;
		model.zRot = 0;
		model.x = 0;
		model.y = 0;
		model.z = 0;
	}
}
