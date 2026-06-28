package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.data.CodedAnimationComponent;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class CodedAnimationComponents extends AnimationRegistry<CodedAnimationComponent, CodedAnimationComponents.RegistrationEntry> {
    public record RegistrationEntry(ResourceLocation name, CodedAnimationComponent component) {
    }

    @Nullable
    private static CodedAnimationComponents INSTANCE = null;

    public static CodedAnimationComponents getInstance() {
        if (INSTANCE == null) INSTANCE = new CodedAnimationComponents();
        return INSTANCE;
    }

    @Nullable
    public CodedAnimationComponent get(ResourceLocation name) {
        var id = getID(name);
        if (id == null) return null;
        return getRegistry().get(id).component;
    }

    public ID<CodedAnimationComponent> register(String subName, CodedAnimationComponent component) {
        var name = new ResourceLocation(ParCool.MOD_ID, subName);
        return register(name, component);
    }

    public ID<CodedAnimationComponent> register(ResourceLocation name, CodedAnimationComponent component) {
        return register(name, new RegistrationEntry(name, component));
    }

    public final ID<CodedAnimationComponent> LOCK_HEAD_ROTATION = register("builtin/lock_head_front", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.HEAD) return null;
        var q = Vector3f.YP.rotationDegrees(
                Mth.wrapDegrees(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot) - Mth.lerp(partial, player.yHeadRotO, player.yHeadRot))
        );
        q.mul(Vector3f.XP.rotation((float) Math.toRadians(Mth.wrapDegrees(-Mth.lerp(partial, player.xRotO, player.getXRot())))));
        return new Transform(Vec3f.ZERO, q);
    });
    public final ID<CodedAnimationComponent> LOCK_BODY_ROTATION = register("builtin/lock_body", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.BODY) return null;
        var q = Vector3f.YP.rotationDegrees(
                Mth.wrapDegrees(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot) - Mth.lerp(partial, player.yHeadRotO, player.yHeadRot))
        );
        return new Transform(Vec3f.ZERO, q);
    });
}
