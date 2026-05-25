package org.figuramc.fsb.api.events;

public abstract class Event {
    public boolean canContinue() {
        return true;
    }
}
