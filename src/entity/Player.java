package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.HashMap;

public class Player extends Entity{

    GamePanel gp;
    KeyHandler KeyH;

    public final int screenX;
    public final int screenY;

    //Nombre de sprite par spriteSheet
    int frameCount = 8;

    public Player(GamePanel gamePanel, KeyHandler keyHandler){
        this.gp = gamePanel;
        this.KeyH = keyHandler;

        screenX = gp.screenWidth/2 -(gp.tileSize*2);
        screenY = gp.screenHeight/2 -(gp.tileSize*2);

        animationFrames = new HashMap<>();

        setDefaultValues();
        loadAnimationFrames();
        getPlayerImage();


    }

    public void setDefaultValues() {

        worldX = gp.tileSize*2;
        worldY = gp.tileSize*2;
        speed = 4;
        direction="down";
    }

    //Obtention des images formant les animations et sprite du personages
    public void getPlayerImage(){
        //taille des frames
        int framesWidth = 96;
        int framesHeight = 80;

        //Tableau pour stocker images du perso qui bouge découpé (en gros frames des animations)
        framesDown = new BufferedImage[frameCount];
        framesUp = new BufferedImage[frameCount];
        framesLeft = new BufferedImage[frameCount];
        framesRight = new BufferedImage[frameCount];

        //Tableau pour stocker images du perso qui bouge découpé (en gros frames des animations idle)
        framesIdleDown = new BufferedImage[frameCount];
        framesIdleUp = new BufferedImage[frameCount];
        framesIdleLeft = new BufferedImage[frameCount];
        framesIdleRight = new BufferedImage[frameCount];

        //Buffer pour image du perso qui bouge
        BufferedImage runDown = null;
        BufferedImage runUp = null;
        BufferedImage runLeft = null;
        BufferedImage runRight = null;

        //Buffer pour image du perso qui bouge pas
        BufferedImage IdleDown = null;
        BufferedImage IdleUp = null;
        BufferedImage IdleLeft = null;
        BufferedImage IdleRight = null;

        //Récupération des flux d'images puis on stock l'image dans un buffer d'image
        try {

            runDown = ImageIO.read(getClass().getResourceAsStream("/res/player/RUN/run_down.png"));
            runUp = ImageIO.read(getClass().getResourceAsStream("/res/player/RUN/run_up.png"));
            runLeft = ImageIO.read(getClass().getResourceAsStream("/res/player/RUN/run_left.png"));
            runRight = ImageIO.read(getClass().getResourceAsStream("/res/player/RUN/run_right.png"));

            IdleDown = ImageIO.read(getClass().getResourceAsStream("/res/player/IDLE/idle_down.png"));
            IdleUp = ImageIO.read(getClass().getResourceAsStream("/res/player/IDLE/idle_up.png"));
            IdleLeft = ImageIO.read(getClass().getResourceAsStream("/res/player/IDLE/idle_left.png"));
            IdleRight = ImageIO.read(getClass().getResourceAsStream("/res/player/IDLE/idle_right.png"));

        }catch(Exception e){
            e.printStackTrace();
        }

        //On divise les images complète en plusieurs plus petite qu'on stock à la suite dans un tab de frames
        for(int i = 0; i< frameCount; ++i){
            //On rempli les tableau avec les images du perso qui bouge découpé
            assert runDown != null;
            framesDown[i] = runDown.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
            assert runUp != null;
            framesUp[i] = runUp.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
            assert runLeft != null;
            framesLeft[i] = runLeft.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
            assert runRight != null;
            framesRight[i] = runRight.getSubimage(i*framesWidth,0,framesWidth,framesHeight);

            //On rempli les tableau avec les images du perso qui bouge pas découpé
            assert IdleDown != null;
            framesIdleDown[i] = IdleDown.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
            assert IdleUp != null;
            framesIdleUp[i] = IdleUp.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
            assert IdleLeft != null;
            framesIdleLeft[i] = IdleLeft.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
            assert IdleRight != null;
            framesIdleRight[i] = IdleRight.getSubimage(i*framesWidth,0,framesWidth,framesHeight);
        }
    }

    public void loadAnimationFrames(){
        animationFrames.put("down", frameCount);
        animationFrames.put("up", frameCount);
        animationFrames.put("left", frameCount);
        animationFrames.put("right", frameCount);

        animationFrames.put("idleDown", frameCount);
        animationFrames.put("idleUp", frameCount);
        animationFrames.put("idleLeft", frameCount);
        animationFrames.put("idleRight", frameCount);
    }

    public void update(){

        boolean moving = false;

        //On récup la direction dans laquelle se dirige le player et s'il bouge (en gros on regarde s'il appuie sur une touche)
        if (KeyH.upPressed){
            setDirection("up");
            worldY -= speed;
            moving = true;
        }
        else if (KeyH.downPressed){
            setDirection("down");
            worldY += speed;
            moving = true;
        }
        else if (KeyH.leftPressed){
            setDirection("left");
            worldX -= speed;
            moving = true;
        }
        else if (KeyH.rightPressed){
            setDirection("right");
            worldX += speed;
            moving = true;
        }

        //Gestion des animations
        spriteCounter++;

        if(moving){
            if(spriteCounter > 10){
                spriteCounter = 0;
                int maxFrames = animationFrames.get(direction);
                spriteNum = (spriteNum + 1) % maxFrames;
            }
        }
        else{
            switch (direction){
                case "down":
                    direction = "idleDown";
                    break;
                case "up":
                    direction = "idleUp";
                    break;
                case "left":
                    direction = "idleLeft";
                    break;
                case "right":
                    direction = "idleRight";
                    break;
            }

            spriteCounter++;
            if(spriteCounter > 20){
                spriteCounter = 0;
                spriteNum = (spriteNum + 1) % frameCount;
            }
        }
    }

    public void setDirection(String newDirection){
        if(!direction.equals(newDirection)){
            direction = newDirection;
            spriteNum = 0;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = framesUp[spriteNum];
                break;
            case "down":
                image = framesDown[spriteNum];
                break;
            case "left":
                image = framesLeft[spriteNum];
                break;
            case "right":
                image = framesRight[spriteNum];
                break;
            case "idleDown":
                image = framesIdleDown[spriteNum];
                break;
            case "idleUp":
                image = framesIdleUp[spriteNum];
                break;
            case "idleLeft":
                image = framesIdleLeft[spriteNum];
                break;
            case "idleRight":
                image = framesIdleRight[spriteNum];
                break;
        }
        g2.drawImage(image, screenX, screenY, gp.tileSize*scalePlayer, gp.tileSize*scalePlayer, null);
    }
}
