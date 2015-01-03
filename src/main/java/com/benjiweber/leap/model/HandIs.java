package com.benjiweber.leap.model;

public class HandIs {
    public static HandGesture Fist = hand -> hand.digit().stream().allMatch(Frame.Hand.Digit::contracted);
    public static HandGesture Upwards = hand -> hand.velocity.y > 100;
    public static HandGesture Downwards = hand -> hand.velocity.y < -100;
}
