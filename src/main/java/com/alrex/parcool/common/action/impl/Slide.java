package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.ContinuableAction;

public class Slide extends ContinuableAction {
    public Slide(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canContinue() {
        return false;
    }
}
