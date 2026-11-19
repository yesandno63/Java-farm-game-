package entity;

import java.awt.image.BufferedImage;
import java.util.Map;

public class Entity {

    public int worldX,worldY;
    public int speed;

    public BufferedImage[] framesDown, framesUp, framesLeft, framesRight, framesIdleDown, framesIdleUp, framesIdleLeft, framesIdleRight;
    public String direction;
    int scalePlayer = 4;

    public int spriteCounter = 0;
    public int spriteNum = 1;
    public Map<String, Integer> animationFrames;
}
