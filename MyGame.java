/* ----------------------------------------------------------------------------
 * Copyright : (c) Svein Inge <Thhethssmuz> Albrigtsen 2012
 * License   : MIT
 * ----------------------------------------------------------------------------
 * 
 * launcher.
 *
 */

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class MyGame extends JFrame implements ActionListener {

        // Levels -------------------------------------------------------------
        int[][] level1 = {
            {1,   1,   1,   598 }, // outer walls
            {1,   1,   798, 1   }, // --
            {1,   598, 798, 598 }, // --
            {798, 1,   798, 598 }, // --
            {1,   81,  41,  121 }, // start ramp
            {728, 538, 728, 598 }, // bucket/goal
            {728, 537, 738, 598 },
        };

        int[][] level2 = {
            {1,   1,   1,   598 }, // outer walls
            {1,   1,   798, 1   }, // --
            {1,   598, 798, 598 }, // --
            {798, 1,   798, 598 }, // --
            {1,   81,  41,  121 }, // start ramp
            {1,   200, 500, 200 },
            {798, 400, 200, 400 },
            {728, 538, 728, 598 }, // bucket/goal
            {728, 537, 738, 598 },
        };

        int[][] level3 = {
            {1,   1,   1,   598 }, // outer walls
            {1,   1,   798, 1   }, // --
            {1,   598, 798, 598 }, // --
            {798, 1,   798, 598 }, // --
            {1,   81,  41,  121 }, // start ramp
            {300, 1,   300, 300 },
            {400, 300, 400, 598 },
            {728, 538, 728, 598 }, // bucket/goal
            {728, 537, 738, 598 },
        };

        int[][] level4 = {
            {1,   1,   1,   598 }, // outer walls
            {1,   1,   798, 1   }, // --
            {1,   598, 798, 598 }, // --
            {798, 1,   798, 598 }, // --
            {1,   81,  41,  121 }, // start ramp
            {50,  31,  100, 81  },
            {80,  81,  80,  450 },
            {41,  121, 41,  500 },
            {41,  480, 61,  540 },
            {41,  500, 81,  540 },
            {41,  520, 101, 540 },
            {41,  540, 400, 540 },
            {400, 400, 400, 798 },
            {728, 538, 728, 598 }, // bucket/goal
            {728, 537, 738, 598 },
        };

    // Other ------------------------------------------------------------------

    private JMenuItem l1 = new JMenuItem("Level 1");
    private JMenuItem l2 = new JMenuItem("Level 2");
    private JMenuItem l3 = new JMenuItem("Level 3");
    private JMenuItem l4 = new JMenuItem("Level 4");

    GameWorld gw = new GameWorld(level1);

    public MyGame() {
        JMenuBar main = new JMenuBar();
        this.setJMenuBar(main);
        main.add(l1);
        l1.addActionListener(this);
        main.add(l2);
        l2.addActionListener(this);
        main.add(l3);
        l3.addActionListener(this);
        main.add(l4);
        l4.addActionListener(this);
        // Lazy man's layout fixing.
        main.add(new JLabel("                                              "+
                            "                                              "+
                            "                    "));
        add(gw);

        GraphicsDevice screen = GraphicsEnvironment
                                .getLocalGraphicsEnvironment()
                                .getDefaultScreenDevice();
        int x = (screen.getDisplayMode().getWidth() / 2) - 400;
        int y = (screen.getDisplayMode().getHeight() / 2) - 300;

        this.setResizable(false); // annoying, as it is easy to do by accident
        this.setLocation(x, y);
        this.setTitle("MyGame");
        this.setSize(804, 648);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if      (e.getSource() == l1) gw.level(level1);
        else if (e.getSource() == l2) gw.level(level2);
        else if (e.getSource() == l3) gw.level(level3);
        else if (e.getSource() == l4) gw.level(level4);
    }

    public static void main(String[] args) {
        new MyGame();
    }

}
