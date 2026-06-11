package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.SimpleBlendFactor;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.resources.ResourceLocation;

public class ParCoolBlendingFactors {
    public static final ID<IBlendingFactor> CLING_CLIFF_LEFT_TO_WALL = BlendingFactors.getInstance()
            .register(new ResourceLocation(ParCool.MOD_ID, "builtin/hang_on_left_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player, partial) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorLeftToWall(partial), method)
            );
    public static final ID<IBlendingFactor> CLING_CLIFF_RIGHT_TO_WALL = BlendingFactors.getInstance()
            .register(new ResourceLocation(ParCool.MOD_ID, "builtin/hang_on_right_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player, partial) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorRightToWall(partial), method)
            );
    public static final ID<IBlendingFactor> CLING_CLIFF_BACK_TO_WALL = BlendingFactors.getInstance()
            .register(new ResourceLocation(ParCool.MOD_ID, "builtin/hang_on_back_to_wall"), (args, method) ->
                    new SimpleBlendFactor((player, partial) -> Parkourability.get(player).get(ParCoolActions.HANG_ON).getBlendFactorBackToWall(partial), method)
            );

    public static void register() {
    }
}
