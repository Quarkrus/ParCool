package com.alrex.parcool.api.unstable.action;

import com.alrex.parcool.common.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ParCoolActionEvent extends Event {
    private final PlayerEntity player;
    private final Action action;

    public PlayerEntity getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }

    public ParCoolActionEvent(PlayerEntity player, Action action) {
        this.player = player;
        this.action = action;
    }

    @Deprecated
    @Cancelable
    public static class TryToStartEvent extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToStartEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    @Deprecated
    @Cancelable
    public static class TryToContinueEvent extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToContinueEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    @Deprecated
    public static class StartEvent extends ParCoolActionEvent {
        public StartEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    @Deprecated
    public static class StopEvent extends ParCoolActionEvent {
        public StopEvent(PlayerEntity player, Action action) {
            super(player, action);
        }
    }
    // ======

    public static class TryToStart extends ParCoolActionEvent {
        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToStart(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    @Cancelable
    public static class TryToContinue extends ParCoolActionEvent {

        @Override
        public boolean isCancelable() {
            return true;
        }

        public TryToContinue(PlayerEntity player, Action action) {
            super(player, action);
        }
    }

    public static class Start extends ParCoolActionEvent {
        private Start(PlayerEntity player, Action action) {
            super(player, action);
        }

        public static class Pre extends Start {
            public Pre(PlayerEntity player, Action action) {
                super(player, action);
            }
        }

        public static class Post extends Start {
            public Post(PlayerEntity player, Action action) {
                super(player, action);
            }
        }
    }

    public static class Finish extends ParCoolActionEvent {
        private Finish(PlayerEntity player, Action action) {
            super(player, action);
        }

        public static class Pre extends Finish {
            public Pre(PlayerEntity player, Action action) {
                super(player, action);
            }
        }

        public static class Post extends Finish {
            public Post(PlayerEntity player, Action action) {
                super(player, action);
            }
        }
    }

    public static class Tick extends ParCoolActionEvent {
        private Tick(PlayerEntity player, Action action) {
            super(player, action);
        }

        public static class Pre extends Tick {
            public Pre(PlayerEntity player, Action action) {
                super(player, action);
            }
        }

        public static class Post extends Tick {
            public Post(PlayerEntity player, Action action) {
                super(player, action);
            }
        }
    }

}
