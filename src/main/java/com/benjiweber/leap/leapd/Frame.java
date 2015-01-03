package com.benjiweber.leap.leapd;

import java.util.List;

public class Frame {

    public List<Hand> hands;
    public List<Pointable> pointables;

    public static class NotAFrameException extends Exception {
        public NotAFrameException(Exception e) {
            super(e);
        }
    }

    public static class Hand {
        public List<Float> palmVelocity;
        public Handedness type;
        public int id;
        public Velocity velocity() {
            return new Velocity(palmVelocity);
        }

        public boolean is(Handedness handedness) {
            return type == handedness;
        }

    }

    public static class Velocity {
        public Float x, y ,z;
        public Velocity(List<Float> coords) {
            this.x = coords.get(0);
            this.y = coords.get(1);
            this.z = coords.get(2);
        }
    }

    public static enum Handedness {
        left,right,invisible
    }

    public static class Pointable {
        public int handId;
        public boolean extended;
    }
}