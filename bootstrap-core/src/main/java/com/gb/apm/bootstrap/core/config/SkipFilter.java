package com.gb.apm.bootstrap.core.config;

/**
 * @author emeroad
 */
public class SkipFilter<T> implements Filter<T> {
    @Override
    public boolean filter(T value) {
        return false;
    }
}
