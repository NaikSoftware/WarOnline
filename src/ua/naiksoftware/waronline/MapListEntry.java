package ua.naiksoftware.waronline;

import android.graphics.Rect;
import java.io.DataInputStream;
import java.io.IOException;
import ua.naiksoftware.waronline.unit.Unit;

public class MapListEntry {

    private String name;
    private final DataInputStream stream;
    private final boolean removable;
    private final int w, h;
    private final String path;
    private final Unit[] noBodyUnit;
    private final Rect[] baseCoords;

    public MapListEntry(DataInputStream s, String p, boolean r) throws IOException {
        stream = s;
        path = p;
        removable = r;
        name = s.readUTF();
        w = s.readInt();
        h = s.readInt();
        baseCoords = new Rect[3];
        for (int i = 0; i < 3; i++) {
            baseCoords[i] = new Rect(s.readInt(), s.readInt(), s.readInt(), s.readInt());
        }
        int noBodyCount = s.readInt();
        noBodyUnit = new Unit[noBodyCount];
        for (int i = 0; i < noBodyCount; i++) {
            // id, x, y
            noBodyUnit[i] = new Unit(s.readInt(), s.readInt(), s.readInt());
        }
    }

    public Tile[][] getTilesMap() throws IOException {
        Tile[][] tmpMap = new Tile[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tmpMap[i][j] = new Tile(stream.readInt());
            }
        }
        return tmpMap;
    }

    public String getPath() {
        return path;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isRemovable() {
        return removable;
    }
	
	public Unit[] getFreeUnits() {
		return noBodyUnit;
	}
	
	/**
	 * @param base - base id (1, 2, or 3)
	 */
	public Rect getBaseCoords(int base) {
		return baseCoords[base - 1];
	}
}
