package com.benjiweber.leap.leapd;

import com.benjiweber.leap.model.Frame;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

import static com.benjiweber.util.Exceptions.unchecked;

public class Leapd extends WebSocketClient {

    private Consumer<Frame> onFrame;

    public Leapd(Consumer<Frame> onFrame) {
        super(unchecked(() -> new URI("ws://127.0.0.1:6437/v6.json")));
        this.onFrame = onFrame;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }


    @Override
    public void onMessage(String s) {
        send("{\"enableGestures\": \"true\"}");
        send("{\"focused\": \"true\"}");
        try {
            Frame frame = Frame.fromString(s);
            onFrame.accept(frame);
        } catch (com.benjiweber.leap.leapd.Frame.NotAFrameException e) {

        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        throw new RuntimeException(e);
    }

    public interface LeapdBuilder {
        Leapd onFrame(Consumer<Frame> frameConsumer);
    }

    public static LeapdBuilder webSocketClient() {
        return frameConsumer -> new Leapd(frameConsumer);
    }
}
