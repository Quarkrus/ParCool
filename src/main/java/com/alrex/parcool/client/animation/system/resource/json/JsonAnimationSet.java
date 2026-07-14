package com.alrex.parcool.client.animation.system.resource.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class JsonAnimationSet {
    private ResourceLocation name;
    @SerializedName("fade_in_duration")
    private int fadeInDuration;
    @SerializedName("fade_out_duration")
    private int fadeOutDuration;
    private List<AnimationItem> animations;

    public ResourceLocation getName() {
        return name;
    }

    public int getFadeInDuration() {
        return fadeInDuration;
    }

    public int getFadeOutDuration() {
        return fadeOutDuration;
    }

    public List<AnimationItem> getAnimations() {
        return animations;
    }

    public static class AnimationItem {
        @Nullable
        private ResourceLocation intro;
        private ResourceLocation main;
        @Nullable
        private ResourceLocation outro;

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
}
