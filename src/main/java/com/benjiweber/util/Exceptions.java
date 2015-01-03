package com.benjiweber.util;

public class Exceptions {

    public interface ExceptionalSupplier<R, E extends Exception> {
        R get() throws E;
    }
    public static <R, E extends Exception> R unchecked(ExceptionalSupplier<R, E> f) {
        try {
            return f.get();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
