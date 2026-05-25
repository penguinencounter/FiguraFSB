package org.figuramc.fsb.api.events;

public abstract class ReturnableEvent<T> extends Event {
    private T returnValue = null;

    public void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }

    public T returnValue() {
        return returnValue;
    }

    public boolean returned() {
        return returnValue != null;
    }

    @Override
    public boolean canContinue() {
        return returnValue == null;
    }
}
