package com.alrex.parcool.server.command.args;

import com.alrex.parcool.server.limitation.ILimitationEntry;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LimitationItemArgumentType implements ArgumentType<Object> {
    private final List<String> paths;
    private final Object[] enumConstants;

    @Override
    public Object parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        int index = paths.indexOf(name);
        if (index == -1) {
            Message message = Component.translatable("parcool.command.message.invalidConfigName", name);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
        return enumConstants[index];
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (String name : paths.stream().filter(it -> it.startsWith(remain)).toList()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return paths;
    }

    LimitationItemArgumentType(Class<?> clazz) {
        enumConstants = clazz.getEnumConstants();
        paths = new ArrayList<>(enumConstants.length);
        for (Object enumConstant : enumConstants) {
            var item = (ILimitationEntry<?>) enumConstant;
            paths.add(item.name());
        }
    }

    public static class Booleans extends LimitationItemArgumentType {
        Booleans() {
            super(ILimitationEntry.Bool.class);
        }
    }

    public static class Integers extends LimitationItemArgumentType {
        Integers() {
            super(ILimitationEntry.Int.class);
        }
    }

    public static class Doubles extends LimitationItemArgumentType {
        Doubles() {
            super(ILimitationEntry.Real.class);
        }
    }

    public static LimitationItemArgumentType.Booleans booleans() {
        return new Booleans();
    }

    public static LimitationItemArgumentType.Integers integers() {
        return new Integers();
    }

    public static LimitationItemArgumentType.Doubles doubles() {
        return new Doubles();
    }

    public static ILimitationEntry.Bool getBool(CommandContext<?> context, String name) {
        return context.getArgument(name, ILimitationEntry.Bool.class);
    }

    public static ILimitationEntry.Int getInt(CommandContext<?> context, String name) {
        return context.getArgument(name, ILimitationEntry.Int.class);
    }

    public static ILimitationEntry.Real getDouble(CommandContext<?> context, String name) {
        return context.getArgument(name, ILimitationEntry.Real.class);
    }
}