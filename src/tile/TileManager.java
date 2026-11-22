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

        // La couche est indexée [collone][ligne] : 12 lignes et 16 colonnes.
        groundLayer = new int[gp.maxWorldCol][gp.maxWorldRow];

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
            for(int row = 0; row < gp.maxWorldRow; ++row){

                String[] values = br.readLine().split(",");

                for(int col = 0; col < gp.maxWorldCol; ++col){

                    int localGid = Integer.parseInt(values[col]);

                    int globalGid;

                    if(localGid == -1){
                        globalGid = -1;
                    } else {
                        globalGid = localGid + tileSetFirstGid;
                    }

                    layer[col][row] = globalGid;
                }
            }
            br.close();
            System.out.println("Map loaded with firstGid=" + tileSetFirstGid);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void drawLayer(Graphics2D g2, int[][] layer){

        // Position de la caméra dans le monde
        int cameraWorldX = gp.player.worldX - gp.player.screenX;
        int cameraWorldY = gp.player.worldY - gp.player.screenY;

        // Première tile visible
        int startCol = Math.floorDiv(cameraWorldX, gp.tileSize);
        int startRow = Math.floorDiv(cameraWorldY, gp.tileSize);

        // Nombre de tiles nécessaires, y compris la tile partiellement visible au bord.
        int visibleCols = (gp.screenWidth + Math.floorMod(cameraWorldX, gp.tileSize)
                + gp.tileSize - 1) / gp.tileSize;
        int visibleRows = (gp.screenHeight + Math.floorMod(cameraWorldY, gp.tileSize)
                + gp.tileSize - 1) / gp.tileSize;

        // Parcours des tiles visibles : col correspond à l'axe horizontal, row au vertical.
        for(int col = 0; col < visibleCols; col++){
            for(int row = 0; row < visibleRows; row++){

                int worldCol = startCol + col;
                int worldRow = startRow + row;

                // Vérification des limites du monde
                if(worldCol < 0 || worldCol >= layer.length ||
                        worldRow < 0 || worldRow >= layer[worldCol].length){
                    continue;
                }

                // Récupération de la tile
                int gid = layer[worldCol][worldRow];
                Tile tile = getTileByGID(gid);

                // Position à l'écran
                int screenX = col * gp.tileSize
                        - Math.floorMod(cameraWorldX, gp.tileSize);

                int screenY = row * gp.tileSize
                        - Math.floorMod(cameraWorldY, gp.tileSize);

                // Dessin
                if(tile != null && tile.image != null){
                    g2.drawImage(
                            tile.image,
                            screenX, screenY,
                            gp.tileSize, gp.tileSize,
                            null
                    );
                }

                // DEBUG
                if(tile == null && gid != -1){
                    g2.setColor(Color.RED);
                    g2.drawRect(screenX, screenY,
                            gp.tileSize, gp.tileSize);
                }
            }
        }
    }



    public void draw(Graphics2D g2){
        drawLayer(g2, groundLayer);
    }
}
