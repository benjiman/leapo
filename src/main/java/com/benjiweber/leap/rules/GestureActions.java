package com.benjiweber.leap.rules;

import com.benjiweber.leap.model.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;

import static java.util.concurrent.CompletableFuture.runAsync;

public class GestureActions {

    private List<GestureAction> actions = new ArrayList<>();

    public GestureSpecifyAction whenYouSee(Predicate<Frame> gesture) {
        return action -> {
            actions.add(new GestureAction(gesture, action));
            return this;
        };
    }


    public void apply(Frame frame) {
        runAsync(() -> actions.stream().forEach(action -> action.apply(frame)));
    }

    public interface GestureSpecifyAction {
        GestureActions thenDo(Runnable action);
    }

    public static class GestureAction {
        private final Predicate<Frame> gesture;
        private final Runnable action;
        Semaphore oneAtATime = new Semaphore(1);
        public GestureAction(Predicate<Frame> gesture, Runnable action) {
            this.gesture = gesture;
            this.action = action;
        }

        public void apply(Frame frame) {
            if (gesture.test(frame)) {
                if (oneAtATime.tryAcquire()) {
                    runAsync(() -> {
                        try {
                            action.run();
                        } finally {
                            oneAtATime.release();
                        }
                    });
                }
            }
        }
    }

}
