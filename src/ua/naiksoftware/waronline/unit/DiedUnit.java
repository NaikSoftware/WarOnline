package ua.naiksoftware.waronline.unit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import ua.naiksoftware.waronline.Tile;

public class DiedUnit {
	private final int x, y;
	private final int drawx, drawy;
	private final Bitmap bitmap;
	
	public DiedUnit(int x, int y, Bitmap bitmap) {
		this.x = x;
		this.y = y;
		this.drawx = x * Tile.TILE_SIZE;
		this.drawy = y * Tile.TILE_SIZE;
		this.bitmap = bitmap;
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, drawx, drawy, null);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
