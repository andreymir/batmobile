package com.example.batmobile.arduino;

;

public class Options {
    public Mode mode = Mode.Manual;

    private static Options instance;

    public static synchronized Options getInstance() {
        if (instance == null) {
            instance = new Options();
        }
        return instance;
    }
}
