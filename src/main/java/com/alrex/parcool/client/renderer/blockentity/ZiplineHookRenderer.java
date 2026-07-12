package com.alrex.parcool.client.renderer.blockentity;

import com.alrex.parcool.client.renderer.RenderTypes;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineShape;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class ZiplineHookRenderer implements BlockEntityRenderer<ZiplineHookTileEntity> {

    public ZiplineHookRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@Nonnull ZiplineHookTileEntity entity, float partial, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int i, int i1) {
        var iterator = entity.getRenderAbleZiplines();
        var level = entity.getLevel();
        if (level == null) return;
        while (iterator.hasNext()) {
            renderRope(entity, iterator.next(), level, partial, poseStack, multiBufferSource);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ZiplineHookTileEntity entity) {
        return true;
    }

    @Override
    public boolean shouldRender(@Nonnull ZiplineHookTileEntity entity, @Nonnull Vec3 viewPoint) {
        return !entity.getConnectionPoints().isEmpty();
    }

    private void renderRope(
            ZiplineHookTileEntity entity,
            Zipline zipline,
            Level level,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource
    ) {
        boolean powered = zipline.info().autoAcceleration() > 0;
        int color = zipline.info().color();
        float r = ((0xFF0000 & color) >> 16) / 255f;
        float g = ((0x00FF00 & color) >> 8) / 255f;
        float b = (0x0000FF & color) / 255f;

        Vec3 hookPoint = entity.getHookPoint();
        BlockPos hookBlockPos = entity.getBlockPos();
        Vec3 endOffsetFromStart = zipline.shape().getOffsetFromStartToEnd();

        boolean render3d = ParCoolConfig.Client.ENABLE_3D_RENDERING_FOR_ZIPLINE.get();

        poseStack.pushPose();
        {
            poseStack.translate(
                    hookPoint.x() - hookBlockPos.getX(),
                    hookPoint.y() - hookBlockPos.getY(),
                    hookPoint.z() - hookBlockPos.getZ()
            );
            var vertexConsumer = render3d ?
                    multiBufferSource.getBuffer(RenderTypes.ZIPLINE_3D) :
                    multiBufferSource.getBuffer(RenderTypes.ZIPLINE_2D);
            Matrix4f transformMatrix = poseStack.last().pose();

            int startBlockLightLevel = level.getBrightness(LightLayer.BLOCK, zipline.start());
            int endBlockLightLevel = level.getBrightness(LightLayer.BLOCK, zipline.end());
            int startSkyBrightness = level.getBrightness(LightLayer.SKY, zipline.start());
            int endSkyBrightness = level.getBrightness(LightLayer.SKY, zipline.end());

            int divisionCount = Math.min((int) Math.ceil(endOffsetFromStart.length() / 0.6), 24);
            float invLengthSqrtXZ = (float) Mth.fastInvSqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z());
            float unitLengthX = (float) (endOffsetFromStart.x() * invLengthSqrtXZ);
            float unitLengthZ = (float) (endOffsetFromStart.z() * invLengthSqrtXZ);

            for (int i = 0; i < divisionCount; i++) {
                float colorScale = i % 2 == 0 ? 1f : (powered ? 0.9f : 0.75f);

                for (int j = 0; j < 2; j++) {
                    if (render3d) {
                        renderRopeSingleBlock3D(
                                transformMatrix, vertexConsumer,
                                zipline.shape(),
                                i, divisionCount,
                                unitLengthX, unitLengthZ,
                                startBlockLightLevel, endBlockLightLevel,
                                startSkyBrightness, endSkyBrightness,
                                r * colorScale, g * colorScale, b * colorScale//,
                                //j % 2 == 0
                        );
                    } else {
                        renderRopeSingleBlock2D(
                                transformMatrix, vertexConsumer,
                                zipline.shape(),
                                i, divisionCount,
                                unitLengthX, unitLengthZ,
                                startBlockLightLevel, endBlockLightLevel,
                                startSkyBrightness, endSkyBrightness,
                                r * colorScale, g * colorScale, b * colorScale,
                                j % 2 == 0
                        );
                    }
                }
            }
        }
        poseStack.popPose();
    }

    private void renderRopeSingleBlock2D(
            Matrix4f transformMatrix,
            VertexConsumer vertexConsumer,
            ZiplineShape zipline,
            int currentCount, int maxCount,
            float unitLengthX,
            float unitLengthZ,
            int startBlockLightLevel, int endBlockLightLevel,
            int startSkyBrightness, int endSkyBrightness,
            float r, float g, float b,
            boolean tiltType
    ) {
        for (int i = 0; i < 2; i++) {
            float phase = (float) (currentCount + i) / maxCount;

            int lightLevel = LightTexture.pack((int) Mth.lerp(phase, startBlockLightLevel, endBlockLightLevel), (int) Mth.lerp(phase, startSkyBrightness, endSkyBrightness));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float) midPointD.x(), (float) midPointD.y(), (float) midPointD.z());

            final float width = 0.075f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.fastInvSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv / 1.41421356f;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv / 1.41421356f;
            float sign = tiltType ? 1 : -1;
            float xOffset = sign * unitLengthZ * width / 1.41421356f;
            float zOffset = sign * -unitLengthX * width / 1.41421356f;

            if (i == 0) {
                vertexConsumer
                        .vertex(transformMatrix,
                                (midPoint.x() + xBaseOffset + xOffset),
                                (midPoint.y() - yOffset),
                                (midPoint.z() + zBaseOffset + zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexConsumer
                        .vertex(transformMatrix,
                                (midPoint.x() - xBaseOffset - xOffset),
                                (midPoint.y() + yOffset),
                                (midPoint.z() - zBaseOffset - zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            } else {
                vertexConsumer
                        .vertex(transformMatrix,
                                (midPoint.x() - xBaseOffset - xOffset),
                                (midPoint.y() + yOffset),
                                (midPoint.z() - zBaseOffset - zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexConsumer
                        .vertex(transformMatrix,
                                (midPoint.x() + xBaseOffset + xOffset),
                                (midPoint.y() - yOffset),
                                (midPoint.z() + zBaseOffset + zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            }
        }
    }

    private void renderRopeSingleBlock3D(
            Matrix4f transformMatrix,
            VertexConsumer vertexConsumer,
            ZiplineShape zipline,
            int currentCount, int maxCount,
            float unitLengthX,
            float unitLengthZ,
            int startBlockLightLevel, int endBlockLightLevel,
            int startSkyBrightness, int endSkyBrightness,
            float r, float g, float b
    ) {
        Vector3f[] vertexList = new Vector3f[8];
        int[] lightLevelList = new int[2];
        for (int i = 0; i < 2; i++) {
            float phase = (float) (currentCount + i) / maxCount;

            lightLevelList[i] = LightTexture.pack((int) Mth.lerp(phase, startBlockLightLevel, endBlockLightLevel), (int) Mth.lerp(phase, startSkyBrightness, endSkyBrightness));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float) midPointD.x(), (float) midPointD.y(), (float) midPointD.z());

            final float width = 0.075f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.fastInvSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv / 1.41421356f;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv / 1.41421356f;
            float xOffset = unitLengthZ * width / 1.41421356f;
            float zOffset = -unitLengthX * width / 1.41421356f;
            vertexList[4 * i] = new Vector3f(
                    (midPoint.x() - xBaseOffset + xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset + zOffset)
            );
            vertexList[4 * i + 1] = new Vector3f(
                    (midPoint.x() - xBaseOffset - xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset - zOffset)
            );
            vertexList[4 * i + 2] = new Vector3f(
                    (midPoint.x() + xBaseOffset - xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset - zOffset)
            );
            vertexList[4 * i + 3] = new Vector3f(
                    (midPoint.x() + xBaseOffset + xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset + zOffset)
            );
        }
        for (int i = 0; i < 4; i++) {
            vertexConsumer.vertex(transformMatrix, vertexList[i].x(), vertexList[i].y(), vertexList[i].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[(i + 1) % 4].x(), vertexList[(i + 1) % 4].y(), vertexList[(i + 1) % 4].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[4 + (i + 1) % 4].x(), vertexList[4 + (i + 1) % 4].y(), vertexList[4 + (i + 1) % 4].z()).color(r, g, b, 1f).uv2(lightLevelList[1]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[4 + i].x(), vertexList[4 + i].y(), vertexList[4 + i].z()).color(r, g, b, 1f).uv2(lightLevelList[1]).endVertex();
        }
        if (currentCount == 0) {
            vertexConsumer.vertex(transformMatrix, vertexList[3].x(), vertexList[3].y(), vertexList[3].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[2].x(), vertexList[2].y(), vertexList[2].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[1].x(), vertexList[1].y(), vertexList[1].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[0].x(), vertexList[0].y(), vertexList[0].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
        } else if (currentCount == maxCount - 1) {
            vertexConsumer.vertex(transformMatrix, vertexList[4].x(), vertexList[4].y(), vertexList[4].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[5].x(), vertexList[5].y(), vertexList[5].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[6].x(), vertexList[6].y(), vertexList[6].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[7].x(), vertexList[7].y(), vertexList[7].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
        }
    }
}
