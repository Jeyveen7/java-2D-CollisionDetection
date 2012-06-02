/* ----------------------------------------------------------------------------
 * Copyright : (c) Svein Inge <Thhethssmuz> Albrigtsen 2012
 * License   : MIT
 * ----------------------------------------------------------------------------
 * 
 * Housing and launcher for GameWorld.java.
 *
 */

import java.awt.*;
import javax.swing.*;

public class MyGame extends JFrame {

    public MyGame() {

        int[][] level1 = {
            {1,   1,   1,   598 }, // outer walls
            {1,   1,   798, 1   }, // --
            {1,   598, 798, 598 }, // --
            {798, 1,   798, 598 }, // --
            {1,   81,  41,  121 }, // start ramp
        };

        add(new GameWorld(level1));

        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int x = (screen.getDisplayMode().getWidth() / 2) - 400;
        int y = (screen.getDisplayMode().getHeight() / 2) - 300;

        this.setLocation(x, y);
        this.setTitle("MyGame");
        this.setSize(804, 628);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new MyGame();
    }

}
