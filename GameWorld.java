/* ----------------------------------------------------------------------------
 * Copyright : (c) Svein Inge <Thhethssmuz> Albrigtsen 2012
 * License   : MIT
 * ----------------------------------------------------------------------------
 * 
 * A drawable plane, for 2D game... 
 *   and a simple sample game.
 * 
 */

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

import java.util.ArrayList;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class GameWorld extends    JPanel 
                       implements ActionListener,
                                  MouseListener, 
                                  MouseMotionListener {

    BufferedImage ball;
    BufferedImage world;
    Timer timer;
    ArrayList<int[]> worldData = new ArrayList<int[]>();
    ArrayList<int[]> drawData = new ArrayList<int[]>();

    double[] p = {20,20};    // ball position
    double[] v = {0,0};      // velocity vector

    int[] mp = {-1,-1,-1,-1}; // mouse position

    int highscores[] = {0,0,0,0};
    int level = 1;
    int score = 0;
    int time = 0;
    int length = 0;
    boolean goal = false;

    public GameWorld(int[][] levelData) {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);

        loadScores();

        for (int[] il : levelData) worldData.add(il);
        drawData.addAll(worldData);

        // Draw the level
        world = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics w = world.createGraphics();
        for (int[] i : levelData) {
            w.drawLine(i[0], i[1], i[2], i[3]);
        }
        w.setColor(Color.GREEN);
        w.drawLine(729, 540, 797, 540);
        w.dispose();

        // Draw ball // could easily use an image for more fancy graphics
        ball = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics tmp = ball.createGraphics();
        tmp.drawOval(0,0,31,31);
        tmp.dispose();

        // Start timer
        timer = new Timer(25, this);
        timer.start();
    }

    private void saveScores() {
        try {
            FileOutputStream f =
            new FileOutputStream("highscores.ser");
            ObjectOutputStream out = new ObjectOutputStream(f);
            out.writeObject(highscores);
            out.close();
            f.close();
        } catch(Exception e) {}
    }

    private void loadScores() {
        try {
            FileInputStream f = new FileInputStream("highscores.ser");
            ObjectInputStream in = new ObjectInputStream(f);
            highscores = (int[]) in.readObject();
            in.close();
            f.close();
        } catch(Exception e) {}
    }

    public void setLevel(int[][] levelData, int level) {
        worldData.clear();
        this.level = level;
        for (int[] il : levelData) worldData.add(il);
        reset();
    }


    // repaint ----------------------------------------------------------------
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(world, 0, 0, this);
        g.drawImage(ball,
                    ((int)p[0]-16),
                    ((int)p[1]-16),
                    this);
        g.setColor(Color.GRAY);
        g.drawLine(mp[0],mp[1],mp[2],mp[3]);
        g.setColor(Color.RED);

        g.drawString("Time", 650,20);
        g.drawString(readableTime(time),730,20);
        g.drawString("Length",650,35);
        g.drawString(""+length, 730,35);
        g.drawString("Score",650,50);
        g.drawString(""+score, 730,50);
        g.drawString("HighScore", 650,65);
        g.drawString(""+highscores[level-1],730,65);

        g.dispose();
    }


    // Other functions --------------------------------------------------------
    public static String readableTime(int ms) {
        // ignores hours
        return String.format("%02d:%02d",
            (ms/60000)%60,
            (ms/1000)%60
            );
    }

    public void reset() {
        p = new double[] {18,18};
        v = new double[] {0,0};
        mp = new int[] {-1,-1,-1,-1};

        time = 0;
        length = 0;
        score = 0;
        goal = false;

        drawData.clear();
        drawData.addAll(worldData);

        Graphics w = world.createGraphics();
        w.setColor(Color.BLACK);
        w.fillRect(0,0,800,600);
        w.setColor(Color.WHITE);
        for (int[] i : worldData) {
            w.drawLine(i[0], i[1], i[2], i[3]);
        }
        w.setColor(Color.GREEN);
        w.drawLine(729, 540, 797, 540);
        w.dispose();
    }


    // Action events (Timer) --------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        // basic gravitation and friction
        v[1] += 0.25;
        v[0] *= 0.98; // somehow makes the ball feel heavier
        v[1] *= 0.98;
        // Update position
        p[0] += v[0];
        p[1] += v[1];
        // run collision detection for new position
        double[] update = CollisionDetection.collisionDetection(
            p[0], p[1], 16, v[0], v[1], drawData);

        p[0] = update[0];
        p[1] = update[1];
        v[0] = update[2];
        v[1] = update[3];

        if (p[0] >= 730 && p[1] >= 540) {
            goal = true;
            score = (100000/time)*(2000/length);

            if (score > highscores[level-1]) {
                highscores[level-1] = score;
                saveScores();
            }
        }

        // update timer
        if (!goal) time += 25;

        repaint();
    }


    // Mouse events -----------------------------------------------------------
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        mp[0] = mp[2] = e.getX();
        mp[1] = mp[3] = e.getY();
    }
    public void mouseReleased(MouseEvent e) {
        mp[2] = e.getX();
        mp[3] = e.getY();

        if (! CollisionDetection.toClose(p[0], p[1], 16,
                                         mp[0],mp[1],mp[2],mp[3])) {

            if (!goal) length += Math.hypot(mp[2]-mp[0],mp[3]-mp[1]);

            Graphics w = world.createGraphics();
            w.drawLine(mp[0], mp[1], mp[2], mp[3]);
            w.dispose();
            drawData.add(new int[] {mp[0],mp[1],mp[2],mp[3]});
            mp = new int[] {-1,-1,-1,-1};

        } else reset();
    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        mp[2] = e.getX();
        mp[3] = e.getY();
    }

}
