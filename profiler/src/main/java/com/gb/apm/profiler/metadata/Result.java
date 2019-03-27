package com.gb.apm.profiler.metadata;

/**
 * @author emeroad
 */
public class Result {

    private final boolean newValue;
    private final int id;

    public Result(boolean newValue, int id) {
        this.newValue = newValue;
        this.id = id;
    }

    public boolean isNewValue() {
        return newValue;
    }

    public int getId() {
        return id;
    }

}
