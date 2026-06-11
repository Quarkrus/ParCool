package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.data.CodedAnimationComponent;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.alrex.parcool.client.animation.system.registration.CodedAnimationComponents;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;

public class ParCoolCodedAnimationComponents {
    public static final ID<CodedAnimationComponent> CLING_TO_CLIFF_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/cling_to_cliff_lock_body",
            (player, part, progress, partial) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var wallVec = Parkourability.get(player).get(ParCoolActions.CLING_TO_CLIFF).getWallVec(partial);
                if (wallVec == null) return null;
                var yaw = Mth.wrapDegrees(MathUtil.toYawRadian(wallVec) + Math.toRadians(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot)));
                return new Transform(Vec3f.ZERO, Vector3f.YP.rotation((float) yaw));
            }
    );

    public static void register() {
    }
}
