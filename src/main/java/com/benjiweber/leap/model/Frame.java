package com.benjiweber.leap.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.function.Predicate;

import static com.benjiweber.leap.leapd.Frame.Handedness;
import static com.benjiweber.leap.leapd.Frame.Velocity;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Frame {
    public final Hand left;
    public final Hand right;

    public Frame(com.benjiweber.leap.leapd.Frame webSocketData) {
        this.left = findHand(Handedness.left).from(webSocketData);
        this.right = findHand(Handedness.right).from(webSocketData);
    }

    public static Frame fromString(String webSocketData) throws com.benjiweber.leap.leapd.Frame.NotAFrameException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return new Frame(mapper.readValue(webSocketData, com.benjiweber.leap.leapd.Frame.class));
        } catch (Exception e) {
            throw new com.benjiweber.leap.leapd.Frame.NotAFrameException(e);
        }
    }

    public boolean matches(Gesture gesture) {
        return gesture.visibleIn(this);
    }

    private interface HandFrom {
        Hand from(com.benjiweber.leap.leapd.Frame frame);
    }

    private HandFrom findHand(Handedness handedness) {
        return webSocketData -> webSocketData.hands.stream()
            .filter(hand -> hand.is(handedness))
            .map(h -> new Hand(h, webSocketData.pointables.stream().filter(finger -> h.id == finger.handId).collect(toList()), handedness))
            .findFirst()
            .orElse(Hand.INVISIBLE);
    }

    @Override
    public String toString() {
        return left.toString() + " " + right.toString();
    }

    public static class Hand {
        public final Digit thumb;
        public final Digit index;
        public final Digit middle;
        public final Digit ring;
        public final Digit pinkie;
        public final Handedness handedness;
        public final Velocity velocity;

        private Hand() {
            this(Digit.INVISIBLE, Digit.INVISIBLE, Digit.INVISIBLE, Digit.INVISIBLE, Digit.INVISIBLE, Handedness.invisible);
        }

        public Hand(com.benjiweber.leap.leapd.Frame.Hand webSocketData, List<com.benjiweber.leap.leapd.Frame.Pointable> pointables, Handedness handedness) {
            this.handedness = handedness;
            this.thumb = new Digit(pointables.get(handedness == handedness.left ? 4 : 0).extended);
            this.index = new Digit(pointables.get(handedness == handedness.left ? 3 : 1).extended);
            this.middle = new Digit(pointables.get(handedness == handedness.left ? 2 : 2).extended);
            this.ring = new Digit(pointables.get(handedness == handedness.left ? 1 : 3).extended);
            this.pinkie = new Digit(pointables.get(handedness == handedness.left ? 0 : 4).extended);
            this.velocity = webSocketData.velocity();
        }

        public Hand(Digit thumb, Digit index, Digit middle, Digit ring, Digit pinkie, Handedness handedness) {
            this.thumb = thumb;
            this.index = index;
            this.middle = middle;
            this.ring = ring;
            this.pinkie = pinkie;
            this.handedness = Handedness.invisible;
            this.velocity = new Velocity(asList(0f,0f,0f));
        }

        public boolean matches(Predicate<Hand> predicate) {
            return predicate.test(this);
        }

        public static class Digit {
            public final boolean extended;

            public Digit() { this(false); }

            public Digit(boolean extended) {
                this.extended = extended;
            }

            public boolean contracted() {
                return !extended;
            }

            public static class InvisibleDigit extends Digit {}
            public static InvisibleDigit INVISIBLE = new InvisibleDigit();

            @Override
            public String toString() {
                return extended ? "|" : ".";
            }
        }


        public static class InvisibleHand extends Hand {};
        public static InvisibleHand INVISIBLE = new InvisibleHand();

        public List<Digit> digit() {
            return handedness == Handedness.left ? asList(pinkie, ring, middle, index, thumb) : asList(thumb, index, middle, ring, pinkie);
        }

        @Override
        public String toString() {
            return digit().stream().map(Object::toString).collect(joining());
        }
    }
}
