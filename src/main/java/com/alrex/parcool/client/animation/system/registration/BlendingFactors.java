package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.SimpleBlendFactor;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.TreeMap;

public class BlendingFactors {
    public interface BlendingFactorFactory {
        IBlendingFactor newInstance(Argument args);
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
    public static IBlendingFactor newInstance(ID<IBlendingFactor> id, Argument argument) {
        var entry = REGISTRY.get(id);
        if (entry == null) return null;
        return entry.factorFactory.newInstance(argument);
    }

    @Nullable
    public static IBlendingFactor newInstance(ResourceLocation name, Argument argument) {
        var id = NAME_TO_ID.get(name);
        if (id == null) return null;
        return newInstance(id, argument);
    }

    public static final ID<IBlendingFactor> TIME = register(
            "time",
            (args) -> {
                var max = Mth.clamp(args.request("max", 20f), 0, 100f);
                return new IBlendingFactor() {
                    private int tick;

                    @Override
                    public float getFactor(AbstractClientPlayer player, float partial) {
                        return EasingFunctions.QUAD.easeInOut(Mth.clamp((tick + partial) / max, 0f, 1f));
                    }

                    @Override
                    public void tick() {
                        tick++;
                    }
                };
            }
    );
    public static final ID<IBlendingFactor> VELOCITY = register(
            "velocity",
            (args) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player, partial) -> Mth.clamp(EasingFunctions.QUAD.easeInOut((float) ((player.getDeltaMovement().length() - min) / (max - min))), 0f, 1f));
            }
    );
    public static final ID<IBlendingFactor> VELOCITY_VERTICAL = register(
            "velocity_v",
            (args) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player, partial) -> Mth.clamp(EasingFunctions.QUAD.easeInOut((float) ((player.getDeltaMovement().y() - min) / (max - min))), 0f, 1f));
            }
    );
    public static final ID<IBlendingFactor> VELOCITY_HORIZONTAL = register(
            "velocity_h",
            (args) -> {
                var min = Mth.clamp(args.request("min", 0f), 0f, 10f);
                var max = Mth.clamp(args.request("max", 0.25f), min, 100f);
                return new SimpleBlendFactor((player, partial) -> {
                    var vel = player.getDeltaMovement();
                    return Mth.clamp(EasingFunctions.QUAD.easeInOut((float) (((new Vec3(vel.x, 0., vel.z)).length() - min) / (max - min))), 0f, 1f);
                });
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_RIGHT = register(
            "rotation_r",
            (args) -> {
                return new SimpleBlendFactor((player, partial) -> 0f);
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_LEFT = register(
            "rotation_l",
            (args) -> {
                return new SimpleBlendFactor((player, partial) -> 0f);
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_UP = register(
            "rotation_u",
            (args) -> {
                return new SimpleBlendFactor((player, partial) -> 0f);
            }
    );
    public static final ID<IBlendingFactor> ANGULAR_VELOCITY_DOWN = register(
            "rotation_d",
            (args) -> {
                return new SimpleBlendFactor((player, partial) -> 0f);
            }
    );
}
