package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public record ZiplineInfo(ZiplineType type, int color, byte autoAcceleration) {

    public Tag save() {
        var tag = new CompoundTag();
        tag.putInt("color", color);
        tag.putByte("type", (byte) type().ordinal());
        tag.putByte("power", autoAcceleration);
        return tag;
    }

    public static ZiplineInfo load(@Nullable Tag tag) {
        if (tag instanceof CompoundTag cTag) {
            int color = cTag.contains("color") ? cTag.getInt("color") : ZiplineRopeItem.DEFAULT_COLOR;
            byte autoAcceleration = cTag.contains("power") ? cTag.getByte("power") : 0;
            ZiplineType type = cTag.contains("type") ?
                    ZiplineType.values()[cTag.getByte("type") % ZiplineType.values().length] :
                    ZiplineType.LOOSE;
            return new ZiplineInfo(type, color, autoAcceleration);
        }
        return new ZiplineInfo(ZiplineType.LOOSE, ZiplineRopeItem.DEFAULT_COLOR, (byte) 0);
    }
}
