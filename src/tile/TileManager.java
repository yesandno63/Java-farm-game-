package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TileManager {

    GamePanel gp;
    List<TileSet> tileSets;

    //Tableau des tiles
    int[][] groundLayer;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tileSets = new ArrayList<TileSet>();

        // La couche est indexée [ligne][colonne] : 12 lignes et 16 colonnes.
        groundLayer = new int[gp.maxScreenCol][gp.maxScreenRow];

        loadAllTileSets();

        //Debut nombre de TileSets
        System.out.println("TileSets count = " + tileSets.size());

        // Charger les maps avec les tilesets correspondants
        // Les tiles démarre au GID 0
        loadMap("/res/Maps/farm.csv", groundLayer, 0);
    }

    //On load chaque Tile Sets (si y'en a plusieurs)
    private void loadAllTileSets() {
        loadTileSets(
                "/res/TileSheet/Environement/tileset_32x32_tiled.png",
                32,
                0
        );
    }

    //On récupère chaque tile à partir d'une image
    private void loadTileSets(String path, int sourceTileSize, int firstGID) {

        BufferedImage image;

        //On récupère l'image dans un buffer
        try {
            image = ImageIO.read(getClass().getResource(path));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        //Calcul des dimensions d'une tile (pixel / taille de la tile)
        int cols = image.getWidth() / sourceTileSize;
        int rows = image.getHeight() / sourceTileSize;

        ArrayList<Tile> tiles = new ArrayList<>();

        //Découpage de l'image tile par tile
        for(int row = 0; row < rows; ++row) {
            for(int col = 0; col < cols; ++col) {

                Tile tile = new Tile();
                tile.image = image.getSubimage(
                        col * sourceTileSize,
                        row * sourceTileSize,
                        sourceTileSize,
                        sourceTileSize
                );

                tiles.add(tile);
            }
        }

        tileSets.add(new TileSet(firstGID, tiles));
    }

    public Tile getTileByGID(int gid){
        if(gid == -1) return null;

        for(int i = tileSets.size() - 1; i >= 0; --i){
            TileSet set = tileSets.get(i);

            if (gid >= set.firstGID) {
                int index = gid - set.firstGID;

                if(index >= 0 && index < set.tiles.size()){
                    return set.tiles.get(index);
                } else {
                    System.out.println("INDEX OUT OF BOUNDS! GID " + gid + " -> index " + index + " but size is " + set.tiles.size());
                }
                break;
            }
        }
        System.out.println("NULL returned for GID: " + gid);
        return null;
    }

    public void loadMap(String path, int[][] layer, int tileSetFirstGid){

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream(path)
                    )
            );

            int rowCount = 0;
            for(int row = 0; row < gp.maxScreenCol; ++row){

                String[] values = br.readLine().split(",");

                for(int col = 0; col < gp.maxScreenRow; ++col){
                    int localGid = Integer.parseInt(values[col]);

                    //Convertion indice local en GID global
                    //En gros la map est fait avec Tile qui utilise ses GID, donc on les converti pour pas foutre la merde avec les autres
                    int globalGid;

                    if(localGid == -1){
                        globalGid = -1; // Ya pas de tile bro, en gros problème
                    } else {
                        // On le convertit en GID global en ajoutant le firstGid du tileset
                        globalGid = localGid + tileSetFirstGid;
                    }

                    if(rowCount < 3 && col < 5){
                        System.out.println("loadMap [" + row + "," + col + "]: localGid=" + localGid + " + firstGid(" + tileSetFirstGid + ") = globalGid=" + globalGid);
                    }

                    layer[row][col] = globalGid;
                }
                ++rowCount;
            }
            br.close();
            System.out.println("Map loaded with firstGid=" + tileSetFirstGid);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void drawLayer(Graphics2D g2, int[][] layer){

        int x = 0;
        int y = 0;

        for(int row = 0; row < gp.maxScreenCol; ++row){
            for(int col = 0; col < gp.maxScreenRow; ++col){

                int gid = layer[row][col];
                Tile tile = getTileByGID(gid);

                if(tile != null && tile.image != null) {
                    //DEBUG
                    g2.drawImage(
                            tile.image,
                            x, y,
                            gp.tileSize, gp.tileSize,
                            null
                    );
                }

                //DEBUG tile si jamais ya une merde dans les tile, ou jsp mais tkt
                if (tile == null) {
                    g2.setColor(Color.RED);
                    g2.drawRect(x, y, gp.tileSize, gp.tileSize);
                }

                x += gp.tileSize;
            }
            x = 0;
            y += gp.tileSize;
        }
    }

    public void draw(Graphics2D g2){
        drawLayer(g2, groundLayer);
    }
}
