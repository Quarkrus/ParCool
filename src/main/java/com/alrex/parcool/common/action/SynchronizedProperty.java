package com.alrex.parcool.common.action;

import java.nio.ByteBuffer;

public abstract class SynchronizedProperty<T> {
    private boolean dirty;
    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        this.dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void sync(T value) {
        this.value = value;
    }

    abstract IHandler<T> getHandler();

    interface IHandler<T> {
        byte dataLengthInBytes();

        T read(ByteBuffer buffer);

        void write(ByteBuffer buffer, T value);
    }

}
