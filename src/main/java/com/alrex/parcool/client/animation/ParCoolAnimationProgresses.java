package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.resources.ResourceLocation;

public class ParCoolAnimationProgresses {
    public static ID<AnimationProgress> DIVE_PROGRESS = AnimationProgresses.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "builtin/dive"),
            (player, partialTick) -> Parkourability.get(player).get(ParCoolActions.DIVE).getAnimationProgress(partialTick)
    );

    public static void register() {
    }
}
