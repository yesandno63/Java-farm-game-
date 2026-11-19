package tile;

import java.util.ArrayList;

public class TileSet {
    public int firstGID;
    public ArrayList<Tile> tiles;

    public TileSet(int firstGID, ArrayList<Tile> tiles) {
        this.firstGID = firstGID;
        this.tiles = tiles;
    }
}
