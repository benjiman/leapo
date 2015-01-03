package com.benjiweber.leap.model;

import java.util.function.Predicate;

public interface HandGesture extends Predicate<Frame.Hand> {
    boolean matches(Frame.Hand hand);
    default boolean test(Frame.Hand hand) {
        return matches(hand);
    }
}
