package com.alrex.parcool.client.animation.system.resource.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class JsonAnimationComponentGroup {
    private int duration;
    private boolean loop;
    public List<AnimationComponent> components;

    public int getDuration() {
        return duration;
    }

    public boolean isLoop() {
        return loop;
    }

    public List<AnimationComponent> getComponents() {
        return components;
    }

    public static class AnimationComponent {
        private ResourceLocation name;
        @Nullable
        private BlendingFactor blend;
        @Nullable
        private ResourceLocation progress;

        public ResourceLocation getName() {
            return name;
        }

        @Nullable
        public BlendingFactor getBlend() {
            return blend;
        }

        @Nullable
        public ResourceLocation getProgress() {
            return progress;
        }

        public static class BlendingFactor {
            private ResourceLocation name;
            @Nullable
            @SerializedName("s_args")
            private Map<String, String> sArgs;
            @Nullable
            @SerializedName("f_args")
            private Map<String, Float> fArgs;

            public ResourceLocation getName() {
                return name;
            }

            @Nullable
            public Map<String, Float> getfArgs() {
                return fArgs;
            }

            @Nullable
            public Map<String, String> getsArgs() {
                return sArgs;
            }
        }
    }
}
