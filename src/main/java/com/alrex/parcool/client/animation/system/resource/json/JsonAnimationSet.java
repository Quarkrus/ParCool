package com.alrex.parcool.client.animation.system.resource.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class JsonAnimationSet {
    private ResourceLocation name;
    @SerializedName("fade_in_duration")
    private int fadeInDuration;
    @Nullable
    private ResourceLocation intro;
    private ResourceLocation main;
    @Nullable
    private ResourceLocation outro;

    public ResourceLocation getName() {
        return name;
    }

    public int getFadeInDuration() {
        return fadeInDuration;
    }

    @Nullable
    public ResourceLocation getIntro() {
        return intro;
    }

    public ResourceLocation getMain() {
        return main;
    }

    @Nullable
    public ResourceLocation getOutro() {
        return outro;
    }
}
