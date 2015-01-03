package com.benjiweber.xorg;

import gnu.x11.Atom;
import gnu.x11.Display;
import gnu.x11.Window;
import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.Event;
import gnu.x11.event.Input;

import static java.util.Arrays.asList;

public class XOrg {

    private static int currentTime() {
        return (int)(System.currentTimeMillis()/1000);
    }

    public static Window activeWindow(Display display) {
        Window.Property activeWindow = display.default_root.get_property(false, findActiveWindowAtom(display), Atom.ANY_PROPERTY_TYPE, 0, 1);
        return new Window(display, activeWindow.value(0));
    }

    private static Atom activeAtom;
    private static Atom findActiveWindowAtom(Display display) {
        return activeAtom != null ? activeAtom : (activeAtom = asList(display.default_root.properties()).stream()
                .filter(atom -> "_NET_ACTIVE_WINDOW".equals(atom.name))
                .findFirst().orElseThrow(() -> new IllegalStateException("No _NET_ACTIVE_WINDOW property on root window")));
    }

    public static class Mouse {
        private final Display display;

        public Mouse(Display display) {
            this.display = display;
        }

        public interface SpecifyWindow {
            void on(Window window);
        }
        public SpecifyWindow scroll(ScrollDirection direction) {
            return window -> {
                Input press = scrollButton(display, window, new ButtonPress(display), 0x10, direction);
                Input release = scrollButton(display, window, new Input(display, ButtonRelease.CODE) {}, 0x810, direction);
                window.send_event(false, Event.NO_EVENT_MASK, press);
                window.send_event(false, Event.NO_EVENT_MASK, release);
                display.flush();
            };
        }


        public Input scrollButton(Display display, Window active, Input b, int state, ScrollDirection direction) {
            b.set_window(active);
            b.set_detail(direction.buttonNo);
            b.set_state(state);
            b.same_screen = true;
            b.time = currentTime();
            b.root_window_id = display.default_root.id();
            b.event_x = (active.get_geometry().width / 2);
            b.event_y = (active.get_geometry().height / 2);
            return b;
        }

    }

    public static enum ScrollDirection {
        up(4), down(5);
        public int buttonNo;

        ScrollDirection(int buttonNo) {
            this.buttonNo = buttonNo;
        }

    }
}
