package org.example;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class App {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Application";
        config.useGL30 = false;
        config.width = 1600;
        config.height = 700;
        new LwjglApplication( new MainWindow(), config);
    }
}