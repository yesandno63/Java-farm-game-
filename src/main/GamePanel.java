package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    //SCREEN SETTINGS
    final int OriginalTileSize =16;
    final int scale = 4;

    public final int tileSize= OriginalTileSize *scale;
    final int maxScreenRow=16;
    final int maxScreenCol=12;

    final int screenHeight=tileSize*maxScreenCol;
    final int screenWidth=tileSize*maxScreenRow;

    //FPS
    int FPS = 60;

    // Set player's default position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    KeyHandler KeyH= new KeyHandler();
    Thread gameThread;


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

        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if (delta >= 1){
                update();
                repaint();
                delta--;
            }
        }

    }

    private void update() {
        if (KeyH.upPressed){
            playerY -= playerSpeed;
        }
        if (KeyH.downPressed){
            playerY += playerSpeed;
        }
        if (KeyH.leftPressed){
            playerX -= playerSpeed;
        }
        if (KeyH.rightPressed){
            playerX += playerSpeed;
        }
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.white);
        g2.fillRect(playerX, playerY, tileSize, tileSize);

        g2.dispose();
    }
}
