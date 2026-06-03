package com.alrex.parcool.client.animation.system.resource;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.AnimatableProperty;
import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.data.*;
import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.client.animation.system.resource.json.*;
import com.alrex.parcool.client.animation.system.util.IResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class AnimationResourceManager extends SimplePreparableReloadListener<AnimationResource> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TypeToken<List<JsonAnimationSet>> ANIMATION_SETS_TYPE = new TypeToken<>() {
    };
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
            .registerTypeAdapter(TimedValue.class, new TimedValueAdapter())
            .registerTypeAdapter(Argument.class, new ArgumentAdapter())
            .create();
    private static AnimationResourceManager INSTANCE = null;

    public static AnimationResourceManager getInstance() {
        if (INSTANCE == null) INSTANCE = new AnimationResourceManager();
        return INSTANCE;
    }

    private AnimationResource resource = AnimationResource.empty();

    public AnimationResource getResource() {
        return resource;
    }

    @Override
    protected AnimationResource prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        var animationSetRegistrationMap = new TreeMap<ResourceLocation, JsonAnimationSet>();
        var requestedComponentGroups = new TreeSet<ResourceLocation>();
        for (var namespace : resourceManager.getNamespaces()) {
            var resourceLocation = new ResourceLocation(namespace, "mma/animations.json");
            for (var setResource : resourceManager.getResourceStack(resourceLocation)) {
                try (var reader = setResource.openAsReader()) {
                    var jsonResult = GSON.<List<JsonAnimationSet>>fromJson(reader, ANIMATION_SETS_TYPE.getType());
                    for (var registration : jsonResult) {
                        animationSetRegistrationMap.put(registration.getName(), registration);
                        if (registration.getIntro() != null) requestedComponentGroups.add(registration.getIntro());
                        if (registration.getOutro() != null) requestedComponentGroups.add(registration.getOutro());
                        requestedComponentGroups.add(registration.getMain());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JsonSyntaxException e) {
                    LOGGER.error("{} on loading AnimationSet[{}]:{}", e.getClass().getSimpleName(), resourceLocation, e.getMessage());
                }
            }
        }
        var animationComponentGroupMap = new TreeMap<ResourceLocation, JsonAnimationComponentGroup>();
        var requestedComponents = new TreeSet<ResourceLocation>();
        for (var groupLocation : requestedComponentGroups) {
            var resourceLocation = new ResourceLocation(groupLocation.getNamespace(), "mma/groups/" + groupLocation.getPath());
            var groupResource = resourceManager.getResource(resourceLocation);
            if (groupResource.isPresent()) {
                try (var reader = groupResource.get().openAsReader()) {
                    var jsonResult = GSON.fromJson(reader, JsonAnimationComponentGroup.class);
                    animationComponentGroupMap.put(groupLocation, jsonResult);
                    jsonResult.getComponents()
                            .stream()
                            .map(JsonAnimationComponentGroup.AnimationComponent::getName)
                            .forEach(requestedComponents::add);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JsonSyntaxException e) {
                    LOGGER.error("{} on loading AnimationComponentGroup[{}]:{}", e.getClass().getSimpleName(), resourceLocation, e.getMessage());
                }
            } else {
                LOGGER.warn("Requested component group [{}] does not exist in resources", groupLocation);
            }
        }
        for (var compGroup : requestedComponentGroups) {
            if (!animationComponentGroupMap.containsKey(compGroup)) {
                LOGGER.warn("Requested component group [{}] is not found in loaded resources", compGroup);
            }
        }
        var animationComponentMap = new TreeMap<ResourceLocation, JsonAnimationComponent>();
        for (var compLocation : requestedComponents) {
            var resourceLocation = new ResourceLocation(compLocation.getNamespace(), "mma/components/" + compLocation.getPath());
            var compResource = resourceManager.getResource(resourceLocation);
            if (compResource.isPresent()) {
                try (var reader = compResource.get().openAsReader()) {
                    var jsonResult = GSON.fromJson(reader, JsonAnimationComponent.class);
                    animationComponentMap.put(compLocation, jsonResult);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JsonSyntaxException e) {
                    LOGGER.error("{} on loading AnimationComponent[{}]:{}", e.getClass().getSimpleName(), resourceLocation, e.getMessage());
                }
            } else {
                LOGGER.warn("Requested component [{}] does not exist in resources", compLocation);
            }
        }
        for (var comp : requestedComponents) {
            if (!animationComponentMap.containsKey(comp)) {
                LOGGER.warn("Requested animation component [{}] is not found in loaded resources", comp);
            }
        }
        return constructResource(animationSetRegistrationMap, animationComponentGroupMap, animationComponentMap);
    }

    private AnimationResource constructResource(
            TreeMap<ResourceLocation, JsonAnimationSet> animationSets,
            TreeMap<ResourceLocation, JsonAnimationComponentGroup> componentGroups,
            TreeMap<ResourceLocation, JsonAnimationComponent> components
    ) {
        var componentInstances = constructComponents(components);
        var componentGroupInstances = constructComponentGroups(componentGroups, componentInstances);
        var animationSetInstances = constructAnimationSets(animationSets, componentGroupInstances);
        return new AnimationResource(componentInstances, componentGroupInstances, animationSetInstances);
    }

    private TreeMap<ResourceLocation, StaticAnimationComponent> constructComponents(
            TreeMap<ResourceLocation, JsonAnimationComponent> jsonComponents
    ) {
        var componentInstances = new TreeMap<ResourceLocation, StaticAnimationComponent>();
        for (var componentJson : jsonComponents.entrySet()) {
            var map = new EnumMap<AnimatableModelPart, EnumMap<AnimatableProperty, Timeline>>(AnimatableModelPart.class);
            int duration = 0;
            for (var part : AnimatableModelPart.values()) {
                var partTimelines = componentJson.getValue().get(part);
                if (partTimelines == null) continue;
                var timelineMap = new EnumMap<AnimatableProperty, Timeline>(AnimatableProperty.class);
                propertyLoop:
                for (var property : AnimatableProperty.values()) {
                    var timeline = partTimelines.get(property);
                    if (timeline == null || timeline.isEmpty()) continue;
                    var list = new ArrayList<Transition>();
                    for (int i = 0; i < timeline.size(); i++) {
                        var result = timeline.get(i).parse(i + 1 < timeline.size() ? timeline.get(i + 1) : null);
                        if (result instanceof IResult.Error<Transition, String> error) {
                            LOGGER.warn("Failed to parse [{}:{}:{}:{}] : {}", componentJson.getKey(), part, property, i, error.error());
                            continue propertyLoop;
                        }
                        if (result instanceof IResult.Success<Transition, String> success) {
                            list.add(success.result());
                        }
                    }
                    var end = list.get(list.size() - 1).getStart();
                    if (duration < end.time()) duration = Mth.ceil(end.time());
                    list.sort((t1, t2) -> Float.compare(t1.getStart().value(), t2.getStart().value()));
                    timelineMap.put(property, new Timeline(list));
                }
                map.put(part, timelineMap);
            }
            componentInstances.put(componentJson.getKey(), new StaticAnimationComponent(map, duration));
        }
        return componentInstances;
    }

    private TreeMap<ResourceLocation, AnimationComponentGroup> constructComponentGroups(
            TreeMap<ResourceLocation, JsonAnimationComponentGroup> jsonComponentGroups,
            TreeMap<ResourceLocation, StaticAnimationComponent> components
    ) {
        var instances = new TreeMap<ResourceLocation, AnimationComponentGroup>();
        for (var compGroupEntry : jsonComponentGroups.entrySet()) {
            var componentList = new ArrayList<AnimationComponentGroup.ComponentEntry>(compGroupEntry.getValue().getComponents().size());
            for (var compEntry : compGroupEntry.getValue().getComponents()) {
                var comp = components.get(compEntry.getName());
                if (comp == null) {
                    LOGGER.warn("Component[{}] requested by Group[{}], is not loaded", compEntry.getName(), compGroupEntry.getKey());
                    continue;
                }
                var blend = compEntry.getBlend();
                var progress = compEntry.getProgress();
                ID<AnimationProgress> animationProgressID = null;
                if (progress != null) {
                    animationProgressID = AnimationProgresses.getID(progress.getName());
                }
                if (animationProgressID == null) {
                    animationProgressID = AnimationProgresses.TIME;
                }
                var finalAnimationProgressID = animationProgressID;
                componentList.add(new AnimationComponentGroup.ComponentEntry(
                        comp,
                        blend == null ? null : () -> BlendingFactors.newInstance(
                                blend.getName(), blend.getArgs()
                        ),
                        progress == null
                                ? () -> AnimationProgresses.getNewInstance(finalAnimationProgressID) :
                                () -> AnimationProgresses.getNewInstance(
                                        finalAnimationProgressID,
                                        progress.getArgs().request("loop", false),
                                        progress.getArgs().request("min", 0f),
                                        progress.getArgs().request("max", Float.MAX_VALUE),
                                        progress.getArgs()
                                )
                ));
            }
            instances.put(compGroupEntry.getKey(), new AnimationComponentGroup(
                    componentList, compGroupEntry.getValue().getDuration(), compGroupEntry.getValue().isLoop()
            ));
        }
        return instances;
    }

    private TreeMap<ResourceLocation, AnimationSet> constructAnimationSets(
            TreeMap<ResourceLocation, JsonAnimationSet> jsonAnimationSets,
            TreeMap<ResourceLocation, AnimationComponentGroup> componentGroups
    ) {
        var instances = new TreeMap<ResourceLocation, AnimationSet>();
        for (var animSetEntry : jsonAnimationSets.entrySet()) {
            if (animSetEntry.getValue().getMain() == null) {
                LOGGER.warn("Animation Set [{}] has no main animation", animSetEntry.getKey());
                continue;
            }
            var main = componentGroups.get(animSetEntry.getValue().getMain());
            if (main == null) {
                LOGGER.warn("Main animation {} of Animation Set [{}] is not found", animSetEntry.getValue().getMain(), animSetEntry.getKey());
                continue;
            }
            AnimationComponentGroup intro = null;
            if (animSetEntry.getValue().getIntro() != null) {
                intro = componentGroups.get(animSetEntry.getValue().getIntro());
                if (intro == null) {
                    LOGGER.warn("Animation Set [{}] has intro animation, but it's not found", animSetEntry.getKey());
                    continue;
                }
            }
            AnimationComponentGroup outro = null;
            if (animSetEntry.getValue().getOutro() != null) {
                outro = componentGroups.get(animSetEntry.getValue().getOutro());
                if (outro == null) {
                    LOGGER.warn("Animation Set [{}] has outro animation, but it's not found", animSetEntry.getKey());
                    continue;
                }
            }
            instances.put(
                    animSetEntry.getKey(),
                    new AnimationSet(
                            animSetEntry.getKey(),
                            animSetEntry.getValue().getFadeInDuration(),
                            animSetEntry.getValue().getFadeOutDuration(),
                            intro, main, outro
                    )
            );
        }
        return instances;
    }

    @Override
    protected void apply(AnimationResource animationResource, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.resource = animationResource;
    }
}
