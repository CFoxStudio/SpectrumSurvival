package dev.cfox.gamejam.game;

import java.util.Random;

public class Phase {
    public static void random() {
        Phases[] phases = Phases.values();
        int randomIndex = new Random().nextInt(phases.length);
        Phases selectedPhase = phases[randomIndex];

        switch (selectedPhase) {
            case RED -> red();
            case BLUE -> blue();
            case GREEN -> green();
            case YELLOW -> yellow();
            case PURPLE -> purple();
        }
    }

    public static void red() {
        // Handle red phase logic
    }
    public static void blue() {
        // Handle blue phase logic
    }
    public static void green() {
        // Handle green phase logic
    }
    public static void yellow() {
        // Handle yellow phase logic
    }
    public static void purple() {
        // Handle purple phase logic
    }
}
