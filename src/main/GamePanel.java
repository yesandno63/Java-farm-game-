package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    //SCREEN SETTINGS
    final int OriginialTileSize=16;
    final int scale = 2;

    public final int tileSize=OriginialTileSize*scale;
    final int maxScreenRow=16;
    final int maxScreenCol=12;

    final int screenWidth=tileSize*maxScreenCol;
    final int screenHeight=tileSize*maxScreenRow;



    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.setFocusable(true);
    }

    @Override
    public void run() {

    }
}
