package ua.naiksoftware.waronline.unit;

import android.graphics.Bitmap;
import android.util.SparseArray;
import ua.naiksoftware.waronline.BitmapUtil;
import ua.naiksoftware.waronline.R;
import ua.naiksoftware.waronline.ReflectType;
import ua.naiksoftware.waronline.Tile;
import filelog.Log;

public class Anim {

	private static final String tag = Anim.class.getSimpleName();
	
    private static final SparseArray<Bitmap[]> anims;

    static {

		anims = new SparseArray<Bitmap[]>(UnitCode.ARTILLERY + UnitCode.ID_DIED);

        Bitmap[] tmp;
		Bitmap[] died = load(R.drawable.died);

        loadOneSequenseUnit(UnitCode.ING_AVTO, R.drawable.avto_up, load(R.drawable.avto_died));

        anims.append(UnitCode.SOLDIER + UnitCode.ID_UP, load(R.drawable.soldier_up));
        anims.append(UnitCode.SOLDIER + UnitCode.ID_DOWN, load(R.drawable.soldier_down));
        tmp = load(R.drawable.soldier_left);
        anims.append(UnitCode.SOLDIER + UnitCode.ID_LEFT, tmp);
        anims.append(UnitCode.SOLDIER + UnitCode.ID_RIGHT, reflect(tmp, ReflectType.HORIZONTAL));
        tmp = load(R.drawable.soldier_left_up);
        anims.append(UnitCode.SOLDIER + UnitCode.ID_LEFT_UP, tmp);
        anims.append(UnitCode.SOLDIER + UnitCode.ID_RIGHT_UP, reflect(tmp, ReflectType.HORIZONTAL));
        tmp = load(R.drawable.soldier_left_down);
        anims.append(UnitCode.SOLDIER + UnitCode.ID_LEFT_DOWN, tmp);
        anims.append(UnitCode.SOLDIER + UnitCode.ID_RIGHT_DOWN, reflect(tmp, ReflectType.HORIZONTAL));
        anims.append(UnitCode.SOLDIER + UnitCode.ID_DIED, died);

		anims.append(UnitCode.HORSE + UnitCode.ID_UP, load(R.drawable.horse_up));
		anims.append(UnitCode.HORSE + UnitCode.ID_DOWN, load(R.drawable.horse_down));
		tmp = load(R.drawable.horse_right);
		anims.append(UnitCode.HORSE + UnitCode.ID_RIGHT, tmp);
		anims.append(UnitCode.HORSE + UnitCode.ID_LEFT, reflect(tmp, ReflectType.HORIZONTAL));
		tmp = load(R.drawable.horse_right_up);
		anims.append(UnitCode.HORSE + UnitCode.ID_RIGHT_UP, tmp);
		anims.append(UnitCode.HORSE + UnitCode.ID_LEFT_UP, reflect(tmp, ReflectType.HORIZONTAL));
		tmp = load(R.drawable.horse_right_down);
		anims.append(UnitCode.HORSE + UnitCode.ID_RIGHT_DOWN, tmp);
		anims.append(UnitCode.HORSE + UnitCode.ID_LEFT_DOWN, reflect(tmp, ReflectType.HORIZONTAL));
		anims.append(UnitCode.HORSE + UnitCode.ID_DIED, died);

		loadOneSequenseUnit(UnitCode.HOTCHKISS, R.drawable.hotchkiss_up, died);
		loadOneSequenseUnit(UnitCode.T34_85, R.drawable.t34_85_up, died);
		loadOneSequenseUnit(UnitCode.PANZER, R.drawable.panzer_up, died);
		loadOneSequenseUnit(UnitCode.TIGER, R.drawable.tiger_up, died);

		tmp = load(R.drawable.artillery_right);
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_RIGHT, tmp);
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_LEFT, reflect(tmp, ReflectType.HORIZONTAL));
		tmp = load(R.drawable.artillery_up);
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_UP, tmp);
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_DOWN, rotate(tmp, 180));
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_RIGHT_UP, rotate(tmp, 45));
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_RIGHT_DOWN, rotate(tmp, 135));
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_LEFT_DOWN, rotate(tmp, 225));
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_LEFT_UP, rotate(tmp, 315));
		anims.append(UnitCode.ARTILLERY + UnitCode.ID_DIED, died);
    }

    public static Bitmap[] get(int code) {
        return anims.get(code);
    }

	private static void loadOneSequenseUnit(int code, int id, Bitmap[] died) {
		Bitmap[] sequense = load(id);
		anims.append(code + UnitCode.ID_UP, sequense);
		anims.append(code + UnitCode.ID_RIGHT_UP, rotate(sequense, 45));
		anims.append(code + UnitCode.ID_RIGHT, rotate(sequense, 90));
		anims.append(code + UnitCode.ID_RIGHT_DOWN, rotate(sequense, 135));
		anims.append(code + UnitCode.ID_DOWN, rotate(sequense, 180));
		anims.append(code + UnitCode.ID_LEFT_DOWN, rotate(sequense, 225));
		anims.append(code + UnitCode.ID_LEFT, rotate(sequense, 270));
		anims.append(code + UnitCode.ID_LEFT_UP, rotate(sequense, 315));
		anims.append(code + UnitCode.ID_DIED, died);
	}

    private static Bitmap[] load(int id) {
        Bitmap all = BitmapUtil.load(id);
		int dx = all.getWidth() / Tile.TILE_SIZE;
		int dy = all.getHeight() / Tile.TILE_SIZE;
		Bitmap[] result = new Bitmap[dx * dy];
		for (int i = 0;i < dx;i++) {
			for (int j = 0;j < dy;j++) {
				result[i  + j] = Bitmap.createBitmap(all, i * Tile.TILE_SIZE, j * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
			}
		}
		return result;
    }

    private static Bitmap[] rotate(Bitmap[] bitmaps, int deg) {
		int len = bitmaps.length;
		Bitmap[] result = new Bitmap[len];
		for (int i = 0;i < len;i++) {
			result[i] = BitmapUtil.rotate(bitmaps[i], deg);
		}
		return result;
    }

    private static Bitmap[] reflect(Bitmap[] bitmaps, ReflectType reflectType) {
        int len = bitmaps.length;
		Bitmap[] result = new Bitmap[len];
		for (int i = 0;i < len;i++) {
			result[i] = BitmapUtil.reflect(bitmaps[i], reflectType);
		}
		return result;
    }
}
