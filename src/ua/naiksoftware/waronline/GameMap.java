package ua.naiksoftware.waronline;

import android.graphics.Canvas;
import java.io.IOException;

/**
 * @author Naik
 */
public class GameMap {

    private static final String tag = GameMap.class.getName();

    private final String title;
    private Tile[][] tileMap;
    private int xTileCount, yTileCount;

    public GameMap(MapListEntry mapEntry) throws IOException {
        this.title = mapEntry.getName();
        setData(mapEntry.getTilesMap());
    }

    public void setData(Tile[][] map) {
        tileMap = map;
        xTileCount = tileMap.length;
        yTileCount = tileMap[0].length;
		for (int i = 0;i < xTileCount;i++){
			for (int j = 0;j < yTileCount;j++){
				tileMap[i][j].setVisibility(false, Gamer.ONE);
				tileMap[i][j].setVisibility(false, Gamer.TWO);
				tileMap[i][j].setVisibility(false, Gamer.THREE);
			}
		}
    }

    public int getXTileCount() {
        return xTileCount;
    }

    public int getYTileCount() {
        return yTileCount;
    }

    /*
     * Set tile visibility for each gamer.
     */
    public void setVisible(int x, int y, Gamer gamer) {
        tileMap[x][y].setVisibility(true, gamer);
    }
	
	public boolean isVisible(int x, int y) {
		return tileMap[x][y].isVisible();
	}

    /**
     * Always call before start drawing game area.
     * @param gamer current gamer
     */
    public void setCurrentGamer(Gamer gamer) {
        Tile.setCurrentGamer(gamer);
    }
	
	public int getTileCode(int x, int y) {
		return tileMap[x][y].getCode();
	}

    public void draw(Canvas canvas, int startX, int startY, int endX, int endY) {
        int drawX = startX * Tile.TILE_SIZE, drawY;
        for (int i = startX; i < endX; i++) {
            drawY = startY * Tile.TILE_SIZE;
            for (int j = startY; j < endY; j++) {
                tileMap[i][j].draw(canvas, drawX, drawY);
                drawY += Tile.TILE_SIZE;
            }
            drawX += Tile.TILE_SIZE;
        }
    }
}
