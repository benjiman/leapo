package com.benjiweber.leap;

import com.benjiweber.leap.leapd.Leapd;
import com.benjiweber.leap.model.HandIs;
import com.benjiweber.leap.rules.GestureActions;
import gnu.x11.Display;

import static com.benjiweber.xorg.XOrg.Mouse;
import static com.benjiweber.xorg.XOrg.ScrollDirection.down;
import static com.benjiweber.xorg.XOrg.ScrollDirection.up;
import static com.benjiweber.xorg.XOrg.activeWindow;

public class Leapy {

    public static void main(String... args) {
        Display display = new Display("127.0.0.1",0,0);
        Mouse mouse = new Mouse(display);

        GestureActions actions = new GestureActions()
            .whenYouSee(hands -> hands.right.matches(HandIs.Fist.and(HandIs.Upwards)))
            .thenDo(() -> mouse.scroll(up).on(activeWindow(display)))
            .whenYouSee(hands -> hands.right.matches(HandIs.Fist.and(HandIs.Downwards)))
            .thenDo(() -> mouse.scroll(down).on(activeWindow(display)));

        Leapd.webSocketClient()
            .onFrame(actions::apply)
            .run();
    }

}

