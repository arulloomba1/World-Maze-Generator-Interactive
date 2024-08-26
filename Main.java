package core;

import tileengine.TERenderer;
import tileengine.TETile;

public class Main {
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(80, 50);
        TETile[][] world = AutograderBuddy.getWorldFromInput("N58486303s");
        ter.renderFrame(world);
    }
}
