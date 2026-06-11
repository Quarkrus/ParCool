package com.alrex.parcool.common.action;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class SynchronizedProperty<T> {
    private boolean dirty;
    @Nullable
    private T value;
    @Nullable
    private final IUpdateListener<T> updateListener;

    private SynchronizedProperty(@Nullable IUpdateListener<T> updateListener) {
        this.updateListener = updateListener;
    }

    public T get() {
        return value;
    }

    public void set(@Nullable T value) {
        if (this.value != value) {
            if (updateListener != null) updateListener.onUpdate(value, this.value);
            this.dirty = true;
            this.value = value;
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void sync(T value) {
        if (updateListener != null) updateListener.onUpdate(value, this.value);
        this.value = value;
    }

    public interface IUpdateListener<T> {
        void onUpdate(T newValue, T oldValue);
    }

    abstract IHandler<T> getHandler();

    interface IHandler<T> {
        byte dataLengthInBytes();

        @Nullable
        T read(ByteBuffer buffer);

        void write(ByteBuffer buffer, @Nullable T value);
    }

    public record Handler<T>(byte dataLengthInBytes, Function<ByteBuffer, T> reader,
                             BiConsumer<ByteBuffer, T> writer) implements IHandler<T> {
        @Override
        public T read(ByteBuffer buffer) {
            return reader.apply(buffer);
        }

        @Override
        public void write(ByteBuffer buffer, T value) {
            writer.accept(buffer, value);
        }
    }

    private static final Handler<Boolean> BOOLEAN_HANDLER = new Handler<>(
            (byte) 1,
            (b) -> switch (b.get()) {
                case 0 -> Boolean.FALSE;
                case 1 -> Boolean.TRUE;
                default -> null;
            },
            (b, v) -> b.put(v == null ? (byte) 255 : (v ? (byte) 1 : (byte) 0)));

    public static SynchronizedProperty<Boolean> newBoolean() {
        return newBoolean(null);
    }

    public static SynchronizedProperty<Boolean> newBoolean(@Nullable IUpdateListener<Boolean> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<Boolean> getHandler() {
                return BOOLEAN_HANDLER;
            }
        };
    }

    private static final Handler<java.lang.Byte> BYTE_HANDLER = new Handler<>(
            (byte) 2,
            (b) -> {
                var notNull = b.get();
                var v = b.get();
                return notNull != 0 ? v : null;
            },
            (b, v) -> {
                b.put(v != null ? (byte) 1 : (byte) 0);
                b.put(v != null ? v : 0);
            }
    );

    public static SynchronizedProperty<java.lang.Byte> newByte() {
        return newByte(null);
    }

    public static SynchronizedProperty<java.lang.Byte> newByte(@Nullable IUpdateListener<java.lang.Byte> updateListener) {
        return new SynchronizedProperty<>(updateListener) {
            @Override
            IHandler<java.lang.Byte> getHandler() {
                return BYTE_HANDLER;
            }
        };
    }

    private static class EnumHandler<T extends Enum<T>> implements IHandler<T> {
        private final Class<T> enumClass;

        public EnumHandler(Class<T> enumClass) {
            this.enumClass = enumClass;
            assert (this.enumClass.getEnumConstants().length < 255);
        }

        @Override
        public byte dataLengthInBytes() {
            return (byte) 1;
        }

        @Override
        public T read(ByteBuffer buffer) {
            var value = buffer.get();
            if (value == (byte) 255) return null;
            var constants = enumClass.getEnumConstants();
            return constants[value % constants.length];
        }

        @Override
        public void write(ByteBuffer buffer, T value) {
            if (value == null) buffer.put((byte) 255);
            else buffer.put((byte) value.ordinal());
        }
    }

    private static class EnumSynchronizationProperty<T extends Enum<T>> extends SynchronizedProperty<T> {
        private final EnumHandler<T> enumHandler;

        public EnumSynchronizationProperty(Class<T> enumClass, @Nullable IUpdateListener<T> updateListener) {
            super(updateListener);
            enumHandler = new EnumHandler<>(enumClass);
        }

        @Override
        IHandler<T> getHandler() {
            return enumHandler;
        }
    }

    public static <T extends Enum<T>> SynchronizedProperty<T> newEnum(Class<T> enumClass) {
        return newEnum(enumClass, null);
    }

    public static <T extends Enum<T>> SynchronizedProperty<T> newEnum(Class<T> enumClass, @Nullable IUpdateListener<T> updateListener) {
        return new EnumSynchronizationProperty<>(enumClass, updateListener);
    }
}
