package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ZiplineRopeAddPowerRecipe extends CustomRecipe {
    public ZiplineRopeAddPowerRecipe(ResourceLocation p_i48169_1_) {
        super(p_i48169_1_);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer craftingContainer, @Nonnull Level level) {
        boolean ziplineRopeFound = false;
        boolean redstoneItemFound = false;
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.getItem().equals(Items.ZIPLINE_ROPE.get())) {
                if (ziplineRopeFound) return false;
                else ziplineRopeFound = true;
            } else if (stack.getItem().equals(net.minecraft.world.item.Items.REDSTONE)) {
                if (redstoneItemFound) return false;
                redstoneItemFound = true;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return ziplineRopeFound && redstoneItemFound;
    }

    @Override
    public net.minecraft.world.item.ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack ziplineRope = null;
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            Item item = stack.getItem();
            if (item instanceof ZiplineRopeItem) {
                ziplineRope = stack;
                break;
            }
        }
        if (ziplineRope == null) return ItemStack.EMPTY;
        ItemStack resultZiplineRope = new ItemStack(Items.ZIPLINE_ROPE::get);
        ZiplineRopeItem.setAutoAcceleration(resultZiplineRope, (byte) 1);
        return resultZiplineRope;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.ZIPLINE_ROPE_POWER.get();
    }
}
