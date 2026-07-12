package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.blockentity.ZiplineHookRenderer;
import com.alrex.parcool.common.block.TileEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Renderers {
    public static void register() {
        BlockEntityRenderers.register(TileEntities.ZIPLINE_HOOK.get(), ZiplineHookRenderer::new);
    }
}
