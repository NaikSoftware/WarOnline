package ua.naiksoftware.waronline;

import java.util.HashMap;
import android.graphics.Canvas;
import android.graphics.Bitmap;

public class Mine {

	private final int x, y;
	private final int drawX, drawY;
	private final Gamer gamer;

	private static Gamer currentGamer;
	private HashMap<Gamer, Boolean> visibilityMap = new HashMap<Gamer, Boolean>(3);

	private static final Bitmap bitmap = BitmapUtil.load(R.drawable.mine);
	private static int shift = Tile.TILE_SIZE / 2 - bitmap.getWidth() / 2;
	private boolean visible;


	public Mine(int x, int y) {
		this.gamer = currentGamer;
		this.x = x;
		this.y = y;
		drawX = x * Tile.TILE_SIZE + shift;
		drawY = y * Tile.TILE_SIZE + shift;
		visibilityMap.put(Gamer.ONE, false);
		visibilityMap.put(Gamer.TWO, false);
		visibilityMap.put(Gamer.THREE, false);
		detect();
	}

	public static void setCurrentGamer(Gamer g) {
		currentGamer = g;
	}
	
	public void onChange() {
		visible = visibilityMap.get(currentGamer);
	}

	public void detect() {
		visibilityMap.put(currentGamer, true);
		onChange();
	}

	public void draw(Canvas canvas) {
		if (visible) {
			canvas.drawBitmap(bitmap, drawX, drawY, null);
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
