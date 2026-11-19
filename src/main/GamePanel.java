package main;

import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    //SCREEN SETTINGS
    final int OriginalTileSize =32;
    final int scale = 2;

    public final int tileSize=OriginalTileSize*scale;
    public final int maxScreenRow=16;
    public final int maxScreenCol=12;

    public final int screenHeight=tileSize*maxScreenCol;
    public final int screenWidth=tileSize*maxScreenRow;

    //WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth=tileSize*maxWorldCol;
    public final int worldHeight=tileSize*maxWorldRow;


    //FPS
    int FPS = 60;

    // Set player's default position (ancien system du debut)
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    KeyHandler KeyH= new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, KeyH);
    TileManager tileManager = new TileManager(this);


    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(KeyH);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        long timer = 0;
        int drawCount = 0;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            timer += (currentTime - lastTime);

            lastTime = currentTime;

            if (delta >= 1){
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if(timer > 1000000000) {

                drawCount = 0;
                timer = 0;
            }
        }

    }

    private void update() {
        player.update();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);
        player.draw(g2);

        g2.dispose();
    }
}
