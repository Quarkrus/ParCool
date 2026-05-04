package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

public class BlendingFactors {
    public interface BlendingFactorFactory {
        IBlendingFactor newInstance(Map<String, String> stringArguments, Map<String, Float> numberArguments);
    }

    private record RegistrationEntry(ResourceLocation name, BlendingFactorFactory factorFactory) {
    }

    private static final IDProvider<IBlendingFactor> ID_PROVIDER = new IDProvider<>();
    private static final TreeMap<ID<IBlendingFactor>, RegistrationEntry> REGISTRY = new TreeMap<>();
    private static final TreeMap<ResourceLocation, ID<IBlendingFactor>> NAME_TO_ID = new TreeMap<>();

    private static ID<IBlendingFactor> register(String subName, BlendingFactorFactory factor) {
        var name = new ResourceLocation(ParCool.MOD_ID, subName);
        var id = ID_PROVIDER.newID();
        REGISTRY.put(id, new RegistrationEntry(name, factor));
        NAME_TO_ID.put(name, id);
        return id;
    }

    public static ID<IBlendingFactor> register(ResourceLocation name, BlendingFactorFactory factor) {
        var id = ID_PROVIDER.newID();
        REGISTRY.put(id, new RegistrationEntry(name, factor));
        NAME_TO_ID.put(name, id);
        return id;
    }

    @Nullable
    public static ID<IBlendingFactor> getID(ResourceLocation name) {
        return NAME_TO_ID.get(name);
    }

    @Nullable
    public static IBlendingFactor get(ID<IBlendingFactor> id, Map<String, String> sArgs, Map<String, Float> fArgs) {
        var entry = REGISTRY.get(id);
        if (entry == null) return null;
        return entry.factorFactory.newInstance(sArgs, fArgs);
    }

    @Nullable
    public static IBlendingFactor get(ResourceLocation name, Map<String, String> sArgs, Map<String, Float> fArgs) {
        var id = NAME_TO_ID.get(name);
        if (id == null) return null;
        return get(id, sArgs, fArgs);
    }

    public static final ID<IBlendingFactor> VELOCITY = register(
            "velocity",
            (stringArgs, floatArgs) -> {
                var min = Mth.clamp(floatArgs.getOrDefault("min", 0f), 0f, 10f);
                var max = Mth.clamp(floatArgs.getOrDefault("max", 0.25f), min, 100f);
                return player -> Mth.clamp(EasingFunctions.CUBE.easeInOut((float) ((player.getDeltaMovement().length() - min) / (max - min))), 0f, 1f);
            }
    );
    public static final ID<IBlendingFactor> VELOCITY_VERTICAL = register(
            "velocity_v",
            (stringArgs, floatArgs) -> {
                var min = Mth.clamp(floatArgs.getOrDefault("min", 0f), 0f, 10f);
                var max = Mth.clamp(floatArgs.getOrDefault("max", 0.25f), min, 100f);
                return player -> Mth.clamp(EasingFunctions.CUBE.easeInOut((float) ((player.getDeltaMovement().y() - min) / (max - min))), 0f, 1f);
            }
    );
    public static final ID<IBlendingFactor> VELOCITY_HORIZONTAL = register(
            "velocity_h",
            (stringArgs, floatArgs) -> {
                var min = Mth.clamp(floatArgs.getOrDefault("min", 0f), 0f, 10f);
                var max = Mth.clamp(floatArgs.getOrDefault("max", 0.25f), min, 100f);
                return player -> {
                    var vel = player.getDeltaMovement();
                    return Mth.clamp(EasingFunctions.CUBE.easeInOut((float) (((new Vec3(vel.x, 0., vel.z)).length() - min) / (max - min))), 0f, 1f);
                };
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_RIGHT = register(
            "rotation_r",
            (stringArgs, floatArgs) -> {
                return player -> 0f;
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_LEFT = register(
            "rotation_l",
            (stringArgs, floatArgs) -> {
                return player -> 0f;
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_UP = register(
            "rotation_u",
            (stringArgs, floatArgs) -> {
                return player -> 0f;
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_DOWN = register(
            "rotation_d",
            (stringArgs, floatArgs) -> {
                return player -> 0f;
            }
    );
}
