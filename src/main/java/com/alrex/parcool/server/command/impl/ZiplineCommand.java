package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTTagArgument;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class ZiplineCommand {
    private static final String ARGS_NAME_HOOK_POS_1 = "hook1";
    private static final String ARGS_NAME_HOOK_POS_2 = "hook2";
    private static final String ARGS_NAME_ZIPLINE_INFO = "zipline_info";

    public static ArgumentBuilder<CommandSource, ?> getBuilder() {
        return Commands
                .literal("zipline")
                .requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.literal("set")
                        .then(
                                Commands.argument(
                                        ARGS_NAME_HOOK_POS_1, BlockPosArgument.blockPos()
                                ).then(
                                        Commands.argument(
                                                ARGS_NAME_HOOK_POS_2, BlockPosArgument.blockPos()
                                        ).executes(c -> ZiplineCommand.setZipline(c, false)).then(
                                                Commands.argument(
                                                        ARGS_NAME_ZIPLINE_INFO, NBTTagArgument.nbtTag()
                                                ).executes(c -> ZiplineCommand.setZipline(c, true))
                                        )
                                )
                        )
                );
    }

    private static int setZipline(CommandContext<CommandSource> context, boolean hasInfo) throws CommandSyntaxException {
        BlockPos hook1 = BlockPosArgument.getOrLoadBlockPos(context, ARGS_NAME_HOOK_POS_1);
        BlockPos hook2 = BlockPosArgument.getOrLoadBlockPos(context, ARGS_NAME_HOOK_POS_2);
        ServerWorld level = context.getSource().getLevel();

        double horizontalDistSqr = MathHelper.square(hook1.getX() - hook2.getX()) + MathHelper.square(hook1.getZ() - hook2.getZ());
        if (horizontalDistSqr > Zipline.MAXIMUM_HORIZONTAL_DISTANCE * Zipline.MAXIMUM_HORIZONTAL_DISTANCE) {
            context.getSource().sendFailure(new TranslationTextComponent("parcool.command.message.hookTooFar"));
        }
        double verticalDist = Math.abs(hook2.getY() - hook1.getY());
        if (verticalDist * MathHelper.fastInvSqrt(horizontalDistSqr) > 1. || verticalDist > Zipline.MAXIMUM_VERTICAL_DISTANCE) {
            context.getSource().sendFailure(new TranslationTextComponent("parcool.command.message.ziplineTooSteep"));
        }
        TileEntity entity = level.getBlockEntity(hook1);
        if (!(entity instanceof ZiplineHookTileEntity)) {
            context.getSource().sendFailure(new TranslationTextComponent("parcool.command.message.hookNotFound", hook1.toShortString()));
            return 1;
        }
        TileEntity entity2 = level.getBlockEntity(hook2);
        if (!(entity2 instanceof ZiplineHookTileEntity)) {
            context.getSource().sendFailure(new TranslationTextComponent("parcool.command.message.hookNotFound", hook2.toShortString()));
            return 1;
        }

        ZiplineInfo info;
        if (hasInfo) {
            INBT infoTag = NBTTagArgument.getNbtTag(context, ARGS_NAME_ZIPLINE_INFO);
            info = ZiplineInfo.load(infoTag);
        } else {
            info = new ZiplineInfo(ZiplineType.STANDARD, ZiplineRopeItem.DEFAULT_COLOR);
        }

        if (!((ZiplineHookTileEntity) entity).connectTo((ZiplineHookTileEntity) entity2, info)) {
            return 1;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setZipline", hook1.toShortString(), hook2.toShortString()), true);
        return 0;
    }
}
