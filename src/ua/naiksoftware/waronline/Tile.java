package ua.naiksoftware.waronline;

import android.graphics.*;

/**
 * @author Naik
 *
 * One square representation of game area.
 */
public class Tile {

    private static final String tag = Tile.class.getName();

    public static final int TILE_COUNT = TileCode.i;

    public static final int TILE_SIZE;//px

    private static final Bitmap[] tiles;

    private boolean invisibleOne, invisibleTwo, invisibleThree;
    private static final Paint paint;
    private static final Bitmap hideMask;
    private static Gamer currentGamer;
    private int code;

    static {
        paint = new Paint();
        tiles = new Bitmap[TILE_COUNT];

        tiles[TileCode.GRASS] = load(R.drawable.grass);//0

        TILE_SIZE = tiles[TileCode.GRASS].getHeight(); // все тайлы одинакового размера

        tiles[TileCode.TREES] = load(R.drawable.trees);//1

        tiles[TileCode.TREES_EDGE_DOWN] = load(R.drawable.trees_edge_down);//2
        tiles[TileCode.TREES_EDGE_RIGHT] = load(R.drawable.trees_edge_right);//3
        tiles[TileCode.TREES_EDGE_UP] = rotate(tiles[TileCode.TREES_EDGE_RIGHT], -90);//4
        tiles[TileCode.TREES_EDGE_LEFT] = rotate(tiles[TileCode.TREES_EDGE_RIGHT], -180);//5

        tiles[TileCode.TREES_CORNER_RIGHT_DOWN] = load(R.drawable.trees_corner_right_down);//6
        tiles[TileCode.TREES_CORNER_LEFT_DOWN] = reflect(tiles[TileCode.TREES_CORNER_RIGHT_DOWN], ReflectType.HORIZONTAL);//7
        tiles[TileCode.TREES_CORNER_LEFT_UP] = load(R.drawable.trees_corner_right_up);//8
        tiles[TileCode.TREES_CORNER_RIGHT_UP] = reflect(tiles[TileCode.TREES_CORNER_LEFT_UP], ReflectType.HORIZONTAL);//9

        tiles[TileCode.TREES_INCORNER_RIGHT_DOWN] = load(R.drawable.trees_incorner_right_down);//10
        tiles[TileCode.TREES_INCORNER_LEFT_DOWN] = reflect(tiles[TileCode.TREES_INCORNER_RIGHT_DOWN], ReflectType.HORIZONTAL);//11

        tiles[TileCode.ROAD_HORIZ] = load(R.drawable.road_horiz);
        tiles[TileCode.ROAD_VERT] = rotate(tiles[TileCode.ROAD_HORIZ], 90);
        tiles[TileCode.ROAD_INTERSECT] = load(R.drawable.road_intersect);
        tiles[TileCode.ROAD_CORNER_RIGHT_UP] = load(R.drawable.road_corner_right_up);
        tiles[TileCode.ROAD_CORNER_RIGHT_DOWN] = reflect(tiles[TileCode.ROAD_CORNER_RIGHT_UP], ReflectType.VERTICAL);
        tiles[TileCode.ROAD_CORNER_LEFT_DOWN] = reflect(tiles[TileCode.ROAD_CORNER_RIGHT_UP], ReflectType.COMBINE);
        tiles[TileCode.ROAD_CORNER_LEFT_UP] = reflect(tiles[TileCode.ROAD_CORNER_RIGHT_UP], ReflectType.HORIZONTAL);
        tiles[TileCode.ROAD_END_RIGHT] = load(R.drawable.road_end_right);
        tiles[TileCode.ROAD_END_DOWN] = rotate(tiles[TileCode.ROAD_END_RIGHT], 90);
        tiles[TileCode.ROAD_END_LEFT] = rotate(tiles[TileCode.ROAD_END_RIGHT], 180);
        tiles[TileCode.ROAD_END_UP] = rotate(tiles[TileCode.ROAD_END_RIGHT], 270);

        tiles[TileCode.WATER] = load(R.drawable.water);//14

        tiles[TileCode.WATER_DOWN_1] = load(R.drawable.water_down1);//15
        tiles[TileCode.WATER_DOWN_2] = load(R.drawable.water_down2);//16
        tiles[TileCode.WATER_UP_1] = reflect(tiles[TileCode.WATER_DOWN_1], ReflectType.VERTICAL);//17
        tiles[TileCode.WATER_UP_2] = reflect(tiles[TileCode.WATER_DOWN_2], ReflectType.VERTICAL);//18
        tiles[TileCode.WATER_LEFT_1] = rotate(tiles[TileCode.WATER_DOWN_1], 90);
        tiles[TileCode.WATER_LEFT_2] = rotate(tiles[TileCode.WATER_DOWN_2], 90);
        tiles[TileCode.WATER_RIGHT_1] = rotate(tiles[TileCode.WATER_DOWN_1], -90);
        tiles[TileCode.WATER_RIGHT_2] = rotate(tiles[TileCode.WATER_DOWN_2], -90);

        tiles[TileCode.WATER_CORNER_RIGHT_DOWN] = load(R.drawable.water_corner1);//19
        tiles[TileCode.WATER_CORNER_LEFT_DOWN] = reflect(tiles[TileCode.WATER_CORNER_RIGHT_DOWN], ReflectType.HORIZONTAL);//20
        tiles[TileCode.WATER_CORNER_RIGHT_UP] = load(R.drawable.water_corner2);//21
        tiles[TileCode.WATER_CORNER_LEFT_UP] = reflect(tiles[TileCode.WATER_CORNER_RIGHT_UP], ReflectType.HORIZONTAL);//22

        tiles[TileCode.WATER_INCORNER_RIGHT_UP] = load(R.drawable.water_incorner_right_up);//23
        tiles[TileCode.WATER_INCORNER_RIGHT_DOWN] = rotate(tiles[TileCode.WATER_INCORNER_RIGHT_UP], 90);//24
        tiles[TileCode.WATER_INCORNER_LEFT_DOWN] = rotate(tiles[TileCode.WATER_INCORNER_RIGHT_UP], 180);//25
        tiles[TileCode.WATER_INCORNER_LEFT_UP] = rotate(tiles[TileCode.WATER_INCORNER_RIGHT_UP], 270);//26

        tiles[TileCode.HATA_1] = load(R.drawable.hata1);
        tiles[TileCode.HATA_2] = load(R.drawable.hata2);
        tiles[TileCode.HATA_3] = load(R.drawable.hata3);
        tiles[TileCode.HATA_4] = load(R.drawable.hata4);

        tiles[TileCode.BRIDGE_VERT] = load(R.drawable.bridge);
        tiles[TileCode.BRIDGE_UP] = load(R.drawable.bridge_up);
        tiles[TileCode.BRIDGE_DOWN] = load(R.drawable.bridge_down);
        tiles[TileCode.BRIDGE_HORIZ] = rotate(tiles[TileCode.BRIDGE_VERT], 90);
        tiles[TileCode.BRIDGE_LEFT] = rotate(tiles[TileCode.BRIDGE_DOWN], 90);
        tiles[TileCode.BRIDGE_RIGHT] = rotate(tiles[TileCode.BRIDGE_DOWN], -90);

        tiles[TileCode.REDUIT_1] = load(R.drawable.reduit1);
        tiles[TileCode.REDUIT_2] = load(R.drawable.reduit2);

        hideMask = load(R.drawable.hide_mask);
    }

    /**
     * @param code - code from TileCode class.
     */
    public Tile(int code) {
        setTile(code);
    }

    public void setTile(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setVisibility(boolean v, Gamer gamer) {
        switch (gamer) {
            case ONE:
                invisibleOne = !v;
                break;
            case TWO:
                invisibleTwo = !v;
                break;
            case THREE:
                invisibleThree = !v;
        }
    }
	
	public boolean isVisible() {
		switch(currentGamer) {
			case ONE: return !invisibleOne;
			case TWO: return !invisibleTwo;
			case THREE: return !invisibleThree;
		}
		return false;// default
	}

    /**
     * Static because set gamer for all tiles (game area), not for one tile.
     */
    public static void setCurrentGamer(Gamer g) {
        currentGamer = g;
    }

    public void draw(Canvas canvas, int x, int y) {
        //Log.d(tag, "tile code=" + code);
        canvas.drawBitmap(tiles[code], x, y, paint);
        switch (currentGamer) {
            case ONE:
                if (invisibleOne) {
                    canvas.drawBitmap(hideMask, x, y, paint);
                }
                break;
            case TWO:
                if (invisibleTwo) {
                    canvas.drawBitmap(hideMask, x, y, paint);
                }
                break;
            case THREE:
                if (invisibleThree) {
                    canvas.drawBitmap(hideMask, x, y, paint);
                }
                break;
        }
    }

    public Bitmap getBitmap() {
        return tiles[code];
    }

    private static Bitmap reflect(Bitmap tile, ReflectType reflectType) {
        return BitmapUtil.reflect(tile, reflectType);
    }

    private static Bitmap load(int id) {
        return BitmapUtil.load(id);
    }

    private static Bitmap rotate(Bitmap tile, int deg) {
        return BitmapUtil.rotate(tile, deg);
    }
}
