package com.benjiweber.leap.model;

import java.util.function.Predicate;

public interface Gesture extends Predicate<Frame> {
    boolean visibleIn(Frame frame);
    default boolean test(Frame frame) {
        return visibleIn(frame);
    }
}
